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

package com.io7m.gatwick.iovar;

import java.util.Objects;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Standard serializers.
 */

public final class GWIOSerializers
{
  private GWIOSerializers()
  {

  }

  /**
   * @return A serializer for strings
   */

  public static GWIOVariableSerializeType<String> stringSerializer()
  {
    return (buffer, value) -> {
      buffer.put(0, value.getBytes(US_ASCII));
    };
  }

  /**
   * @return A deserializer for strings
   */

  public static GWIOVariableDeserializeType<String> stringDeserializer()
  {
    return buffer -> {
      final var data = new byte[buffer.remaining()];
      buffer.get(0, data);
      return new String(data, US_ASCII);
    };
  }

  /**
   * A serializer for values in the range [0,127]
   *
   * @return A serializer
   */

  public static GWIOVariableSerializeType<Integer> uint8Serializer()
  {
    return (buffer, value) -> {
      buffer.put(0, (byte) (value.intValue() & 0xff));
    };
  }

  /**
   * A deserializer for values in the range [0,127]
   *
   * @return A serializer
   */

  public static GWIOVariableDeserializeType<Integer> uint8Deserializer()
  {
    return buffer -> Integer.valueOf((int) buffer.get(0) & 0xff);
  }

  /**
   * A serializer for values greater than 127 but less than 255.
   *
   * @return A serializer
   */

  public static GWIOVariableSerializeType<Integer> uint8As16Serializer()
  {
    return (buffer, value) -> {
      final var x = value.intValue();
      final var xmsb = (x >> 4) & 0b1111;
      final var xlsb = (x & 0b1111);
      buffer.put(0, (byte) xmsb);
      buffer.put(1, (byte) xlsb);
    };
  }

  /**
   * A serializer for values greater than 127 but less than 255.
   *
   * @return A serializer
   */

  public static GWIOVariableDeserializeType<Integer> uint8As16Deserializer()
  {
    return buffer -> {
      final var xmsb = (int) buffer.get(0) & 0b1111;
      final var xlsb = (int) buffer.get(1) & 0b1111;
      return Integer.valueOf((xmsb << 4) | xlsb);
    };
  }

  /**
   * A serializer for values greater than 255.
   *
   * @return A serializer
   */

  public static GWIOVariableSerializeType<Integer> uint16As32Serializer()
  {
    return (buffer, value) -> {
      final var x = value.intValue();
      final var x0 = (x >> 12) & 0b1111;
      final var x1 = (x >> 8) & 0b1111;
      final var x2 = (x >> 4) & 0b1111;
      final var x3 = x & 0b1111;
      buffer.put(0, (byte) x0);
      buffer.put(1, (byte) x1);
      buffer.put(2, (byte) x2);
      buffer.put(3, (byte) x3);
    };
  }

  /**
   * A serializer for values greater than 255.
   *
   * @return A serializer
   */

  public static GWIOVariableDeserializeType<Integer> uint16As32Deserializer()
  {
    return buffer -> {
      final var x0 = (int) buffer.get(0) & 0b1111;
      final var x1 = (int) buffer.get(1) & 0b1111;
      final var x2 = (int) buffer.get(2) & 0b1111;
      final var x3 = (int) buffer.get(3) & 0b1111;

      var r = 0;
      r |= (x0 << 12);
      r |= (x1 << 8);
      r |= (x2 << 4);
      r |= x3;

      return Integer.valueOf(r);
    };
  }

  /**
   * @return A serializer for Rate118 values
   */

  public static GWIOVariableSerializeType<GWIORate118Type> rate118Serializer()
  {
    return (buffer, value) -> {
      if (value instanceof GWIORate118Milliseconds milliseconds) {
        buffer.put(0, (byte) (milliseconds.value() & 0xff));
      } else if (value instanceof GWIORate118Note note) {
        buffer.put(0, (byte) (note.toInt() & 0xff));
      } else {
        throw new IllegalStateException();
      }
    };
  }

