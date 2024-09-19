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

package com.io7m.gatwick.device.javamidi.internal;

import com.io7m.gatwick.device.api.GWDeviceCommandRequestData;
import com.io7m.gatwick.device.api.GWDeviceCommandSetData;
import com.io7m.gatwick.device.api.GWDeviceCommandType;
import com.io7m.gatwick.device.api.GWDeviceConfiguration;
import com.io7m.gatwick.device.api.GWDeviceDescription;
import com.io7m.gatwick.device.api.GWDeviceException;
import com.io7m.gatwick.device.api.GWDeviceMIDIDescription;
import com.io7m.gatwick.device.api.GWDeviceResponseOK;
import com.io7m.gatwick.device.api.GWDeviceResponseType;
import com.io7m.gatwick.device.api.GWDeviceStandardErrorCodes;
import com.io7m.gatwick.device.api.GWDeviceType;
import com.io7m.jattribute.core.AttributeReadableType;
import com.io7m.jattribute.core.AttributeType;
import com.io7m.jattribute.core.Attributes;
import com.io7m.jmulticlose.core.CloseableCollection;
import com.io7m.jmulticlose.core.CloseableCollectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.SysexMessage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Objects;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.io7m.gatwick.device.api.GWDeviceStandardErrorCodes.DEVICE_MIDI_MESSAGE_INVALID;
import static com.io7m.gatwick.device.api.GWDeviceStandardErrorCodes.DEVICE_MIDI_SYSTEM_ERROR;
import static com.io7m.gatwick.device.api.GWDeviceStandardErrorCodes.DEVICE_TIMED_OUT;

/**
 * The JavaMIDI device implementation.
 */

