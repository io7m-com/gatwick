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

package com.io7m.gatwick.device.api;

import com.io7m.taskrecorder.core.TRTask;

import java.util.List;

/**
 * A provider of devices.
 */

public interface GWDeviceFactoryType
{
  /**
   * Open a device.
   *
   * @param configuration The device configuration
   *
   * @return A device
   *
   * @throws GWDeviceException On errors
   */

  GWDeviceType openDevice(
    GWDeviceConfiguration configuration)
    throws GWDeviceException;

  /**
   * Detect devices that appear to be GT-1000 devices.
   *
   * @return A list of probable GT-1000 devices
   */

  TRTask<List<GWDeviceMIDIDescription>> detectDevices();

  /**
   * @return A list of the system's MIDI devices
   *
   * @throws GWDeviceException On errors
   */

  List<GWDeviceMIDIDescription> listMIDIDevices()
    throws GWDeviceException;
}
