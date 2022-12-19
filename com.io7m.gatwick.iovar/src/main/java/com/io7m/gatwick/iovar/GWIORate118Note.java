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

/**
 * Musical durations for rates.
 */

public enum GWIORate118Note implements GWIORate118Type,
  GWIOExtendedEnumerationType<GWIORate118Note>
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
    for (final var v : VALUES) {
      if (v.ordinal() == index) {
        return v;
      }
    }
    throw new IllegalArgumentException(
      "No enumeration value for index %d".formatted(index)
    );
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

  @Override
  public int toInt()
  {
    return this.ordinal() + 100;
  }

  @Override
  public String label()
  {
    return this.toString();
  }

  @Override
  public GWIORate118Note next()
  {
    return ofInt((this.toInt() + 1) % VALUES.length);
  }

  @Override
  public GWIORate118Note previous()
  {
    return ofInt((this.toInt() - 1) % VALUES.length);
  }
}
