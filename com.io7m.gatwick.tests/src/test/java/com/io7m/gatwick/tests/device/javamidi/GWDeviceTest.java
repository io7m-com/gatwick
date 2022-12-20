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
import com.io7m.gatwick.device.api.GWDeviceException;
import com.io7m.gatwick.device.api.GWDeviceResponseRequestData;
import com.io7m.gatwick.device.api.GWDeviceStandardErrorCodes;
import com.io7m.gatwick.device.api.GWDeviceType;
import com.io7m.gatwick.device.javamidi.GWDevicesJavaMIDI;
import com.io7m.gatwick.device.javamidi.GWDevicesJavaMIDIDevicesType;
import com.io7m.gatwick.device.javamidi.internal.GWDeviceMessages;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Transmitter;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import static com.io7m.gatwick.device.api.GWDeviceStandardErrorCodes.DEVICE_MIDI_MESSAGE_INVALID;
import static com.io7m.gatwick.device.api.GWDeviceStandardErrorCodes.DEVICE_MIDI_MESSAGE_UNEXPECTED_TYPE;
import static com.io7m.gatwick.device.api.GWDeviceStandardErrorCodes.DEVICE_MIDI_SYSTEM_ERROR;
import static com.io7m.gatwick.device.api.GWDeviceStandardErrorCodes.DEVICE_NOT_FOUND;
import static com.io7m.gatwick.device.api.GWDeviceStandardErrorCodes.DEVICE_TIMED_OUT;
import static com.io7m.gatwick.device.api.GWDeviceStandardErrorCodes.DEVICE_WRONG_MANUFACTURER;
import static com.io7m.gatwick.device.api.GWDeviceStandardErrorCodes.DEVICE_WRONG_MESSAGE_CATEGORY;
import static com.io7m.gatwick.device.api.GWDeviceStandardErrorCodes.DEVICE_WRONG_MESSAGE_TYPE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@Timeout(value = 5L, unit = TimeUnit.SECONDS)
public final class GWDeviceTest
{
  private GWDevicesJavaMIDI devices;
  private GWDevicesJavaMIDIDevicesType backend;
  private MidiDevice midiDevice;
  private Transmitter midiTransmitter;
  private Receiver midiReceiver;
  private AtomicReference<Receiver> currentReceiver;
  private ExecutorService midiThread;

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
  }

  @AfterEach
  public void tearDown()
  {
    this.midiThread.shutdown();
  }

  /**
   * If no devices match, an error occurs.
   *
   * @throws Exception On errors
   */

  @Test
  public void testOpenNoDevices()
  {
    when(this.backend.getMidiDeviceInfo())
      .thenReturn(new MidiDevice.Info[0]);

    final var ex =
      assertThrows(GWDeviceException.class, () -> {
        this.devices.openDevice(new GWDeviceConfiguration(
          Pattern.compile(".*"),
          Duration.ofSeconds(10L),
          Duration.ofSeconds(10L),
          1,
          Duration.ofMillis(100L)
        ));
      });

    assertEquals(DEVICE_NOT_FOUND, ex.errorCode());
  }

  /**
   * If a matching device raises an error, an error occurs.
   *
   * @throws Exception On errors
   */

  @Test
  public void testOpenDeviceFailure()
    throws Exception
  {
    final var info =
      new MidiDevice.Info("GT-1000", "BOSS", "GT-1000", "1.0")
      {

      };

    when(this.backend.getMidiDeviceInfo())
      .thenReturn(new MidiDevice.Info[]{info});

    when(this.backend.getMidiDevice(info))
      .thenThrow(new MidiUnavailableException("Device not ready."));

    final var ex =
      assertThrows(GWDeviceException.class, () -> {
        this.devices.openDevice(new GWDeviceConfiguration(
          Pattern.compile(".*"),
          Duration.ofSeconds(10L),
          Duration.ofSeconds(10L),
          1,
          Duration.ofMillis(100L)
        ));
      });

    assertEquals(DEVICE_MIDI_SYSTEM_ERROR, ex.errorCode());
  }

  /**
   * If the device doesn't respond to identity requests in time, opening the
   * device failed.
   *
   * @throws Exception On errors
   */

  @Test
  public void testOpenDeviceIdentityTimeout()
    throws Exception
  {
    /* Arrange */

    final var info =
      new MidiDevice.Info("GT-1000", "BOSS", "GT-1000", "1.0")
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

    /* Act */

    final var ex =
      assertThrows(GWDeviceException.class, () -> {
        this.devices.openDevice(new GWDeviceConfiguration(
          Pattern.compile(".*"),
          Duration.ofMillis(100L),
          Duration.ofMillis(100L),
          1,
          Duration.ofMillis(100L)
        ));
      });

    /* Assert */

    assertEquals(DEVICE_TIMED_OUT, ex.errorCode());

    verify(this.midiDevice, new Times(1))
      .getMaxTransmitters();
    verify(this.midiDevice, new Times(1))
      .getMaxReceivers();
    verify(this.midiDevice, new Times(1))
      .getTransmitter();
    verify(this.midiDevice, new Times(1))
      .getReceiver();
    verify(this.midiDevice, new Times(2))
      .open();

    verify(this.midiReceiver, new Times(1))
      .send(argThat(m -> m instanceof SysexMessage), eq(-1L));

    verify(this.midiTransmitter, new Times(1))
      .setReceiver(any());

    verifyNoMoreInteractions(this.midiReceiver);
    verifyNoMoreInteractions(this.midiTransmitter);
    verifyNoMoreInteractions(this.midiDevice);
  }

  /**
   * The device's information is captured properly.
   *
   * @throws Exception On errors
   */

  @Test
  public void testOpenDeviceOK()
    throws Exception
  {
    /* Arrange */

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

    /* Act */

    final var device =
      this.devices.openDevice(new GWDeviceConfiguration(
        Pattern.compile(".*"),
        Duration.ofMillis(100L),
        Duration.ofMillis(100L),
        1,
        Duration.ofMillis(100L)
      ));

    /* Assert */

    final var desc = device.description();
    assertEquals(0x41, desc.deviceManufacturer());
    assertEquals(0x4f03, desc.deviceFamilyCode());
    assertEquals(0x0, desc.deviceFamilyNumberCode());
    assertEquals(0x0200_0000, desc.deviceSoftwareVersion());
    assertEquals("GT-1000", desc.midiDeviceName());
    assertEquals("GT-1000 Hardware", desc.midiDeviceDescription());
    assertEquals("BOSS", desc.midiDeviceVendor());
    assertEquals("1.0", desc.midiDeviceVersion());
  }

  /**
   * The device must see the correct identity message manufacturer.
   *
   * @throws Exception On errors
   */

  @Test
  public void testOpenDeviceIdentityWrongManufacturer()
    throws Exception
  {
    /* Arrange */

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
    data[5] = 0x34; // Wrong manufacturer ID
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

    /* Act */

    final var ex =
      assertThrows(GWDeviceException.class, () -> {
        this.devices.openDevice(new GWDeviceConfiguration(
          Pattern.compile(".*"),
          Duration.ofMillis(100L),
          Duration.ofMillis(100L),
          1,
          Duration.ofMillis(100L)
        ));
      });

    /* Assert */

    assertEquals(DEVICE_WRONG_MANUFACTURER, ex.errorCode());
  }

  /**
   * The device must see the correct identity message category.
   *
   * @throws Exception On errors
   */

  @Test
  public void testOpenDeviceIdentityWrongCategory()
    throws Exception
  {
    /* Arrange */

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
    data[3] = 0x1; // Wrong category
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

    /* Act */

    final var ex =
      assertThrows(GWDeviceException.class, () -> {
        this.devices.openDevice(new GWDeviceConfiguration(
          Pattern.compile(".*"),
          Duration.ofMillis(100L),
          Duration.ofMillis(100L),
          1,
          Duration.ofMillis(100L)
        ));
      });

    /* Assert */

    assertEquals(DEVICE_WRONG_MESSAGE_CATEGORY, ex.errorCode());
  }

  /**
   * The device must see the correct identity message type.
   *
   * @throws Exception On errors
   */

  @Test
  public void testOpenDeviceIdentityWrongType()
    throws Exception
  {
    /* Arrange */

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
    data[4] = 0x32; // Wrong message type
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

    /* Act */

    final var ex =
      assertThrows(GWDeviceException.class, () -> {
        this.devices.openDevice(new GWDeviceConfiguration(
          Pattern.compile(".*"),
          Duration.ofMillis(100L),
          Duration.ofMillis(100L),
          1,
          Duration.ofMillis(100L)
        ));
      });

    /* Assert */

    assertEquals(DEVICE_WRONG_MESSAGE_TYPE, ex.errorCode());
  }

  /**
   * Sending a write command works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testSendWrite()
    throws Exception
  {
    final var command =
      new GWDeviceCommandSetData(
      0x0000_0000,
      new byte[]{(byte) 0xaa, (byte) 0xbb, (byte) 0xcc, (byte) 0xdd}
    );

    try (var device = this.openDeviceCorrectly()) {

      /*
       * Do nothing in response to sent messages.
       */

      doAnswer(invocation -> "ok")
        .when(this.midiReceiver)
        .send(any(), anyLong());

      device.sendCommand(command);
      device.sendCommand(command);
      device.sendCommand(command);
    }

    /* Assert */

    /*
     * Four messages should have been sent; the initial identity request,
     * and the three write commands.
     */

    verify(this.midiReceiver, new Times(4))
      .send(argThat(m -> m instanceof SysexMessage), eq(-1L));
  }

  /**
   * Sending a read command works as long as a response comes back.
   *
   * @throws Exception On errors
   */

  @Test
  public void testSendRead()
    throws Exception
  {
    final var command =
      new GWDeviceCommandRequestData(
        0x0000_0000,
        4
      );

    final GWDeviceResponseRequestData response;
    try (var device = this.openDeviceCorrectly()) {

      /*
       * Respond with the correct message type.
       */

      doAnswer(invocation -> {
        final var msg =
          GWDeviceMessages.serializeCommand(
            0x1f,
            0x41,
            new GWDeviceCommandSetData(0, new byte[4])
          );

        this.midiThread.execute(() -> {
          this.currentReceiver.get().send(msg, -1L);
        });
        return "ok";
      })
        .when(this.midiReceiver)
        .send(any(), anyLong());

      response = device.sendCommand(command);
    }

    /* Assert */

    assertEquals(0, response.address());
    assertArrayEquals(new byte[4], response.data());
    assertEquals(128, response.checksum());
  }

  /**
   * Sending a read command times out if nothing comes back. The request
   * is attempted three times.
   *
   * @throws Exception On errors
   */

  @Test
  public void testSendReadTimeout()
    throws Exception
  {
    final var command =
      new GWDeviceCommandRequestData(
        0x0000_0000,
        4
      );

    try (var device = this.openDeviceCorrectly()) {

      /*
       * Do nothing in response to sent messages.
       */

      doAnswer(invocation -> "ok")
        .when(this.midiReceiver)
        .send(any(), anyLong());

      final var ex =
        assertThrows(GWDeviceException.class, () -> {
          device.sendCommand(command);
        });

      assertEquals(DEVICE_TIMED_OUT, ex.errorCode());
    }

    verify(this.midiReceiver, new Times(4))
      .send(any(), eq(-1L));
  }

  /**
   * Sending a read command fails if the wrong response comes back.
   *
   * @throws Exception On errors
   */

  @Test
  public void testSendReadInvalid()
    throws Exception
  {
    final var command =
      new GWDeviceCommandRequestData(
        0x0000_0000,
        4
      );

    try (var device = this.openDeviceCorrectly()) {

      /*
       * Respond with the wrong message type.
       */

      doAnswer(invocation -> {
        final var msg =
          GWDeviceMessages.serializeCommand(
            0x1f,
            0x41,
            new GWDeviceCommandRequestData(0, 23)
          );

        this.midiThread.execute(() -> {
          this.currentReceiver.get().send(msg, -1L);
        });
        return "ok";
      })
        .when(this.midiReceiver)
        .send(any(), anyLong());

      final var ex =
        assertThrows(GWDeviceException.class, () -> {
          device.sendCommand(command);
        });

      assertEquals(DEVICE_MIDI_MESSAGE_UNEXPECTED_TYPE, ex.errorCode());
    }
  }

  /**
   * Sending a read command fails if the wrong response comes back.
   *
   * @throws Exception On errors
   */

  @Test
  public void testSendReadInvalidManufacturer()
    throws Exception
  {
    final var command =
      new GWDeviceCommandRequestData(
        0x0000_0000,
        4
      );

    final GWDeviceResponseRequestData response;
    try (var device = this.openDeviceCorrectly()) {

      /*
       * Respond with the wrong message type.
       */

      doAnswer(invocation -> {
        final var msg =
          GWDeviceMessages.serializeCommand(
            0x1f,
            0x20,
            new GWDeviceCommandRequestData(0, 23)
          );

        this.midiThread.execute(() -> {
          this.currentReceiver.get().send(msg, -1L);
        });
        return "ok";
      })
        .when(this.midiReceiver)
        .send(any(), anyLong());

      final var ex =
        assertThrows(GWDeviceException.class, () -> {
          device.sendCommand(command);
        });

      assertEquals(DEVICE_WRONG_MANUFACTURER, ex.errorCode());
    }
  }

  private GWDeviceType openDeviceCorrectly()
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

    final var device =
      this.devices.openDevice(new GWDeviceConfiguration(
      Pattern.compile(".*"),
      Duration.ofMillis(100L),
      Duration.ofMillis(100L),
      3,
      Duration.ofMillis(50L)
    ));

    assertTrue(device.toString().contains("GWDeviceJavaMIDI"));
    return device;
  }
}
