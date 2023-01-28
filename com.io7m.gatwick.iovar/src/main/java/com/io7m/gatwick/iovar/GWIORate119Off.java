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

import java.util.List;

import static com.io7m.gatwick.iovar.GWIOSerializers.rate119Deserializer;
import static com.io7m.gatwick.iovar.GWIOSerializers.rate119Serializer;

/**
 * An "off" value for rates.
 */

public enum GWIORate119Off implements GWIORate119Type
{
  /**
   * An "off" value for rates.
   */

  OFF;

  private static final GWIORate119Off[] VALUES = values();

  /**
   * Retrieve an off value from the given integer.
   *
   * @param index The integer
   *
   * @return An off value
   *
   * @throws IllegalArgumentException On unrecognized indices
   */

  public static GWIORate119Off ofInt(
    final int index)
    throws IllegalArgumentException
  {
    for (final var v : VALUES) {
      if (v.ordinal() == index) {
        return v;
      }
    }
    throw new IllegalArgumentException(
      "No enumeration value for index %d".formatted(index)
    );
  }

  @Override
  public int toInt()
  {
    return INFO.toInt(this);
  }

  @Override
  public GWIORate119Type next()
  {
    if (this == INFO.last()) {
      return this;
    }
    return INFO.next(this);
  }

  @Override
  public GWIORate119Type previous()
  {
    if (this == INFO.first()) {
      return new GWIORate119Milliseconds(100);
    }
    return INFO.previous(this);
  }

  private static final GWIOEnumerationInfoType<GWIORate119Off> INFO =
    new Info();

  /**
   * @return Enumeration info
   */

  public static GWIOEnumerationInfoType<GWIORate119Off> info()
  {
    return INFO;
  }

  private static final class Info
    implements GWIOEnumerationInfoType<GWIORate119Off>
  {
    private Info()
    {

    }

    @Override
    public Class<GWIORate119Off> enumerationClass()
    {
      return GWIORate119Off.class;
    }

    @Override
    public GWIORate119Off fromInt(
      final int x)
    {
      return ofInt(x);
    }

    @Override
    public int toInt(
      final GWIORate119Off x)
    {
      return x.ordinal();
    }

    @Override
    public String label(
      final GWIORate119Off x)
    {
      return x.toString();
    }

    @Override
    public GWIORate119Off next(
      final GWIORate119Off x)
    {
      return VALUES[x.ordinal() + 1];
    }

    @Override
    public GWIORate119Off previous(
      final GWIORate119Off x)
    {
      return VALUES[x.ordinal() - 1];
    }

    @Override
    public int caseCount()
    {
      return VALUES.length;
    }

    @Override
    public List<GWIORate119Off> valueList()
    {
      return List.of(values());
    }

    @Override
    public GWIOVariableDeserializeType<GWIORate119Off> deserializer()
    {
      return (GWIOVariableDeserializeType<GWIORate119Off>) (Object) rate119Deserializer();
    }

    @Override
    public GWIOVariableSerializeType<GWIORate119Off> serializer()
    {
      return (GWIOVariableSerializeType<GWIORate119Off>) (Object) rate119Serializer();
    }

    @Override
    public int serializeSize()
    {
      return 1;
    }
  }
}
