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

import com.io7m.gatwick.device.api.GWDeviceChecksums;
import com.io7m.gatwick.device.api.GWDeviceCommandRequestData;
import com.io7m.gatwick.device.api.GWDeviceCommandSetData;
import com.io7m.gatwick.device.api.GWDeviceCommandType;
import com.io7m.gatwick.device.api.GWDeviceException;
import com.io7m.gatwick.device.api.GWDeviceResponseRequestData;
import com.io7m.gatwick.device.api.GWDeviceResponseType;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.SysexMessage;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static com.io7m.gatwick.device.api.GWDeviceStandardErrorCodes.DEVICE_MIDI_MESSAGE_INVALID;
import static com.io7m.gatwick.device.api.GWDeviceStandardErrorCodes.DEVICE_MIDI_MESSAGE_UNEXPECTED_TYPE;
import static com.io7m.gatwick.device.api.GWDeviceStandardErrorCodes.DEVICE_WRONG_MANUFACTURER;
import static com.io7m.gatwick.device.api.GWDeviceStandardErrorCodes.DEVICE_WRONG_MESSAGE_TYPE;
import static java.nio.ByteOrder.BIG_ENDIAN;

/**
 * Commands to serialize and parse messages.
 */

public final class GWDeviceMessages
{
  private GWDeviceMessages()
  {

  }

  private static final int COMMAND_DATA_SET = 0x12;
  private static final int COMMAND_DATA_REQUEST = 0x11;

  /**
   * Parse a response.
   *
   * @param expectCode The expected Sysex message code
   * @param data       The data
   *
   * @return A parsed response
   *
   * @throws GWDeviceException On errors
   */

  public static GWDeviceResponseType parseResponse(
    final int expectCode,
    final byte[] data)
    throws GWDeviceException
  {
    if (data.length >= 7) {
      final var manufacturer =
        (int) data[0] & 0xff;
      final var commandId =
        (int) data[6] & 0xff;

      if (manufacturer != 0x41) {
        throw new GWDeviceException(
          DEVICE_WRONG_MANUFACTURER,
          String.format(
            "Message manufacturer ID expected 0x41 but received 0x%02x",
            Integer.valueOf(manufacturer))
        );
      }

      if (commandId != expectCode) {
        throw new GWDeviceException(
          DEVICE_MIDI_MESSAGE_UNEXPECTED_TYPE,
          String.format(
            "Expected a response of type 0x%02x but received 0x%02x",
            Integer.valueOf(expectCode),
            Integer.valueOf(commandId))
        );
      }

      return switch (commandId) {
        case COMMAND_DATA_SET -> {
          yield parseCommandDataSet(data);
        }
        case COMMAND_DATA_REQUEST -> {
          yield parseCommandDataRequest(data);
        }
        default -> {
          throw new GWDeviceException(
            DEVICE_WRONG_MESSAGE_TYPE,
            String.format(
              "Unrecognized message type 0x%02x",
              Integer.valueOf(commandId))
          );
        }
      };
    }

    throw new GWDeviceException(
      DEVICE_MIDI_MESSAGE_INVALID,
      String.format(
        "A message of length %d is too short to be a valid response message.",
        Integer.valueOf(data.length))
    );
  }

  private static GWDeviceResponseType parseCommandDataRequest(
    final byte[] data)
  {
    throw new IllegalStateException();
  }

  private static GWDeviceResponseType parseCommandDataSet(
    final byte[] data)
    throws GWDeviceException
  {
    if (data.length < 13) {
      throw new GWDeviceException(
        DEVICE_MIDI_MESSAGE_INVALID,
        String.format(
          "A message of length %d is too short to be a valid response message.",
          Integer.valueOf(data.length))
      );
    }

    final var buffer = ByteBuffer.allocate(4);
    buffer.order(BIG_ENDIAN);
    buffer.put(0, data[7]);
    buffer.put(1, data[8]);
    buffer.put(2, data[9]);
    buffer.put(3, data[10]);

    final var address = buffer.getInt(0);

    // There must be at least one byte of data, a checksum byte, and an EOM
    final var sizePreamble = 11;
    final var sizeChecksum = 1;
    final var sizeEOM = 1;
    final var sizeNotData = sizePreamble + sizeChecksum + sizeEOM;
    final var size = data.length - sizeNotData;

    final var payload = new byte[size];
    System.arraycopy(data, 11, payload, 0, payload.length);

    return new GWDeviceResponseRequestData(
      address,
      payload,
      (int) data[data.length - 2] & 0xff
    );
  }

