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

/**
 * Standard error codes.
 */

public final class GWDeviceStandardErrorCodes
{
  /**
   * A MIDI message was of an unexpected type.
   */

  public static final GWDeviceErrorCode DEVICE_MIDI_MESSAGE_UNEXPECTED_TYPE =
    new GWDeviceErrorCode("device-midi-message-unexpected-type");

  /**
   * A MIDI message was invalid.
   */

  public static final GWDeviceErrorCode DEVICE_MIDI_MESSAGE_INVALID =
    new GWDeviceErrorCode("device-midi-message-invalid");

  /**
   * A MIDI message had the wrong category.
   */

  public static final GWDeviceErrorCode DEVICE_WRONG_MESSAGE_CATEGORY =
    new GWDeviceErrorCode("device-midi-message-wrong-category");

  /**
   * A MIDI message had the wrong type.
   */

  public static final GWDeviceErrorCode DEVICE_WRONG_MESSAGE_TYPE =
    new GWDeviceErrorCode("device-midi-message-wrong-type");

  /**
   * A MIDI message had the wrong manufacturer.
   */

  public static final GWDeviceErrorCode DEVICE_WRONG_MANUFACTURER =
    new GWDeviceErrorCode("device-midi-message-wrong-manufacturer");

  /**
   * No suitable devices found.
   */

  public static final GWDeviceErrorCode DEVICE_NOT_FOUND =
    new GWDeviceErrorCode("device-not-found");

  /**
   * The underlying MIDI system raised some kind of error.
   */

  public static final GWDeviceErrorCode DEVICE_MIDI_SYSTEM_ERROR =
    new GWDeviceErrorCode("device-midi-system-error");

  /**
   * The underlying MIDI device timed out.
   */

  public static final GWDeviceErrorCode DEVICE_TIMED_OUT =
    new GWDeviceErrorCode("device-timed-out");

  private GWDeviceStandardErrorCodes()
  {

  }
}
