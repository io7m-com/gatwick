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
 * An "off" value for rates.
 */

public enum GWIORate119Off implements GWIORate119Type,
  GWIOExtendedEnumerationType<GWIORate119Off>
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
    return 0;
  }

  @Override
  public String label()
  {
    return this.toString();
  }

  @Override
  public GWIORate119Off next()
  {
    return OFF;
  }

  @Override
  public GWIORate119Off previous()
  {
    return OFF;
  }
}