  /**
   * @return A serializer for Rate118 values
   */

  public static GWIOVariableDeserializeType<GWIORate118Type> rate118Deserializer()
  {
    return buffer -> {
      final var i = (int) buffer.get(0);
      if (i <= 100) {
        return new GWIORate118Milliseconds(i);
      }
      return GWIORate118Note.ofInt(i);
    };
  }

  /**
   * @return A serializer for Rate318 values
   */

  public static GWIOVariableSerializeType<GWIORate318Type> rate318Serializer()
  {
    return (buffer, value) -> {
      if (value instanceof GWIORate318Milliseconds milliseconds) {
        uint8As16Serializer().serializeTo(
          buffer,
          Integer.valueOf(milliseconds.value()));
      } else if (value instanceof GWIORate318Note note) {
        uint8As16Serializer().serializeTo(
          buffer,
          Integer.valueOf(note.toInt()));
      } else {
        throw new IllegalStateException();
      }
    };
  }

  /**
   * @return A serializer for Rate318 values
   */

  public static GWIOVariableDeserializeType<GWIORate318Type> rate318Deserializer()
  {
    return buffer -> {
      final var i = uint8As16Deserializer().deserializeFrom(buffer).intValue();
      if (i <= 300) {
        return new GWIORate318Milliseconds(i);
      }
      return GWIORate318Note.ofInt(i);
    };
  }

  /**
   * @return A serializer for Rate119 values
   */

  public static GWIOVariableSerializeType<GWIORate119Type> rate119Serializer()
  {
    return (buffer, value) -> {
      if (value instanceof GWIORate119Off off) {
        uint8As16Serializer().serializeTo(buffer, Integer.valueOf(0));
      } else if (value instanceof GWIORate119Milliseconds milliseconds) {
        uint8As16Serializer().serializeTo(
          buffer,
          Integer.valueOf(milliseconds.value() + 1));
      } else if (value instanceof GWIORate119Note note) {
        uint8As16Serializer().serializeTo(
          buffer,
          Integer.valueOf(note.toInt()));
      } else {
        throw new IllegalStateException();
      }
    };
  }

  /**
   * @return A serializer for Rate119 values
   */

  public static GWIOVariableDeserializeType<GWIORate119Type> rate119Deserializer()
  {
    return buffer -> {
      final var i = uint8As16Deserializer().deserializeFrom(buffer).intValue();
      if (i == 0) {
        return GWIORate119Off.OFF;
      }
      if (i >= 1 && i <= 101) {
        return new GWIORate119Milliseconds(i);
      }
      return GWIORate119Note.ofInt(i);
    };
  }

  /**
   * An integer serializer that maps a logical range to a physical range. The
   * two ranges must be the same size.
   *
   * @param base            The base serializer
   * @param logicalMinimum  The logical lower bound
   * @param logicalMaximum  The logical upper bound
   * @param physicalMinimum The physical lower bound
   * @param physicalMaximum The physical upper bound
   *
   * @return A serializer
   */

  public static GWIOVariableSerializeType<Integer> integerMappedSerializer(
    final GWIOVariableSerializeType<Integer> base,
    final int logicalMinimum,
    final int logicalMaximum,
    final int physicalMinimum,
    final int physicalMaximum)
  {
    Objects.requireNonNull(base, "base");

    final var diffA =
      logicalMaximum - logicalMinimum;
    final var diffB =
      physicalMaximum - physicalMinimum;

    if (diffA != diffB) {
      throw new IllegalArgumentException(
        String.format(
          "Logical maximum %d - minimum %d (%d) != Physical maximum %d - minimum %d (%d)",
          Integer.valueOf(logicalMaximum),
          Integer.valueOf(logicalMinimum),
          Integer.valueOf(diffA),
          Integer.valueOf(physicalMaximum),
          Integer.valueOf(physicalMinimum),
          Integer.valueOf(diffB)
        )
      );
    }

    final var mapDelta = logicalMinimum - physicalMinimum;
    return (buffer, value) -> {
      final var physical = value.intValue() - mapDelta;
      base.serializeTo(buffer, Integer.valueOf(physical));
    };
  }

