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


package com.io7m.gatwick.device.javamidi;

import com.io7m.gatwick.device.api.GWDeviceConfiguration;
import com.io7m.gatwick.device.api.GWDeviceException;
import com.io7m.gatwick.device.api.GWDeviceFactoryType;
import com.io7m.gatwick.device.api.GWDeviceType;
import com.io7m.gatwick.device.javamidi.internal.GWDeviceJavaMIDI;
import com.io7m.jdeferthrow.core.ExceptionTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import java.util.Objects;

import static com.io7m.gatwick.device.api.GWDeviceStandardErrorCodes.DEVICE_MIDI_SYSTEM_ERROR;
import static com.io7m.gatwick.device.api.GWDeviceStandardErrorCodes.DEVICE_NOT_FOUND;

/**
 * The JavaMIDI device implementation.
 */

public final class GWDevicesJavaMIDI
  implements GWDeviceFactoryType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(GWDevicesJavaMIDI.class);

  private final GWDevicesJavaMIDIDevicesType backend;

  /**
   * The JavaMIDI device implementation.
   */

  public GWDevicesJavaMIDI()
  {
    this(new GWDevicesJavaMIDIDevices());
  }

  /**
   * The JavaMIDI device implementation.
   *
   * @param inBackend The backend
   */

  public GWDevicesJavaMIDI(
    final GWDevicesJavaMIDIDevicesType inBackend)
  {
    this.backend =
      Objects.requireNonNull(inBackend, "backend");
  }

  @Override
  public GWDeviceType openDevice(
    final GWDeviceConfiguration configuration)
    throws GWDeviceException
  {
    final var exceptions =
      new ExceptionTracker<GWDeviceException>();
    final var deviceInfos =
      this.backend.getMidiDeviceInfo();
    final var namePattern =
      configuration.namePattern();

    MidiDevice receiver = null;
    MidiDevice transmitter = null;

    for (final var deviceInfo : deviceInfos) {
      if (receiver != null && transmitter != null) {
        break;
      }

      final var name = deviceInfo.getName();
      final var matcher = namePattern.matcher(name);

      if (matcher.matches()) {
        LOG.trace("device {} matches", name);

        try {
          final var device =
            this.backend.getMidiDevice(deviceInfo);

          if (receiver == null) {
            final var maxReceivers = device.getMaxReceivers();
            LOG.trace("[{}] max receivers {}", name, maxReceivers);
            if (maxReceivers != 0) {
              LOG.trace("[{}] selected as receiver", name);
              receiver = device;
            }
          }

          if (transmitter == null) {
            final var maxTransmitters = device.getMaxTransmitters();
            LOG.trace("[{}] max transmitters {}", name, maxTransmitters);
            if (maxTransmitters != 0) {
              LOG.trace("[{}] selected as transmitter", name);
              transmitter = device;
            }
          }
        } catch (final MidiUnavailableException e) {
          exceptions.addException(
            new GWDeviceException(
              DEVICE_MIDI_SYSTEM_ERROR,
              e.getMessage(),
              e)
          );
        }
      }
    }

    exceptions.throwIfNecessary();

    if (receiver != null && transmitter != null) {
      return GWDeviceJavaMIDI.open(configuration, receiver, transmitter);
    }

    throw new GWDeviceException(
      DEVICE_NOT_FOUND,
      "No usable devices available matching '%s'".formatted(namePattern)
    );
  }
}
