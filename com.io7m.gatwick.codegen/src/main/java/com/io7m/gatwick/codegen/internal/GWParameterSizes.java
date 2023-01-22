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


package com.io7m.gatwick.codegen.internal;

import com.io7m.gatwick.codegen.jaxb.ParameterBase;
import com.io7m.gatwick.codegen.jaxb.ParameterChainType;
import com.io7m.gatwick.codegen.jaxb.ParameterEnumeratedType;
import com.io7m.gatwick.codegen.jaxb.ParameterFractionalType;
import com.io7m.gatwick.codegen.jaxb.ParameterHighCutType;
import com.io7m.gatwick.codegen.jaxb.ParameterIntegerDirectType;
import com.io7m.gatwick.codegen.jaxb.ParameterIntegerMappedType;
import com.io7m.gatwick.codegen.jaxb.ParameterLowCutType;
import com.io7m.gatwick.codegen.jaxb.ParameterRate118AndOffType;
import com.io7m.gatwick.codegen.jaxb.ParameterRate118Type;
import com.io7m.gatwick.codegen.jaxb.ParameterRate318Type;
import com.io7m.gatwick.codegen.jaxb.ParameterStringType;

import java.math.BigInteger;

/**
 * Functions to calculate structure sizes.
 */

public final class GWParameterSizes
{
  private static final BigInteger BIG_128 =
    BigInteger.valueOf(128L);
  private static final BigInteger BIG_1000 =
    BigInteger.valueOf(1000L);

  private GWParameterSizes()
  {

  }

  /**
   * The size of {@code p}
   *
   * @param p The parameter
   *
   * @return the size of {@code p}
   */

  public static long sizeOf(
    final ParameterBase p)
  {
    if (p instanceof ParameterEnumeratedType pp) {
      return sizeOf(pp);
    }
    if (p instanceof ParameterHighCutType pp) {
      return sizeOf(pp);
    }
    if (p instanceof ParameterLowCutType pp) {
      return sizeOf(pp);
    }
    if (p instanceof ParameterRate118Type pp) {
      return sizeOf(pp);
    }
    if (p instanceof ParameterRate118AndOffType pp) {
      return sizeOf(pp);
    }
    if (p instanceof ParameterRate318Type pp) {
      return sizeOf(pp);
    }
    if (p instanceof ParameterStringType pp) {
      return sizeOf(pp);
    }
    if (p instanceof ParameterIntegerMappedType pp) {
      return sizeOf(pp);
    }
    if (p instanceof ParameterIntegerDirectType pp) {
      return sizeOf(pp);
    }
    if (p instanceof ParameterFractionalType pp) {
      return sizeOf(pp);
    }
    if (p instanceof ParameterChainType pp) {
      return sizeOf(pp);
    }

    throw new IllegalArgumentException(
      "Unrecognized parameter type: %s".formatted(p)
    );
  }

  private static long sizeOf(
    final ParameterChainType p)
  {
    return 49L;
  }

  private static long sizeOf(
    final ParameterEnumeratedType p)
  {
    return 1L;
  }

  private static long sizeOf(
    final ParameterHighCutType p)
  {
    return 1L;
  }

  private static long sizeOf(
    final ParameterLowCutType p)
  {
    return 1L;
  }

  private static long sizeOf(
    final ParameterRate118Type p)
  {
    return 1L;
  }

  private static long sizeOf(
    final ParameterRate118AndOffType p)
  {
    return 1L;
  }

  private static long sizeOf(
    final ParameterRate318Type p)
  {
    return 4L;
  }

  private static long sizeOf(
    final ParameterStringType p)
  {
    return p.getLength();
  }

  private static long sizeOf(
    final ParameterIntegerDirectType p)
  {
    if (p.getMaxInclusive().compareTo(BIG_1000) >= 0) {
      return 4L;
    }
    if (p.getMaxInclusive().compareTo(BIG_128) >= 0) {
      return 2L;
    }
    return 1L;
  }

  private static long sizeOf(
    final ParameterIntegerMappedType p)
  {
    if (p.getPhysicalMaxInclusive().compareTo(BIG_1000) >= 0) {
      return 4L;
    }
    if (p.getPhysicalMaxInclusive().compareTo(BIG_128) >= 0) {
      return 2L;
    }
    return 1L;
  }

  private static long sizeOf(
    final ParameterFractionalType p)
  {
    if (p.getPhysicalMaxInclusive().compareTo(BIG_1000) >= 0) {
      return 4L;
    }
    if (p.getPhysicalMaxInclusive().compareTo(BIG_128) >= 0) {
      return 2L;
    }
    return 1L;
  }
}