  /**
   * An integer deserializer that maps a logical range to a physical range. The
   * two ranges must be the same size.
   *
   * @param base            The base deserializer
   * @param logicalMinimum  The logical lower bound
   * @param logicalMaximum  The logical upper bound
   * @param physicalMinimum The physical lower bound
   * @param physicalMaximum The physical upper bound
   *
   * @return A deserializer
   */

  public static GWIOVariableDeserializeType<Integer> integerMappedDeserializer(
    final GWIOVariableDeserializeType<Integer> base,
    final int logicalMinimum,
    final int logicalMaximum,
    final int physicalMinimum,
    final int physicalMaximum)
  {
    Objects.requireNonNull(base, "base");

    final var diffA =
      logicalMaximum - logicalMinimum;
    final var diffB =
      physicalMaximum - physicalMinimum;

    if (diffA != diffB) {
      throw new IllegalArgumentException(
        String.format(
          "Logical maximum %d - minimum %d (%d) != Physical maximum %d - minimum %d (%d)",
          Integer.valueOf(logicalMaximum),
          Integer.valueOf(logicalMinimum),
          Integer.valueOf(diffA),
          Integer.valueOf(physicalMaximum),
          Integer.valueOf(physicalMinimum),
          Integer.valueOf(diffB)
        )
      );
    }

    final var mapDelta = logicalMinimum - physicalMinimum;
    return buffer -> {
      final var physical = base.deserializeFrom(buffer);
      return Integer.valueOf(physical.intValue() + mapDelta);
    };
  }

  /**
   * A fractional serializer that maps a logical range to a physical range.
   *
   * @param base            The base serializer
   * @param logicalMinimum  The logical lower bound
   * @param logicalMaximum  The logical upper bound
   * @param physicalMinimum The physical lower bound
   * @param physicalMaximum The physical upper bound
   *
   * @return A serializer
   */

  public static GWIOVariableSerializeType<Double> fractionalSerializer(
    final GWIOVariableSerializeType<Integer> base,
    final double logicalMinimum,
    final double logicalMaximum,
    final int physicalMinimum,
    final int physicalMaximum)
  {
    Objects.requireNonNull(base, "base");

    final var logicalDelta =
      (int) logicalMaximum - (int) logicalMinimum;
    final var physicalDelta =
      physicalMaximum - physicalMinimum;
    final var scale =
      physicalDelta / logicalDelta;

    return (buffer, value) -> {
      final var scaled = (int) value.doubleValue() * scale;
      base.serializeTo(buffer, Integer.valueOf(scaled));
    };
  }

  /**
   * A fractional deserializer that maps a logical range to a physical range.
   *
   * @param base            The base serializer
   * @param logicalMinimum  The logical lower bound
   * @param logicalMaximum  The logical upper bound
   * @param physicalMinimum The physical lower bound
   * @param physicalMaximum The physical upper bound
   *
   * @return A deserializer
   */

  public static GWIOVariableDeserializeType<Double> fractionalDeserializer(
    final GWIOVariableDeserializeType<Integer> base,
    final double logicalMinimum,
    final double logicalMaximum,
    final int physicalMinimum,
    final int physicalMaximum)
  {
    Objects.requireNonNull(base, "base");

    final var logicalDelta =
      (double) logicalMaximum - (double) logicalMinimum;
    final var physicalDelta =
      (double) physicalMaximum - (double) physicalMinimum;
    final var scale =
      logicalDelta / physicalDelta;

    return buffer -> {
      final var physical = base.deserializeFrom(buffer);
      return Double.valueOf(scale * physical.doubleValue());
    };
  }
}
