/*
 * Copyright © 2022 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */


package com.io7m.gatwick.gui.internal.debug;

import com.io7m.gatwick.preferences.GWPreferencesDebug;
import com.io7m.jmulticlose.core.CloseableCollection;
import com.io7m.jmulticlose.core.CloseableCollectionType;
import com.io7m.jmulticlose.core.ClosingResourceFailedException;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import org.apache.commons.text.StringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * The debug server.
 */

public final class GWDebugServer implements AutoCloseable
{
  private static final Logger LOG =
    LoggerFactory.getLogger(GWDebugService.class);

  private final CloseableCollectionType<ClosingResourceFailedException> resources;
  private final AtomicBoolean close;
  private final ExecutorService executor;
  private final GWDCommands commands;

  /**
   * Create a debug server.
   *
   * @param services The service directory
   * @param debug    The debug preferences
   *
   * @return The server
   */

  public static GWDebugServer create(
    final RPServiceDirectoryType services,
    final GWPreferencesDebug debug)
  {
    LOG.debug("starting debug server on port {}", debug.debugPort());

    final var resources =
      CloseableCollection.create();

    final var close =
      new AtomicBoolean(false);

    final var executor =
      Executors.newCachedThreadPool(r -> {
        final var th = new Thread(r);
        th.setDaemon(true);
        th.setName("com.io7m.gatwick.gui.internal.debug.GWDebugServer[%d]"
                     .formatted(th.getId()));
        return th;
      });

    resources.add(() -> close.set(true));
    resources.add(executor::shutdown);

    final var commands =
      new GWDCommands(services);

    final var server =
      new GWDebugServer(resources, close, executor, commands);

    executor.execute(() -> server.runDebugServer(debug));
    return server;
  }

  private GWDebugServer(
    final CloseableCollectionType<ClosingResourceFailedException> inResources,
    final AtomicBoolean inClose,
    final ExecutorService inExecutor,
    final GWDCommands inCommands)
  {
    this.resources =
      Objects.requireNonNull(inResources, "resources");
    this.close =
      Objects.requireNonNull(inClose, "close");
    this.executor =
      Objects.requireNonNull(inExecutor, "executor");
    this.commands =
      Objects.requireNonNull(inCommands, "commands");
  }

  private void runDebugServer(
    final GWPreferencesDebug debug)
  {
    while (this.continueRunning()) {
      try {
        final var socket =
          this.resources.add(new ServerSocket());
        final var bindAddress =
          new InetSocketAddress("localhost", debug.debugPort());

        socket.bind(bindAddress);

        while (this.continueRunning()) {
          final var clientSocket = socket.accept();
          this.executor.execute(() -> this.runDebugClient(clientSocket));
        }
      } catch (final IOException e) {
        LOG.error("failed to open socket: ", e);
        try {
          Thread.sleep(2_000L);
        } catch (final InterruptedException ex) {
          Thread.currentThread().interrupt();
        }
      }
    }
  }

  private void runDebugClient(
    final Socket socket)
  {
    try {
      try (var clientResources = CloseableCollection.create()) {
        final var inputReader =
          clientResources.add(new InputStreamReader(
            socket.getInputStream(),
            UTF_8)
          );

        final var bufferedReader =
          clientResources.add(new BufferedReader(inputReader));
        final var outputWriter =
          clientResources.add(new OutputStreamWriter(
            socket.getOutputStream(),
            UTF_8));
        final var bufferedWriter =
          clientResources.add(new BufferedWriter(outputWriter));

        while (this.continueRunning()) {
          final var line =
            bufferedReader.readLine();

          if (line == null) {
            LOG.debug("[{}] disconnected", socket.getInetAddress());
            return;
          }

          final var token =
            new StringTokenizer(line, ' ', '"');
          final var parts =
            new ArrayList<>(token.getTokenList());

          if (parts.isEmpty()) {
            continue;
          }

          final var name =
            parts.remove(0);

          try {
            this.commands.createCommand(name, parts)
              .execute(bufferedWriter);
          } catch (final Exception e) {
            LOG.error("client exception: ", e);
          }
        }
      }
    } catch (final Exception e) {
      LOG.error("client exception: ", e);
    } finally {
      try {
        socket.close();
      } catch (final IOException e) {
        LOG.error("failed to close socket: ", e);
      }
    }
  }

  private boolean continueRunning()
  {
    return !this.close.get();
  }

  @Override
  public void close()
    throws Exception
  {
    this.resources.close();
  }
}
