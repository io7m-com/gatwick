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

import com.io7m.gatwick.controller.main.GWControllers;
import com.io7m.gatwick.device.api.GWDeviceConfiguration;
import com.io7m.gatwick.device.api.GWDeviceMIDIDescription;
import com.io7m.gatwick.device.javamidi.GWDevicesJavaMIDI;
import com.io7m.taskrecorder.core.TRTaskRecorder;
import com.io7m.taskrecorder.core.TRTaskSucceeded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

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
      new GWControllers();

    final var detectTask =
      devices.detectDevices(TRTaskRecorder.create(LOG, "Detecting..."));

    final var success =
      (TRTaskSucceeded<List<GWDeviceMIDIDescription>>) detectTask.resolution();

    final var deviceConfiguration =
      new GWDeviceConfiguration(
        success.result().get(0),
        Duration.ofSeconds(5L),
        Duration.ofSeconds(1L),
        3,
        Duration.ofMillis(10L)
      );

    try (var controller = controllers.openController(devices, deviceConfiguration)) {
      final var patch = controller.patchCurrent();
      patch.fx1()
        .harmonizer()
        .variables()
        .get(4)
        .readFromDevice();
    }
  }
}
