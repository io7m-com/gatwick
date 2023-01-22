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


package com.io7m.gatwick.tests.iovar;

import com.io7m.gatwick.iovar.GWIORate118Milliseconds;
import com.io7m.gatwick.iovar.GWIORate118Note;
import com.io7m.gatwick.iovar.GWIORate118Type;
import com.io7m.gatwick.iovar.GWIORate318Milliseconds;
import com.io7m.gatwick.iovar.GWIORate318Note;
import com.io7m.gatwick.iovar.GWIORate318Type;
import com.io7m.gatwick.iovar.GWIOSerializers;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.constraints.CharRange;
import net.jqwik.api.constraints.DoubleRange;
import net.jqwik.api.constraints.IntRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class GWSerializersTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(GWSerializersTest.class);

  @Property
  public void testUint8(
    final @ForAll @IntRange(min = 0, max = 127) int x)
  {
    final var buffer =
      ByteBuffer.allocate(1);
    final var s =
      GWIOSerializers.uint8Serializer();
    final var d =
      GWIOSerializers.uint8Deserializer();

    s.serializeTo(buffer, Integer.valueOf(x));
    final var y = d.deserializeFrom(buffer);
    assertEquals(x, y);
  }

  @Property
  public void testUint16(
    final @ForAll @IntRange(min = 0, max = 255) int x)
  {
    final var buffer =
      ByteBuffer.allocate(2);
    final var s =
      GWIOSerializers.uint8As16Serializer();
    final var d =
      GWIOSerializers.uint8As16Deserializer();

    s.serializeTo(buffer, Integer.valueOf(x));
    final var y = d.deserializeFrom(buffer);
    assertEquals(x, y);
  }

  @Property
  public void testUint32(
    final @ForAll @IntRange(min = 0, max = 65535) int x)
  {
    final var buffer =
      ByteBuffer.allocate(4);
    final var s =
      GWIOSerializers.uint16As32Serializer();
    final var d =
      GWIOSerializers.uint16As32Deserializer();

    s.serializeTo(buffer, Integer.valueOf(x));
    final var y = d.deserializeFrom(buffer);
    assertEquals(x, y);
  }

  @Property
  public void testString(
    final @ForAll @CharRange(from = 'a', to = 'z') String x)
  {
    final var buffer =
      ByteBuffer.allocate(x.getBytes(US_ASCII).length);
    final var s =
      GWIOSerializers.stringSerializer();
    final var d =
      GWIOSerializers.stringDeserializer();

    s.serializeTo(buffer, x);
    final var y = d.deserializeFrom(buffer);
    assertEquals(x, y);
  }

  @Property
  public void testPitch(
    final @ForAll @IntRange(min = -24, max = 24) int x)
  {
    final var buffer =
      ByteBuffer.allocate(1);
    final var s =
      GWIOSerializers.integerMappedSerializer(
        GWIOSerializers.uint8Serializer(),
        -24,
        24,
        8,
        56
      );
    final var d =
      GWIOSerializers.integerMappedDeserializer(
        GWIOSerializers.uint8Deserializer(),
        -24,
        24,
        8,
        56
      );

    s.serializeTo(buffer, Integer.valueOf(x));
    final var y = d.deserializeFrom(buffer);
    assertEquals(x, y);
  }

  @Property
  public void testTone(
    final @ForAll @IntRange(min = -50, max = 50) int x)
  {
    final var buffer =
      ByteBuffer.allocate(1);
    final var s =
      GWIOSerializers.integerMappedSerializer(
        GWIOSerializers.uint8Serializer(),
        -50,
        50,
        14,
        114
      );
    final var d =
      GWIOSerializers.integerMappedDeserializer(
        GWIOSerializers.uint8Deserializer(),
        -50,
        50,
        14,
        114
      );

    s.serializeTo(buffer, Integer.valueOf(x));
    final var y = d.deserializeFrom(buffer);
    assertEquals(x, y);
  }

  @Property
  public void testPreDelay(
    final @ForAll @DoubleRange(min = 0.0, max = 40.0) double x)
  {
    final var buffer =
      ByteBuffer.allocate(1);
    final var s =
      GWIOSerializers.fractionalSerializer(
        GWIOSerializers.uint8Serializer(),
        0.0,
        40.0,
        0,
        80
      );
    final var d =
      GWIOSerializers.fractionalDeserializer(
        GWIOSerializers.uint8Deserializer(),
        0.0,
        40.0,
        0,
        80
      );

    s.serializeTo(buffer, Double.valueOf(x));
    final var y = d.deserializeFrom(buffer);
    final var diff = Math.abs(y - x);
    LOG.debug("{} -> {} ({})", x, y, diff);
    assertTrue(diff <= 1.0);
  }

  @Provide
  public Arbitrary<GWIORate118Type> rate118()
  {
    return Arbitraries.integers()
      .between(0, 118)
      .map(x -> {
        if (x <= 100) {
          return new GWIORate118Milliseconds(x);
        } else {
          return GWIORate118Note.ofInt(x);
        }
      });
  }

  @Property
  public void testRate118(
    final @ForAll("rate118") GWIORate118Type x)
  {
    final var buffer =
      ByteBuffer.allocate(1);
    final var s =
      GWIOSerializers.rate118Serializer();
    final var d =
      GWIOSerializers.rate118Deserializer();

    s.serializeTo(buffer, x);
    final var y = d.deserializeFrom(buffer);
    assertEquals(x, y);
  }

  @Provide
  public Arbitrary<GWIORate318Type> rate318()
  {
    return Arbitraries.integers()
      .between(0, 318)
      .map(x -> {
        if (x <= 300) {
          return new GWIORate318Milliseconds(x);
        } else {
          return GWIORate318Note.ofInt(x);
        }
      });
  }

  @Property
  public void testRate318(
    final @ForAll("rate318") GWIORate318Type x)
  {
    final var buffer =
      ByteBuffer.allocate(4);
    final var s =
      GWIOSerializers.rate318Serializer();
    final var d =
      GWIOSerializers.rate318Deserializer();

    s.serializeTo(buffer, x);
    final var y = d.deserializeFrom(buffer);
    assertEquals(x, y);
  }
}
