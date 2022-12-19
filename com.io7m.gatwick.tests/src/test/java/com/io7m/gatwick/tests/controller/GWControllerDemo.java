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

package com.io7m.gatwick.tests.controller;

import com.io7m.gatwick.controller.api.GWControllerConfiguration;
import com.io7m.gatwick.controller.api.GWOnOffValue;
import com.io7m.gatwick.controller.main.GWControllers;
import com.io7m.gatwick.device.api.GWDeviceConfiguration;
import com.io7m.gatwick.device.javamidi.GWDevicesJavaMIDI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.regex.Pattern;

import static com.io7m.gatwick.controller.api.GWOnOffValue.ON;

public final class GWControllerDemo
{
  private static final Logger LOG =
    LoggerFactory.getLogger(GWControllerDemo.class);

  private GWControllerDemo()
  {

  }

  public static void main(
    final String[] args)
    throws Exception
  {
    final var devices =
      new GWDevicesJavaMIDI();
    final var controllers =
      new GWControllers(devices);

    final var deviceConfiguration =
      new GWDeviceConfiguration(
        Pattern.compile("GT1000 \\[.*\\]"),
        Duration.ofSeconds(5L),
        Duration.ofSeconds(1L)
      );

    final var configuration =
      new GWControllerConfiguration(deviceConfiguration);

    try (var controller = controllers.openController(configuration)) {
      final var patch = controller.patchCurrent();
      patch.cmp().readFromDevice();
      patch.cmp().enabled().set(ON);
    }
  }
}
