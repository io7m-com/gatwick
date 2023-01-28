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

package com.io7m.gatwick.gui.internal.preset;

import com.io7m.digal.core.DialValueConverterDiscreteType;
import com.io7m.gatwick.iovar.GWIORate118Type;

final class GWIORate118ValueConverter
  implements DialValueConverterDiscreteType
{
  GWIORate118ValueConverter()
  {

  }

  @Override
  public double convertToDial(
    final long x)
  {
    return (double) x / 118.0;
  }

  @Override
  public long convertFromDial(
    final double x)
  {
    return Math.round(x * 118.0);
  }

  @Override
  public long convertedNext(
    final long x)
  {
    return (long) GWIORate118Type.ofInt((int) x)
      .next()
      .toInt();
  }

  @Override
  public long convertedPrevious(
    final long x)
  {
    return (long) GWIORate118Type.ofInt((int) x)
      .previous()
      .toInt();
  }
}