  /**
   * Serialize a command.
   *
   * @param midiDeviceId       The target device ID
   * @param midiManufacturerId The required manufacturer ID
   * @param command            The command
   *
   * @return A serialized command
   *
   * @throws InvalidMidiDataException On errors
   */

  public static MidiMessage serializeCommand(
    final int midiDeviceId,
    final int midiManufacturerId,
    final GWDeviceCommandType<?> command)
    throws InvalidMidiDataException
  {
    if (command instanceof GWDeviceCommandRequestData c) {
      return serializeCommandRequestData(midiDeviceId, midiManufacturerId, c);
    }
    if (command instanceof GWDeviceCommandSetData c) {
      return serializeCommandSetData(midiDeviceId, midiManufacturerId, c);
    }

    throw new IllegalStateException(
      "Unrecognized command: %s".formatted(command)
    );
  }

  private static MidiMessage serializeCommandSetData(
    final int midiDeviceId,
    final int midiManufacturerId,
    final GWDeviceCommandSetData command)
    throws InvalidMidiDataException
  {
    final var buffer =
      ByteBuffer.allocate(4)
        .order(BIG_ENDIAN);

    final var payload = command.data();
    final var data = new byte[12 + payload.length + 2];
    // Sysex status
    data[0] = (byte) 0xf0;
    data[1] = (byte) (midiManufacturerId & 0xff);
    data[2] = (byte) (midiDeviceId & 0xff);
    // "Model ID" GT-1000
    data[3] = (byte) 0x00;
    // "Model ID" GT-1000
    data[4] = (byte) 0x00;
    // "Model ID" GT-1000
    data[5] = (byte) 0x00;
    // "Model ID" GT-1000
    data[6] = (byte) 0x4f;
    // Command ID ("DT1")
    data[7] = (byte) (command.commandCode() & 0xff);

    // Address
    buffer.putInt(0, command.address());
    buffer.get(0, data, 8, 4);

    // Data bytes
    System.arraycopy(payload, 0, data, 12, payload.length);

    // Checksum of address and data.
    data[data.length - 2] = GWDeviceChecksums.rolandChecksum(
      Arrays.copyOfRange(data, 8, 8 + 4 + payload.length)
    );

    // End of sysex
    data[data.length - 1] = (byte) 0xf7;
    return new SysexMessage(data, data.length);
  }

  private static MidiMessage serializeCommandRequestData(
    final int midiDeviceId,
    final int midiManufacturerId,
    final GWDeviceCommandRequestData command)
    throws InvalidMidiDataException
  {
    final var buffer =
      ByteBuffer.allocate(4)
        .order(BIG_ENDIAN);

    final var data = new byte[18];
    // Sysex status
    data[0] = (byte) 0xf0;
    data[1] = (byte) (midiManufacturerId & 0xff);
    data[2] = (byte) (midiDeviceId & 0xff);
    // "Model ID" GT-1000
    data[3] = (byte) 0x00;
    // "Model ID" GT-1000
    data[4] = (byte) 0x00;
    // "Model ID" GT-1000
    data[5] = (byte) 0x00;
    // "Model ID" GT-1000
    data[6] = (byte) 0x4f;
    // Command ID ("RQ1")
    data[7] = (byte) (command.commandCode() & 0xff);

    buffer.putInt(0, command.address());
    buffer.get(0, data, 8, 4);

    buffer.putInt(0, command.size());
    buffer.get(0, data, 12, 4);

    // Checksum of address and size.
    data[16] = GWDeviceChecksums.rolandChecksum(
      Arrays.copyOfRange(data, 8, 16)
    );

    // End of sysex
    data[17] = (byte) 0xf7;
    return new SysexMessage(data, data.length);
  }
}
