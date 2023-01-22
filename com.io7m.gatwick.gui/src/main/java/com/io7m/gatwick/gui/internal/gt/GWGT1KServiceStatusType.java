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

import com.io7m.gatwick.controller.api.GWControllerType;
import com.io7m.taskrecorder.core.TRTask;

import java.util.Objects;

/**
 * The status of the GT-1000.
 */

public sealed interface GWGT1KServiceStatusType
{
  /**
   * The category of status types that imply a device is closed.
   */

  sealed interface GWGT1KServiceStatusClosedType
    extends GWGT1KServiceStatusType
  {
    @Override
    default boolean isOpen()
    {
      return false;
    }
  }

  /**
   * The category of status types that imply a device is open.
   */

  sealed interface GWGT1KServiceStatusOpenType
    extends GWGT1KServiceStatusType
  {
    @Override
    default boolean isOpen()
    {
      return true;
    }

    /**
     * @return A reference to the open device
     */

    GWControllerType device();
  }

  /**
   * @return {@code true} if the status implies a device is open
   */

  boolean isOpen();

  /**
   * The GT-1000 is not currently connected.
   */

  enum Disconnected implements GWGT1KServiceStatusClosedType
  {
    /**
     * The GT-1000 is not currently connected.
     */

    DISCONNECTED
  }

  /**
   * An attempt to connect to the GT-1000 failed.
   *
   * @param task The task
   */

  record OpenFailed(
    TRTask<?> task)
    implements GWGT1KServiceStatusClosedType
  {
    /**
     * An attempt to connect to the GT-1000 failed.
     */

    public OpenFailed
    {
      Objects.requireNonNull(task, "task");
    }
  }

  /**
   * The GT-1000 is currently connected.
   *
   * @param device The device
   */

  record Connected(GWControllerType device)
    implements GWGT1KServiceStatusOpenType
  {
    /**
     * The GT-1000 is currently connected.
     */

    public Connected
    {
      Objects.requireNonNull(device, "device");
    }
  }

  /**
   * The GT-1000 is currently performing I/O.
   *
   * @param device The device
   */

  record PerformingIO(GWControllerType device)
    implements GWGT1KServiceStatusOpenType
  {
    /**
     * The GT-1000 is currently performing I/O.
     */

    public PerformingIO
    {
      Objects.requireNonNull(device, "device");
    }
  }

  /**
   * There was some kind of device-related error.
   *
   * @param device The device
   * @param ex     The error
   */

  record DeviceError(GWControllerType device, Throwable ex)
    implements GWGT1KServiceStatusOpenType
  {
    /**
     * There was some kind of device-related error.
     */

    public DeviceError
    {
      Objects.requireNonNull(device, "device");
      Objects.requireNonNull(ex, "ex");
    }
  }
}
