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

import java.time.Duration;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Device configuration information.
 *
 * @param namePattern           Only try to open MIDI devices with a name
 *                              matching this pattern
 * @param openTimeout           The maximum length of time to wait until a
 *                              device is open
 * @param messageTimeout        The maximum length of time to wait for a command
 *                              response
 * @param messageSendTries      The maximum number of times to try sending a
 *                              message
 * @param messageSendRetryPause The duration to pause before retrying a message
 *                              send
 */

public record GWDeviceConfiguration(
  Pattern namePattern,
  Duration openTimeout,
  Duration messageTimeout,
  int messageSendTries,
  Duration messageSendRetryPause)
{
  /**
   * Device configuration information.
   *
   * @param namePattern           Only try to open MIDI devices with a name
   *                              matching this pattern
   * @param openTimeout           The maximum length of time to wait until a
   *                              device is open
   * @param messageTimeout        The maximum length of time to wait for a
   *                              command response
   * @param messageSendTries      The maximum number of times to try sending a
   *                              message
   * @param messageSendRetryPause The duration to pause before retrying a
   *                              message send
   */

  public GWDeviceConfiguration
  {
    Objects.requireNonNull(namePattern, "namePattern");
    Objects.requireNonNull(openTimeout, "openTimeout");
    Objects.requireNonNull(messageTimeout, "messageTimeout");
    Objects.requireNonNull(messageSendRetryPause, "messageSendRetryPause");
  }
}
