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

import com.io7m.taskrecorder.core.TRTask;

import java.util.Objects;

/**
 * The status of the GT-1000.
 */

public sealed interface GWGT1KServiceStatusType
{
  /**
   * @return {@code true} if the status implies a device can be closed
   */

  boolean impliesCloseable();

  /**
   * The GT-1000 is not currently connected.
   */

  enum Disconnected implements GWGT1KServiceStatusType
  {
    /**
     * The GT-1000 is not currently connected.
     */

    DISCONNECTED;

    @Override
    public boolean impliesCloseable()
    {
      return false;
    }
  }

  /**
   * An attempt to connect to the GT-1000 failed.
   *
   * @param task The task
   */

  record OpenFailed(
    TRTask<?> task)
    implements GWGT1KServiceStatusType
  {
    /**
     * An attempt to connect to the GT-1000 failed.
     */

    public OpenFailed
    {
      Objects.requireNonNull(task, "task");
    }

    @Override
    public boolean impliesCloseable()
    {
      return false;
    }
  }

  /**
   * The GT-1000 is currently connected.
   */

  enum Connected implements GWGT1KServiceStatusType
  {
    /**
     * The GT-1000 is currently connected.
     */

    CONNECTED;

    @Override
    public boolean impliesCloseable()
    {
      return true;
    }
  }
}
