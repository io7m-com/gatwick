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


package com.io7m.gatwick.gui.internal.gt;

import com.io7m.gatwick.controller.api.GWControllerConfiguration;
import com.io7m.gatwick.controller.api.GWControllerFactoryType;
import com.io7m.gatwick.controller.api.GWControllerType;
import com.io7m.gatwick.device.api.GWDeviceFactoryType;
import com.io7m.gatwick.device.api.GWDeviceMIDIDescription;
import com.io7m.gatwick.gui.internal.GWStrings;
import com.io7m.gatwick.gui.internal.gt.GWGT1KServiceStatusType.Connected;
import com.io7m.gatwick.gui.internal.gt.GWGT1KServiceStatusType.OpenFailed;
import com.io7m.gatwick.gui.internal.gt.GWGT1KServiceStatusType.PerformingIO;
import com.io7m.jmulticlose.core.CloseableCollection;
import com.io7m.jmulticlose.core.CloseableCollectionType;
import com.io7m.jmulticlose.core.ClosingResourceFailedException;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import com.io7m.taskrecorder.core.TRTask;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

import static com.io7m.gatwick.gui.internal.gt.GWGT1KServiceStatusType.Disconnected.DISCONNECTED;

/**
 * The GT-1000 service.
 */

public final class GWGT1KService implements GWGT1KServiceType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(GWGT1KService.class);

  private final CloseableCollectionType<ClosingResourceFailedException> resources;
  private final ExecutorService executor;
  private final GWStrings strings;
  private final SimpleObjectProperty<GWGT1KServiceStatusType> status;
  private volatile GWControllerType controller;

  private GWGT1KService(
    final CloseableCollectionType<ClosingResourceFailedException> inResources,
    final ExecutorService inExecutor,
    final GWStrings inStrings)
  {
    this.resources =
      Objects.requireNonNull(inResources, "resources");
    this.executor =
      Objects.requireNonNull(inExecutor, "executor");
    this.strings =
      Objects.requireNonNull(inStrings, "strings");
    this.status =
      new SimpleObjectProperty<>(DISCONNECTED);
  }

  /**
   * The GT-1000 service.
   *
   * @param services The service directory
   *
   * @return The service
   */

  public static GWGT1KServiceType create(
    final RPServiceDirectoryType services)
  {
    final var strings =
      services.requireService(GWStrings.class);

    final var executor =
      Executors.newSingleThreadExecutor(r -> {
        final var thread = new Thread(r);
        thread.setDaemon(true);
        thread.setName(
          "com.io7m.gatwick.gui.internal.gt.GWGT1KService[%d]"
            .formatted(Long.valueOf(thread.getId()))
        );
        return thread;
      });

    final var resources =
      CloseableCollection.create();

    resources.add(executor::shutdown);
    return new GWGT1KService(resources, executor, strings);
  }

  @Override
  public String description()
  {
    return "GT-1000 service.";
  }

  @Override
  public String toString()
  {
    return String.format("[GWGT1KService 0x%08x]", this.hashCode());
  }

  @Override
  public void close()
    throws Exception
  {
    this.resources.close();
  }

  @Override
  public ReadOnlyProperty<GWGT1KServiceStatusType> status()
  {
    return this.status;
  }

  @Override
  public CompletableFuture<TRTask<?>> open(
    final GWControllerConfiguration configuration)
  {
    Objects.requireNonNull(configuration, "configuration");

    final var future =
      new CompletableFuture<TRTask<?>>();

    this.executor.execute(() -> {
      final var task =
        TRTask.create(LOG, this.strings.format("task.openDevice"));

      try {
        final var controllers =
          findControllers();

        final var lastController = this.controller;
        if (lastController != null) {
          lastController.close();
        }

        this.controller =
          this.resources.add(controllers.openController(configuration));

        task.setResult(this.controller);
        task.setSucceeded();
        Platform.runLater(() -> this.status.set(new Connected(this.controller)));

        future.complete(task);
      } catch (final Throwable e) {
        task.setFailed(e.getMessage(), e);
        Platform.runLater(() -> this.status.set(new OpenFailed(task)));
        future.completeExceptionally(e);
      }
    });
    return future;
  }

  private static GWControllerFactoryType findControllers()
  {
    return ServiceLoader.load(GWControllerFactoryType.class)
      .findFirst()
      .orElseThrow(() -> {
        throw new ServiceConfigurationError(
          "No services available of type %s"
            .formatted(GWControllerFactoryType.class)
        );
      });
  }

  @Override
  public CompletableFuture<TRTask<List<GWDeviceMIDIDescription>>> detectDevices(
    final Predicate<GWDeviceFactoryType> deviceFactoryFilter)
  {
    Objects.requireNonNull(deviceFactoryFilter, "deviceFactoryFilter");

    final var future =
      new CompletableFuture<TRTask<List<GWDeviceMIDIDescription>>>();

    this.executor.execute(() -> {
      final var task =
        TRTask.<List<GWDeviceMIDIDescription>>create(
          LOG, this.strings.format("task.listDevices"));

      try {
        final var controllers = findControllers();
        future.complete(controllers.detectDevices(deviceFactoryFilter));
      } catch (final Throwable e) {
        task.setFailed(e.getMessage(), e);
        future.completeExceptionally(e);
      }
    });
    return future;
  }

  @Override
  public CompletableFuture<?> executeOnDevice(
    final GWGT1KRunnableType runnable)
  {
    Objects.requireNonNull(runnable, "runnable");

    final var future = new CompletableFuture<>();
    this.executor.execute(() -> {
      try {
        final var ctrl = this.controller;
        if (ctrl == null) {
          future.completeExceptionally(
            new IllegalStateException("Device is not open."));
          return;
        }

        Platform.runLater(() -> this.status.set(new PerformingIO(this.controller)));
        runnable.execute(ctrl);
        future.complete(null);
        Platform.runLater(() -> this.status.set(new Connected(this.controller)));
      } catch (final Throwable e) {
        future.completeExceptionally(e);
      }
    });

    return future;
  }
}
