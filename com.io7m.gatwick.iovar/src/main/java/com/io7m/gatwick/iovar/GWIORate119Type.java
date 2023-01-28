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
 * The type of rate values that may be either milliseconds or musical durations.
 */

public sealed interface GWIORate119Type
  permits GWIORate119Milliseconds, GWIORate119Note, GWIORate119Off
{
  /**
   * Derive a Rate119 value from the given integer.
   *
   * @param x The integer
   *
   * @return A Rate119 value
   */

  static GWIORate119Type ofInt(
    final int x)
  {
    if (x >= 0 && x <= 100) {
      return new GWIORate119Milliseconds(x);
    }
    return GWIORate119Note.ofInt(x);
  }

  /**
   * @return This value as an integer
   *
   * @see #ofInt(int)
   */

  int toInt();

  /**
   * @return The next Rate119 value
   */

  GWIORate119Type next();

  /**
   * @return The previous Rate119 value
   */

  GWIORate119Type previous();
}
