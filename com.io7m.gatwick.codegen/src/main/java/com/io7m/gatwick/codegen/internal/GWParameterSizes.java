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

import com.io7m.gatwick.codegen.jaxb.ParameterChain;
import com.io7m.gatwick.codegen.jaxb.ParameterEnumerated;
import com.io7m.gatwick.codegen.jaxb.ParameterFractional;
import com.io7m.gatwick.codegen.jaxb.ParameterHighCut;
import com.io7m.gatwick.codegen.jaxb.ParameterIntegerDirect;
import com.io7m.gatwick.codegen.jaxb.ParameterIntegerMapped;
import com.io7m.gatwick.codegen.jaxb.ParameterLowCut;
import com.io7m.gatwick.codegen.jaxb.ParameterRate118;
import com.io7m.gatwick.codegen.jaxb.ParameterRate118AndOff;
import com.io7m.gatwick.codegen.jaxb.ParameterRate318;
import com.io7m.gatwick.codegen.jaxb.ParameterString;

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
    final Object p)
  {
    if (p instanceof ParameterEnumerated pp) {
      return sizeOf(pp);
    }
    if (p instanceof ParameterHighCut pp) {
      return sizeOf(pp);
    }
    if (p instanceof ParameterLowCut pp) {
      return sizeOf(pp);
    }
    if (p instanceof ParameterRate118 pp) {
      return sizeOf(pp);
    }
    if (p instanceof ParameterRate118AndOff pp) {
      return sizeOf(pp);
    }
    if (p instanceof ParameterRate318 pp) {
      return sizeOf(pp);
    }
    if (p instanceof ParameterString pp) {
      return sizeOf(pp);
    }
    if (p instanceof ParameterIntegerMapped pp) {
      return sizeOf(pp);
    }
    if (p instanceof ParameterIntegerDirect pp) {
      return sizeOf(pp);
    }
    if (p instanceof ParameterFractional pp) {
      return sizeOf(pp);
    }
    if (p instanceof ParameterChain pp) {
      return sizeOf(pp);
    }

    throw new IllegalArgumentException(
      "Unrecognized parameter type: %s".formatted(p)
    );
  }

  private static long sizeOf(
    final ParameterChain p)
  {
    return 49L;
  }

  private static long sizeOf(
    final ParameterEnumerated p)
  {
    return 1L;
  }

  private static long sizeOf(
    final ParameterHighCut p)
  {
    return 1L;
  }

  private static long sizeOf(
    final ParameterLowCut p)
  {
    return 1L;
  }

  private static long sizeOf(
    final ParameterRate118 p)
  {
    return 1L;
  }

  private static long sizeOf(
    final ParameterRate118AndOff p)
  {
    return 1L;
  }

  private static long sizeOf(
    final ParameterRate318 p)
  {
    return 2L;
  }

  private static long sizeOf(
    final ParameterString p)
  {
    return p.getLength();
  }

  private static long sizeOf(
    final ParameterIntegerDirect p)
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
    final ParameterIntegerMapped p)
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
    final ParameterFractional p)
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
