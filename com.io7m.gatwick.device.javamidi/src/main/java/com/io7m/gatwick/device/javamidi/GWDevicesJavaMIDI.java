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
import com.io7m.gatwick.device.api.GWDeviceMIDIDescription;
import com.io7m.gatwick.device.api.GWDeviceType;
import com.io7m.gatwick.device.javamidi.internal.GWDeviceJavaMIDI;
import com.io7m.jdeferthrow.core.ExceptionTracker;
import com.io7m.taskrecorder.core.TRTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

  private static GWDeviceMIDIDescription midiInfoToDeviceDescription(
    final MidiDevice.Info info)
  {
    return new GWDeviceMIDIDescription(
      info.getName(),
      info.getDescription(),
      info.getVendor(),
      info.getVersion()
    );
  }

  private static List<GWDeviceMIDIDescription> detectDevicesCandidatesOpen(
    final TRTask<List<GWDeviceMIDIDescription>> task,
    final HashMap<String, PotentialDevice> candidates)
  {
    /*
     * Try opening each candidate device.
     */

    final var detectedDevices =
      new ArrayList<GWDeviceMIDIDescription>(candidates.size());

    task.beginStep(
      "Checking %d candidate devices.".formatted(candidates.size()));

    for (final var entry : candidates.entrySet()) {
      final var name = entry.getKey();
      final var subTask =
        task.beginSubtask("Opening device '%s'".formatted(name));

      final var value = entry.getValue();
      try {
        if (value.transmitter == null || value.receiver == null) {
          subTask.setFailed(
            "Device is missing a transmitter or receiver, and so is unsuitable."
          );
          continue;
        }

        try (var device = GWDeviceJavaMIDI.open(
          new GWDeviceConfiguration(
            value.description,
            Duration.ofSeconds(3L),
            Duration.ofSeconds(3L),
            3,
            Duration.ofMillis(100L)
          ),
          value.receiver,
          value.transmitter
        )) {
          subTask.setSucceeded(
            "Device '%s' has been detected as a GT-1000 device.".formatted(name)
          );
          detectedDevices.add(device.description().midiDevice());
        } catch (final GWDeviceException e) {
          subTask.setFailed(
            "Device '%s' failed with an exception.".formatted(name),
            Optional.of(e)
          );
        }
      } finally {
        value.close();
      }
    }

    return detectedDevices;
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

    MidiDevice receiver = null;
    MidiDevice transmitter = null;

    final var infoMatching =
      Arrays.stream(deviceInfos)
        .filter(i -> midiInfoToDeviceDescription(i).equals(configuration.device()))
        .toList();

    for (final var deviceInfo : infoMatching) {
      try {
        final var name =
          deviceInfo.getName();
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

    exceptions.throwIfNecessary();

    if (receiver != null && transmitter != null) {
      return GWDeviceJavaMIDI.open(configuration, receiver, transmitter);
    }

    throw new GWDeviceException(
      DEVICE_NOT_FOUND,
      "No usable devices available matching '%s'"
        .formatted(configuration.device().midiDeviceName())
    );
  }

  @Override
  public TRTask<List<GWDeviceMIDIDescription>> detectDevices()
  {
    final var task =
      TRTask.<List<GWDeviceMIDIDescription>>create(LOG, "Detecting devices...");

    final var deviceInfos =
      this.backend.getMidiDeviceInfo();

    final var candidates =
      this.detectDevicesCandidates(task, deviceInfos);
    final var detectedDevices =
      detectDevicesCandidatesOpen(task, candidates);

    final var subTask =
      task.beginSubtask("Return devices.");

    if (detectedDevices.isEmpty()) {
      subTask.setFailed("No suitable devices could be detected.");
    }

    task.setResult(List.copyOf(detectedDevices));
    return task;
  }

  private HashMap<String, PotentialDevice> detectDevicesCandidates(
    final TRTask<List<GWDeviceMIDIDescription>> task,
    final MidiDevice.Info[] deviceInfos)
  {
    final var candidates =
      new HashMap<String, PotentialDevice>();

    /*
     * Get a list of all devices that have receivers and transmitters.
     */

    for (final var deviceInfo : deviceInfos) {
      final var description =
        midiInfoToDeviceDescription(deviceInfo);

      final var name =
        deviceInfo.getName();
      final var subTask =
        task.beginSubtask(
          "Checking if device '%s' has the required transmitters and receivers."
            .formatted(name));

      final MidiDevice device;
      try {
        device = this.backend.getMidiDevice(deviceInfo);
      } catch (final MidiUnavailableException e) {
        subTask.setFailed(
          "MIDI device '%s' threw an exception.".formatted(name),
          Optional.of(e)
        );
        continue;
      }

      final var maxReceivers = device.getMaxReceivers();
      if (maxReceivers != 0) {
        subTask.setSucceeded(
          "Device has %s receivers and so is considered a candidate device."
            .formatted(maxReceivers == -1
                         ? "unlimited"
                         : Integer.toUnsignedString(maxReceivers))
        );

        var existing = candidates.get(name);
        if (existing == null) {
          existing = new PotentialDevice();
        }
        existing.description = description;
        existing.receiver = device;
        candidates.put(name, existing);
      }

      final var maxTransmitters = device.getMaxTransmitters();
      if (maxTransmitters != 0) {
        subTask.setSucceeded(
          "Device has %s transmitters and so is considered a candidate device."
            .formatted(maxTransmitters == -1
                         ? "unlimited"
                         : Integer.toUnsignedString(maxTransmitters))
        );

        var existing = candidates.get(name);
        if (existing == null) {
          existing = new PotentialDevice();
        }
        existing.description = description;
        existing.transmitter = device;
        candidates.put(name, existing);
      }
    }

    return candidates;
  }

  @Override
  public List<GWDeviceMIDIDescription> listMIDIDevices()
    throws GWDeviceException
  {
    try {
      final var deviceInfos =
        this.backend.getMidiDeviceInfo();

      return Arrays.stream(deviceInfos)
        .map(GWDevicesJavaMIDI::midiInfoToDeviceDescription)
        .toList();
    } catch (final Exception e) {
      throw new GWDeviceException(DEVICE_MIDI_SYSTEM_ERROR, e);
    }
  }

  private final class PotentialDevice
  {
    private GWDeviceMIDIDescription description;
    private MidiDevice receiver;
    private MidiDevice transmitter;

    PotentialDevice()
    {

    }

    public void close()
    {
      try {
        final var dev = this.receiver;
        if (dev != null) {
          dev.close();
        }
      } catch (final Exception e) {
        LOG.debug("error closing receiver: ", e);
      }

      try {
        final var dev = this.transmitter;
        if (dev != null) {
          dev.close();
        }
      } catch (final Exception e) {
        LOG.debug("error closing transmitter: ", e);
      }
    }
  }
}
