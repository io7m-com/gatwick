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

import java.util.Objects;

/**
 * A description of a device.
 *
 * @param midiDevice             The underlying MIDI device
 * @param deviceManufacturer     The device manufacturer code (0x41 for Roland)
 * @param deviceFamilyCode       The device family code (0x4f03 for the
 *                               GT-1000*)
 * @param deviceFamilyNumberCode The device family number code (typically 0)
 * @param deviceSoftwareVersion  The device software version
 */

public record GWDeviceDescription(
  GWDeviceMIDIDescription midiDevice,
  int deviceManufacturer,
  int deviceFamilyCode,
  int deviceFamilyNumberCode,
  int deviceSoftwareVersion)
{
  /**
   * A description of a device.
   *
   * @param midiDevice             The underlying MIDI device
   * @param deviceManufacturer     The device manufacturer code (0x41 for
   *                               Roland)
   * @param deviceFamilyCode       The device family code (0x4f03 for the
   *                               GT-1000*)
   * @param deviceFamilyNumberCode The device family number code (typically 0)
   * @param deviceSoftwareVersion  The device software version
   */

  public GWDeviceDescription
  {
    Objects.requireNonNull(midiDevice, "midiDevice");
  }
}
