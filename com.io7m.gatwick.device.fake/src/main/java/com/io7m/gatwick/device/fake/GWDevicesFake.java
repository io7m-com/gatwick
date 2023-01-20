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


package com.io7m.gatwick.device.fake;

import com.io7m.gatwick.device.api.GWDeviceConfiguration;
import com.io7m.gatwick.device.api.GWDeviceDescription;
import com.io7m.gatwick.device.api.GWDeviceException;
import com.io7m.gatwick.device.api.GWDeviceFactoryProperty;
import com.io7m.gatwick.device.api.GWDeviceFactoryType;
import com.io7m.gatwick.device.api.GWDeviceMIDIDescription;
import com.io7m.gatwick.device.api.GWDeviceType;
import com.io7m.gatwick.device.fake.internal.GWDeviceFake;
import com.io7m.taskrecorder.core.TRTask;
import com.io7m.taskrecorder.core.TRTaskRecorderType;
import net.jcip.annotations.GuardedBy;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import static com.io7m.gatwick.device.api.GWDeviceStandardErrorCodes.DEVICE_NOT_FOUND;

/**
 * A provider of fake devices.
 */

public final class GWDevicesFake
  implements GWDeviceFactoryType
{
  private static final Set<GWDeviceFactoryProperty> PROPERTIES =
    Set.of(new GWDeviceFactoryProperty("fake"));

  @GuardedBy("deviceLock")
  private final TreeMap<Long, GWDeviceFake> devicesNotOpen;
  @GuardedBy("deviceLock")
  private final TreeMap<Long, GWDeviceFake> devicesOpen;
  private final Object deviceLock;

  /**
   * A provider of fake devices.
   */

  public GWDevicesFake()
  {
    this.deviceLock = new Object();
    this.devicesNotOpen = new TreeMap<>();
    this.devicesOpen = new TreeMap<>();

    for (int index = 0; index < 10; ++index) {
      final var id = (long) index;
      final var description =
        deviceDescriptionOf(id);
      final var device =
        new GWDeviceFake(id, description);

      this.devicesNotOpen.put(Long.valueOf(id), device);
    }
  }

  private static GWDeviceDescription deviceDescriptionOf(
    final long id)
  {
    return new GWDeviceDescription(
      new GWDeviceMIDIDescription(
        String.format("Fake 0x%016x", Long.valueOf(id)),
        "Fake MIDI device",
        "Fake",
        "1.0"
      ),
      0x20,
      0x20,
      0x20,
      0x20
    );
  }

  @Override
  public Set<GWDeviceFactoryProperty> properties()
  {
    return PROPERTIES;
  }

  @Override
  public GWDeviceType openDevice(
    final GWDeviceConfiguration configuration)
    throws GWDeviceException
  {
    synchronized (this.deviceLock) {
      final var device =
        this.devicesNotOpen.values()
          .stream()
          .filter(d -> {
            final var midiDevice = d.description().midiDevice();
            return Objects.equals(midiDevice, configuration.device());
          })
          .findFirst()
          .orElseThrow(() -> {
            return new GWDeviceException(
              DEVICE_NOT_FOUND,
              "No usable devices available matching '%s'"
                .formatted(configuration.device().midiDeviceName())
            );
          });

      final var id = Long.valueOf(device.id());
      this.devicesNotOpen.remove(id);
      this.devicesOpen.put(Long.valueOf(device.id()), device);
      return device;
    }
  }

  @Override
  public TRTask<List<GWDeviceMIDIDescription>> detectDevices(
    final TRTaskRecorderType<?> recorder)
  {
    try (var subRec =
           recorder.<List<GWDeviceMIDIDescription>>beginSubtask(
      "Detecting devices...")) {
      final List<GWDeviceMIDIDescription> results;
      synchronized (this.deviceLock) {
        results = this.devicesNotOpen.values()
          .stream()
          .map(GWDeviceFake::description)
          .map(GWDeviceDescription::midiDevice)
          .toList();
      }
      subRec.setTaskSucceeded("Detected devices.", results);
      return subRec.toTask();
    }
  }

  @Override
  public List<GWDeviceMIDIDescription> listMIDIDevices()
  {
    synchronized (this.deviceLock) {
      return this.devicesNotOpen.values()
        .stream()
        .map(GWDeviceFake::description)
        .map(GWDeviceDescription::midiDevice)
        .toList();
    }
  }
}
