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

import static com.io7m.gatwick.iovar.GWIOSerializers.rate118Deserializer;
import static com.io7m.gatwick.iovar.GWIOSerializers.rate118Serializer;

/**
 * Musical durations for rates.
 */

public enum GWIORate118Note implements GWIORate118Type
{
  /**
   * The double whole note duration.
   */

  RATE_DOUBLE_WHOLENOTE,

  /**
   * The dotted whole note duration.
   */

  RATE_DOTTED_WHOLENOTE,

  /**
   * The whole note duration.
   */

  RATE_WHOLE_NOTE,

  /**
   * The dotted half note duration.
   */

  RATE_DOTTED_HALF_NOTE,

  /**
   * The triplet whole note duration.
   */

  RATE_TRIPLET_WHOLE_NOTE,

  /**
   * The half note duration.
   */

  RATE_HALF_NOTE,

  /**
   * The dotted quarter note duration.
   */

  RATE_DOTTED_QUARTER_NOTE,

  /**
   * The triplet half note duration.
   */

  RATE_TRIPLET_HALF_NOTE,

  /**
   * The quarter note duration.
   */

  RATE_QUARTER_NOTE,

  /**
   * The dotted 8th note duration.
   */

  RATE_DOTTED_8TH_NOTE,

  /**
   * The triplet quarter note duration.
   */

  RATE_TRIPLET_QUARTER_NOTE,

  /**
   * The 8th note duration.
   */

  RATE_8TH_NOTE,

  /**
   * The dotted 16th note duration.
   */

  RATE_DOTTED_16TH_NOTE,

  /**
   * The triplet 8th note duration.
   */

  RATE_TRIPLET_8TH_NOTE,

  /**
   * The 16th note duration.
   */

  RATE_16TH_NOTE,

  /**
   * The dotted 32nd note duration.
   */

  RATE_DOTTED_32ND_NOTE,

  /**
   * The triplet 16th note duration.
   */

  RATE_TRIPLET_16TH_NOTE,

  /**
   * The 32nd note duration.
   */

  RATE_32ND_NOTE;

  private static final GWIORate118Note[] VALUES = values();

  /**
   * Retrieve a value from the given integer.
   *
   * @param index The integer
   *
   * @return A value
   *
   * @throws IllegalArgumentException On unrecognized indices
   */

  public static GWIORate118Note ofInt(
    final int index)
  {
    final var trans = index - 101;
    for (final var v : VALUES) {
      final var ord = v.ordinal();
      if (ord == trans) {
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
  public GWIORate118Type next()
  {
    if (this == INFO.last()) {
      return this;
    }
    return INFO.next(this);
  }

  @Override
  public GWIORate118Type previous()
  {
    if (this == INFO.first()) {
      return new GWIORate118Milliseconds(100);
    }
    return INFO.previous(this);
  }

  /**
   * @return The first enum value
   */

  public static GWIORate118Note first()
  {
    return RATE_DOUBLE_WHOLENOTE;
  }

  /**
   * @return The last enum value
   */

  public static GWIORate118Note last()
  {
    return RATE_32ND_NOTE;
  }

  private static final GWIOEnumerationInfoType<GWIORate118Note> INFO =
    new Info();

  /**
   * @return Enumeration info
   */

  public static GWIOEnumerationInfoType<GWIORate118Note> info()
  {
    return INFO;
  }

  /**
   * @return The graphic label for the note
   */

  public String graphicLabel()
  {
    return switch (this) {
      case RATE_DOUBLE_WHOLENOTE -> "\uE0A0 DOUBLE WHOLE";
      case RATE_DOTTED_WHOLENOTE -> "∙\uD834\uDD5D DOTTED WHOLE";
      case RATE_WHOLE_NOTE -> "\uD834\uDD5D WHOLE";
      case RATE_DOTTED_HALF_NOTE -> "∙\uD834\uDD5E DOTTED HALF";
      case RATE_TRIPLET_WHOLE_NOTE -> "3\uD834\uDD5D TRIPLET WHOLE";
      case RATE_HALF_NOTE -> "∙\uD834\uDD5E HALF";
      case RATE_DOTTED_QUARTER_NOTE -> "∙♩ DOTTED QUARTER";
      case RATE_TRIPLET_HALF_NOTE -> "3\uD834\uDD5E TRIPLET HALF";
      case RATE_QUARTER_NOTE -> "♩ QUARTER";
      case RATE_DOTTED_8TH_NOTE -> "∙♪ DOTTED 8TH";
      case RATE_TRIPLET_QUARTER_NOTE -> "3♩ TRIPLET QUARTER";
      case RATE_8TH_NOTE -> "♪ 8TH";
      case RATE_DOTTED_16TH_NOTE -> "∙♬ DOTTED 16TH";
      case RATE_TRIPLET_8TH_NOTE -> "3♪ TRIPLET 8TH";
      case RATE_16TH_NOTE -> "♬ 16TH";
      case RATE_DOTTED_32ND_NOTE -> "∙\uD834\uDD62 DOTTED 32ND";
      case RATE_TRIPLET_16TH_NOTE -> "3♬ TRIPLET 16TH";
      case RATE_32ND_NOTE -> "\uD834\uDD62 32ND";
    };
  }

  private static final class Info
    implements GWIOEnumerationInfoType<GWIORate118Note>
  {
    private Info()
    {

    }

    @Override
    public Class<GWIORate118Note> enumerationClass()
    {
      return GWIORate118Note.class;
    }

    @Override
    public GWIORate118Note fromInt(
      final int x)
    {
      return ofInt(x);
    }

    @Override
    public int toInt(
      final GWIORate118Note x)
    {
      return x.ordinal() + 101;
    }

    @Override
    public String label(
      final GWIORate118Note x)
    {
      return x.toString();
    }

    @Override
    public GWIORate118Note next(
      final GWIORate118Note x)
    {
      return VALUES[x.ordinal() + 1];
    }

    @Override
    public GWIORate118Note previous(
      final GWIORate118Note x)
    {
      return VALUES[x.ordinal() - 1];
    }

    @Override
    public int caseCount()
    {
      return VALUES.length;
    }

    @Override
    public List<GWIORate118Note> valueList()
    {
      return List.of(values());
    }

    @Override
    public GWIOVariableDeserializeType<GWIORate118Note> deserializer()
    {
      return (GWIOVariableDeserializeType<GWIORate118Note>) (Object) rate118Deserializer();
    }

    @Override
    public GWIOVariableSerializeType<GWIORate118Note> serializer()
    {
      return (GWIOVariableSerializeType<GWIORate118Note>) (Object) rate118Serializer();
    }

    @Override
    public int serializeSize()
    {
      return 1;
    }
  }
}
