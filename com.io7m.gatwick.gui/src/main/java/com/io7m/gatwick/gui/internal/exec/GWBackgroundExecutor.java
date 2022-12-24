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


package com.io7m.gatwick.gui.internal.exec;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * A scheduled executor for background UI tasks.
 */

public final class GWBackgroundExecutor implements GWBackgroundExecutorType
{
  private final ScheduledExecutorService executor;

  private GWBackgroundExecutor(
    final ScheduledExecutorService inExecutor)
  {
    this.executor =
      Objects.requireNonNull(inExecutor, "executor");
  }

  /**
   * A scheduled executor for background UI tasks.
   *
   * @return The service
   */

  public static GWBackgroundExecutorType create()
  {
    final var executor =
      Executors.newSingleThreadScheduledExecutor(r -> {
        final var thread = new Thread(r);
        thread.setName(
          "com.io7m.gatwick.gui.internal.exec.GWBackgroundExecutor[%d]"
            .formatted(thread.getId())
        );
        thread.setDaemon(true);
        return thread;
      });

    return new GWBackgroundExecutor(executor);
  }

  @Override
  public String toString()
  {
    return String.format("[GWBackgroundExecutor 0x%08x]", this.hashCode());
  }

  @Override
  public String description()
  {
    return "Background executor service.";
  }

  @Override
  public ScheduledExecutorService executor()
  {
    return this.executor;
  }

  @Override
  public void close()
  {
    this.executor.shutdown();
  }
}
