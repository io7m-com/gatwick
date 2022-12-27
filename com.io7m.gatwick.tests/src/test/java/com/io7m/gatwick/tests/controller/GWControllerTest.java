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
import com.io7m.gatwick.controller.api.GWControllerType;
import com.io7m.gatwick.controller.main.GWControllers;
import com.io7m.gatwick.device.api.GWDeviceConfiguration;
import com.io7m.gatwick.device.api.GWDeviceMIDIDescription;
import com.io7m.gatwick.device.javamidi.GWDevicesJavaMIDI;
import com.io7m.gatwick.device.javamidi.GWDevicesJavaMIDIDevicesType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.Mockito;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.Receiver;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Transmitter;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@Timeout(value = 5L, unit = TimeUnit.SECONDS)
public final class GWControllerTest
{
  private GWDevicesJavaMIDI devices;
  private GWDevicesJavaMIDIDevicesType backend;
  private MidiDevice midiDevice;
  private Transmitter midiTransmitter;
  private Receiver midiReceiver;
  private AtomicReference<Receiver> currentReceiver;
  private ExecutorService midiThread;
  private GWControllers controllers;

  @BeforeEach
  public void setup()
  {
    this.midiDevice =
      Mockito.mock(MidiDevice.class);
    this.midiTransmitter =
      Mockito.mock(Transmitter.class);
    this.midiReceiver =
      Mockito.mock(Receiver.class);
    this.midiThread =
      Executors.newFixedThreadPool(1);

    this.currentReceiver =
      new AtomicReference<>();

    this.backend =
      Mockito.mock(GWDevicesJavaMIDIDevicesType.class);
    this.devices =
      new GWDevicesJavaMIDI(this.backend);
    this.controllers =
      new GWControllers();
  }

  @AfterEach
  public void tearDown()
  {
    this.midiThread.shutdown();
  }

  /**
   * Opening a controller works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testOpen()
    throws Exception
  {
    try (var controller = this.openControllerCorrectly()) {
      final var patch = controller.patchCurrent();
      assertEquals("", patch.name().get());
    }
  }

  private GWControllerType openControllerCorrectly()
    throws Exception
  {
    final var info =
      new MidiDevice.Info("GT-1000", "BOSS", "GT-1000 Hardware", "1.0")
      {

      };

    when(this.backend.getMidiDeviceInfo())
      .thenReturn(new MidiDevice.Info[]{info});
    when(this.backend.getMidiDevice(info))
      .thenReturn(this.midiDevice);

    when(this.midiDevice.getMaxReceivers())
      .thenReturn(1);
    when(this.midiDevice.getMaxTransmitters())
      .thenReturn(1);
    when(this.midiDevice.getReceiver())
      .thenReturn(this.midiReceiver);
    when(this.midiDevice.getTransmitter())
      .thenReturn(this.midiTransmitter);
    when(this.midiDevice.getDeviceInfo())
      .thenReturn(info);

    /*
     * Save the receiver the device implementation sets. This will be
     * used to spoof an identity sysex message.
     */

    doAnswer(invocation -> {
      this.currentReceiver.set(invocation.getArgument(0, Receiver.class));
      return "ok";
    }).when(this.midiTransmitter)
      .setReceiver(any());

    // The "Identity Reply Message"
    final var data = new byte[15];
    data[0] = (byte) 0xf0; // System Exclusive Message status
    data[1] = 0x7e; // ID Number (Universal Non-realtime Message)
    data[2] = 0x1f; // Device ID (dev: 00H - 1FH)
    data[3] = 0x6; // Sub ID # 1 (General Information)
    data[4] = 0x2; // Sub ID # 2 (Identity Reply)
    data[5] = 0x41; //  Roland's manufacturer ID
    data[6] = 0x4f; // GT-1000
    data[7] = 0x3; // GT-1000
    data[8] = 0x0; // GT-1000
    data[9] = 0x0; // GT-1000
    data[10] = 0x02; // Version
    data[11] = 0x00; //
    data[12] = 0x00; //
    data[13] = 0x00; //
    data[14] = (byte) 0xf7; // End of sysex

    doAnswer(invocation -> {
      final var msg = new SysexMessage(data, data.length);
      this.midiThread.execute(() -> {
        this.currentReceiver.get().send(msg, -1L);
      });
      return "ok";
    }).when(this.midiReceiver)
      .send(any(), anyLong());

    final var controller =
      this.controllers.openControllerWith(
        this.devices,
        new GWControllerConfiguration(
          gwDeviceFactoryType -> Objects.equals(
            gwDeviceFactoryType,
            this.devices),
          new GWDeviceConfiguration(
            new GWDeviceMIDIDescription(
              info.getName(),
              info.getDescription(),
              info.getVendor(),
              info.getVersion()
            ),
            Duration.ofMillis(100L),
            Duration.ofMillis(100L),
            3,
            Duration.ofMillis(50L)
          )
        )
      );

    return controller;
  }
}