public final class GWDeviceJavaMIDI implements GWDeviceType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(GWDeviceJavaMIDI.class);

  private final GWDeviceDescription description;
  private final GWDeviceConfiguration configuration;
  private final byte deviceIdentifier;
  private final CloseableCollectionType<GWDeviceException> resources;
  private final Receiver receiver;
  private final DeviceMessageReceiver messageReceiver;
  private final AttributeType<Duration> commandRTT;
  private Instant timeSendStarted;
  private Instant timeSendReceived;

  /**
   * The JavaMIDI device implementation.
   */

  private GWDeviceJavaMIDI(
    final GWDeviceDescription inDescription,
    final GWDeviceConfiguration inConfiguration,
    final byte inDeviceIdentifier,
    final CloseableCollectionType<GWDeviceException> inResources,
    final Receiver inReceiver,
    final DeviceMessageReceiver inMessageReceiver)
  {
    this.description =
      Objects.requireNonNull(inDescription, "description");
    this.configuration =
      Objects.requireNonNull(inConfiguration, "configuration");
    this.deviceIdentifier =
      inDeviceIdentifier;
    this.resources =
      Objects.requireNonNull(inResources, "resources");
    this.receiver =
      Objects.requireNonNull(inReceiver, "receiver");
    this.messageReceiver =
      Objects.requireNonNull(inMessageReceiver, "messageReceiver");
    this.timeSendStarted =
      Instant.now();
    this.timeSendReceived =
      Instant.now();

    this.commandRTT =
      Attributes.create(throwable -> {
        LOG.error("error captured by attribute: ", throwable);
      }).fromFunction(() -> Duration.between(this.timeSendStarted, this.timeSendReceived));
  }

  /**
   * Open a device.
   *
   * @param configuration     The configuration
   * @param receiverDevice    The receiver device
   * @param transmitterDevice The transmitter device
   *
   * @return A device
   *
   * @throws GWDeviceException On errors
   */

  public static GWDeviceType open(
    final GWDeviceConfiguration configuration,
    final MidiDevice receiverDevice,
    final MidiDevice transmitterDevice)
    throws GWDeviceException
  {
    final var resources =
      CloseableCollection.create(() -> {
        return new GWDeviceException(
          DEVICE_MIDI_SYSTEM_ERROR,
          "Device failure."
        );
      });

    resources.add(transmitterDevice);
    resources.add(receiverDevice);

    try {
      receiverDevice.open();
      transmitterDevice.open();

      final var receiver =
        receiverDevice.getReceiver();
      final var transmitter =
        transmitterDevice.getTransmitter();

      resources.add(transmitter);
      resources.add(receiver);

      final var identityLatch =
        new CountDownLatch(1);
      final var identityReceiver =
        new DeviceIdentityReceiver(identityLatch);

      transmitter.setReceiver(identityReceiver);

      LOG.trace("requesting device identity");

      {
        final var data = new byte[6];
        // Sysex status
        data[0] = (byte) 0xf0;
        // Universal non-realtime message
        data[1] = (byte) 0x7e;
        // Broadcast to all devices
        data[2] = (byte) 0x7f;
        // "General information" message category
        data[3] = (byte) 0x06;
        // "Identity request" message type
        data[4] = (byte) 0x01;
        // End of sysex
        data[5] = (byte) 0xf7;

        final var msg = new SysexMessage(data, data.length);
        receiver.send(msg, -1L);
      }

      final var seconds = configuration.openTimeout().toMillis();
      if (!identityLatch.await(seconds, TimeUnit.MILLISECONDS)) {
        throw new GWDeviceException(
          DEVICE_TIMED_OUT,
          "Timed out waiting for device identity."
        );
      }

      if (identityReceiver.failure != null) {
        throw identityReceiver.failure;
      }

      final var messageReceiver = new DeviceMessageReceiver(configuration);
      transmitter.setReceiver(messageReceiver);

      final var deviceInfo =
        receiverDevice.getDeviceInfo();

      final var midiDevice =
        new GWDeviceMIDIDescription(
          deviceInfo.getName(),
          deviceInfo.getDescription(),
          deviceInfo.getVendor(),
          deviceInfo.getVersion()
        );

      final var info =
        new GWDeviceDescription(
          midiDevice,
          identityReceiver.deviceManufacturer,
          identityReceiver.deviceFamilyCode,
          identityReceiver.deviceFamilyNumberCode,
          identityReceiver.deviceSoftwareVersion
        );

      return new GWDeviceJavaMIDI(
        info,
        configuration,
        identityReceiver.deviceIdentifier,
        resources,
        receiver,
        messageReceiver
      );

    } catch (final MidiUnavailableException | InvalidMidiDataException e) {
      resources.close();
      throw new GWDeviceException(DEVICE_MIDI_SYSTEM_ERROR, e.getMessage(), e);
    } catch (final InterruptedException e) {
      resources.close();
      throw new GWDeviceException(DEVICE_TIMED_OUT, e.getMessage(), e);
    }
  }

  private static final class DeviceMessageReceiver implements Receiver
  {
    private static final Logger LOG =
      LoggerFactory.getLogger(DeviceMessageReceiver.class);

    private final HexFormat format;
    private final CyclicBarrier barrier;
    private final GWDeviceConfiguration configuration;
    private volatile int expectCode;
    private volatile GWDeviceResponseType response;
    private volatile GWDeviceException failure;

    DeviceMessageReceiver(
      final GWDeviceConfiguration inConfiguration)
    {
      this.configuration =
        Objects.requireNonNull(inConfiguration, "configuration");

      this.format = HexFormat.of();
      this.barrier = new CyclicBarrier(2);
      this.expectCode = 0b11111111_11111111_11111111_11111111;
    }

    @Override
    public void send(
      final MidiMessage message,
      final long timeStamp)
    {
      LOG.trace("received: {}", message);

      this.response = null;
      this.failure = null;

      try {
        if (message instanceof SysexMessage sysex) {
          if (LOG.isTraceEnabled()) {
            LOG.trace(
              "received sysex: 0x{}",
              this.format.formatHex(sysex.getData())
            );
          }

          final var data = sysex.getData();
          try {
            this.response =
              GWDeviceMessages.parseResponse(this.expectCode, data);
          } catch (final GWDeviceException e) {
            this.failure = e;
          }
        } else {
          throw new GWDeviceException(
            DEVICE_MIDI_MESSAGE_INVALID,
            String.format(
              "Unrecognized message type %s",
              message.getClass().getName())
          );
        }
      } catch (final GWDeviceException e) {
        this.failure = e;
      } catch (final Exception e) {
        this.failure = new GWDeviceException(DEVICE_MIDI_SYSTEM_ERROR, e);
      }

      try {
        final var timeout =
          this.configuration.messageTimeout()
            .toMillis() * 2L;

        this.barrier.await(timeout, TimeUnit.MILLISECONDS);
      } catch (final Exception e) {
        final var existing = this.failure;
        if (existing != null) {
          existing.addSuppressed(e);
        } else {
          this.failure = new GWDeviceException(DEVICE_MIDI_SYSTEM_ERROR, e);
        }
      }
    }

    @Override
    public void close()
    {

    }
  }

  private static final class DeviceIdentityReceiver implements Receiver
  {
    private final CountDownLatch latch;
    private final ByteBuffer buffer;
    private volatile byte deviceIdentifier = (byte) -1;
    private volatile int deviceFamilyCode;
    private volatile int deviceFamilyNumberCode;
    private volatile int deviceSoftwareVersion;
    private volatile int deviceManufacturer;
    private volatile GWDeviceException failure;

    DeviceIdentityReceiver(
      final CountDownLatch inLatch)
    {
      this.latch = inLatch;
      this.buffer = ByteBuffer.allocate(4);
      this.buffer.order(ByteOrder.BIG_ENDIAN);
    }

    @Override
    public void send(
      final MidiMessage message,
      final long timeStamp)
    {
      try {
        if (message instanceof SysexMessage sysex) {
          final var data = sysex.getData();
          if (data.length >= 5) {
            final var deviceId = (int) data[1] & 0xff;
            final var messageCategory = (int) data[2] & 0xff;
            final var messageType = (int) data[3] & 0xff;
            final var manufacturer = (int) data[4] & 0xff;

            if (messageCategory != 0x6) {
              throw new GWDeviceException(
                GWDeviceStandardErrorCodes.DEVICE_WRONG_MESSAGE_CATEGORY,
                String.format(
                  "Unexpected identity message category 0x%02x (expected 0x6)",
                  Integer.valueOf(messageCategory))
              );
            }

            if (messageType != 0x2) {
              throw new GWDeviceException(
                GWDeviceStandardErrorCodes.DEVICE_WRONG_MESSAGE_TYPE,
                String.format(
                  "Unexpected identity message type 0x%02x (expected 0x2)",
                  Integer.valueOf(messageType))
              );
            }

            if (manufacturer != 0x41) {
              throw new GWDeviceException(
                GWDeviceStandardErrorCodes.DEVICE_WRONG_MANUFACTURER,
                String.format(
                  "Unexpected manufacturer code 0x%02x (expected 0x41)",
                  Integer.valueOf(manufacturer))
              );
            }

            this.deviceIdentifier = (byte) deviceId;
            this.deviceManufacturer = 0x41;

            this.buffer.put(0, (byte) 0);
            this.buffer.put(1, (byte) 0);
            this.buffer.put(2, (byte) ((int) data[5] & 0xff));
            this.buffer.put(3, (byte) ((int) data[6] & 0xff));
            this.deviceFamilyCode = this.buffer.getInt(0) & 0xffff;

            this.buffer.put(0, (byte) 0);
            this.buffer.put(1, (byte) 0);
            this.buffer.put(2, (byte) ((int) data[7] & 0xff));
            this.buffer.put(3, (byte) ((int) data[8] & 0xff));
            this.deviceFamilyNumberCode = this.buffer.getInt(0) & 0xffff;

            this.buffer.put(0, (byte) ((int) data[9] & 0xff));
            this.buffer.put(1, (byte) ((int) data[10] & 0xff));
            this.buffer.put(2, (byte) ((int) data[11] & 0xff));
            this.buffer.put(3, (byte) ((int) data[12] & 0xff));
            this.deviceSoftwareVersion = this.buffer.getInt(0);

            this.latch.countDown();
          }
        }
      } catch (final GWDeviceException e) {
        this.failure = e;
        this.latch.countDown();
      }
    }

    @Override
    public void close()
    {

    }
  }

  @Override
  public String toString()
  {
    return String.format(
      "[GWDeviceJavaMIDI 0x%08x]",
      Integer.valueOf(this.hashCode())
    );
  }

  @Override
  public GWDeviceDescription description()
  {
    return this.description;
  }

  @Override
  public AttributeReadableType<Duration> commandRoundTripTime()
  {
    return this.commandRTT;
  }

  @Override
  public <R extends GWDeviceResponseType> R sendCommand(
    final GWDeviceCommandType<R> command)
    throws GWDeviceException, InterruptedException
  {
    Objects.requireNonNull(command, "command");

    final var attemptMax = 3;
    for (int attempt = 1; attempt <= attemptMax; ++attempt) {
      try {
        return this.sendOneMessage(command, attempt, attemptMax);
      } catch (final GWDeviceException e) {
        if (Objects.equals(e.errorCode(), DEVICE_TIMED_OUT)) {
          if (attempt == attemptMax) {
            throw e;
          }
          LOG.trace("pausing for retry");
          Thread.sleep(50L);
          continue;
        }
        throw e;
      }
    }

    throw new GWDeviceException(
      DEVICE_TIMED_OUT, "Timed out waiting for message response."
    );
  }

  private <R extends GWDeviceResponseType> R sendOneMessage(
    final GWDeviceCommandType<R> command,
    final int attempt,
    final int attemptMax)
    throws GWDeviceException, InterruptedException
  {
    this.timeSendStarted = Instant.now();

    if (LOG.isTraceEnabled()) {
      LOG.trace(
        "sendCommand ({}/{}) {}",
        Integer.valueOf(attempt),
        Integer.valueOf(attemptMax),
        command
      );
    }

    this.messageReceiver.barrier.reset();
    this.messageReceiver.expectCode = expectCodeFor(command);

    try {
      this.receiver.send(
        GWDeviceMessages.serializeCommand(
          (int) this.deviceIdentifier & 0xff,
          this.description.deviceManufacturer(),
          command
        ),
        -1L
      );
    } catch (final InvalidMidiDataException e) {
      throw new GWDeviceException(
        DEVICE_MIDI_MESSAGE_INVALID,
        e.getMessage(),
        e
      );
    }

    /*
     * If the type of the command implies that there won't be a response,
     * then simply return immediately.
     */

    final var expectResponse = command.responseClass();
    if (Objects.equals(expectResponse, GWDeviceResponseOK.class)) {
      return expectResponse.cast(GWDeviceResponseOK.ok());
    }

    /*
     * Otherwise, wait for a response.
     */

    final var milliseconds =
      this.configuration.messageTimeout()
        .toMillis();

    try {
      this.messageReceiver.barrier.await(milliseconds, TimeUnit.MILLISECONDS);
    } catch (final BrokenBarrierException | TimeoutException e) {
      throw new GWDeviceException(
        DEVICE_TIMED_OUT, "Timed out waiting for message response."
      );
    }

    this.timeSendReceived = Instant.now();
    this.commandRTT.set(Duration.between(this.timeSendStarted, this.timeSendReceived));

    final var response = this.messageReceiver.response;
    if (response == null) {
      throw this.messageReceiver.failure;
    }

    return expectResponse.cast(response);
  }

  private static int expectCodeFor(
    final GWDeviceCommandType<?> command)
  {
    /*
     * An RQ1 (0x11) command provokes a DT1 (0x12) response.
     */

    if (command instanceof GWDeviceCommandRequestData) {
      return 0x12;
    }

    /*
     * An DT1 (0x12) command provokes no response.
     */

    if (command instanceof GWDeviceCommandSetData) {
      return -1;
    }

    throw new IllegalStateException(
      "Unrecognized command: %s".formatted(command)
    );
  }

  @Override
  public void close()
    throws GWDeviceException
  {
    this.resources.close();
  }
}
