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

package com.io7m.gatwick.tests.device.javamidi;

import com.io7m.gatwick.device.api.GWDeviceCommandRequestData;
import com.io7m.gatwick.device.api.GWDeviceCommandSetData;
import com.io7m.gatwick.device.api.GWDeviceConfiguration;
import com.io7m.gatwick.device.javamidi.GWDevicesJavaMIDI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HexFormat;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.US_ASCII;

public final class GWDeviceDemo
{
  private static final Logger LOG =
    LoggerFactory.getLogger(GWDeviceDemo.class);

  private GWDeviceDemo()
  {

  }

  public static void main(
    final String[] args)
    throws Exception
  {
    final var devices =
      new GWDevicesJavaMIDI();
    final var configuration =
      new GWDeviceConfiguration(
        Pattern.compile("GT1000 \\[.*\\]"),
        Duration.ofSeconds(5L),
        Duration.ofSeconds(1L)
      );

    try (var device = devices.openDevice(configuration)) {
      LOG.debug("opened {}", device);
      final var description = device.description();
      LOG.debug(
        "name:          {}",
        description.midiDeviceName()
      );
      LOG.debug(
        "vendor:        {}",
        description.midiDeviceVendor()
      );
      LOG.debug(
        "description:   {}",
        description.midiDeviceDescription()
      );
      LOG.debug(
        "version:       {}",
        description.midiDeviceVersion()
      );
      LOG.debug(
        "manufacturer:  0x{}",
        Integer.toUnsignedString(description.deviceManufacturer(), 16)
      );
      LOG.debug(
        "family:        0x{}",
        Integer.toUnsignedString(description.deviceFamilyCode(), 16)
      );
      LOG.debug(
        "family number: 0x{}",
        Integer.toUnsignedString(description.deviceFamilyNumberCode(), 16)
      );
      LOG.debug(
        "firmware:      0x{}",
        Integer.toUnsignedString(description.deviceSoftwareVersion(), 16)
      );

      {
        final var address = 0x1000_0000;
        final var response =
          device.sendCommand(
            new GWDeviceCommandRequestData(address, 16)
          );

        LOG.debug(
          "[0x{}] {} {} (0x{}) ('{}')",
          String.format("%08x", Integer.valueOf(address)),
          Integer.valueOf(response.data().length),
          HexFormat.of().formatHex(response.data()),
          String.format("%02x", Integer.valueOf(response.checksum())),
          new String(response.data(), US_ASCII)
        );
      }

      device.sendCommand(new GWDeviceCommandSetData(
        0x1000_0000,
        new byte[] { 'D' }
      ));

      {
        final var address = 0x1000_0000;
        final var response =
          device.sendCommand(
            new GWDeviceCommandRequestData(address, 16)
          );

        LOG.debug(
          "[0x{}] {} {} (0x{}) ('{}')",
          String.format("%08x", Integer.valueOf(address)),
          Integer.valueOf(response.data().length),
          HexFormat.of().formatHex(response.data()),
          String.format("%02x", Integer.valueOf(response.checksum())),
          new String(response.data(), US_ASCII)
        );
      }
    }
  }
}
