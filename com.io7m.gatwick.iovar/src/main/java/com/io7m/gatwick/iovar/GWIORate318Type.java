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

public sealed interface GWIORate318Type
  permits GWIORate318Milliseconds,
  GWIORate318Note
{
  /**
   * Derive a Rate318 value from the given integer.
   *
   * @param x The integer
   *
   * @return A Rate318 value
   */

  static GWIORate318Type ofInt(
    final int x)
  {
    if (x >= 0 && x <= 100) {
      return new GWIORate318Milliseconds(x);
    }
    return GWIORate318Note.ofInt(x);
  }

  /**
   * @return This value as an integer
   *
   * @see #ofInt(int)
   */

  int toInt();

  /**
   * @return The next Rate318 value
   */

  GWIORate318Type next();

  /**
   * @return The previous Rate318 value
   */

  GWIORate318Type previous();
}
