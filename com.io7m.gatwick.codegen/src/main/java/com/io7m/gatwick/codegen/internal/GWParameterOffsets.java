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
import com.io7m.gatwick.codegen.jaxb.StructureReference;

import static com.io7m.gatwick.codegen.internal.GWHexIntegers.parseHex;

/**
 * Functions to calculate parameter offsets.
 */

public final class GWParameterOffsets
{
  private GWParameterOffsets()
  {

  }

  /**
   * The offset of {@code p}
   *
   * @param p The parameter
   *
   * @return the offset of {@code p}
   */

  public static long offsetOf(
    final Object p)
  {
    if (p instanceof ParameterEnumerated pp) {
      return offsetOf(pp);
    }
    if (p instanceof ParameterHighCut pp) {
      return offsetOf(pp);
    }
    if (p instanceof ParameterLowCut pp) {
      return offsetOf(pp);
    }
    if (p instanceof ParameterRate118 pp) {
      return offsetOf(pp);
    }
    if (p instanceof ParameterRate118AndOff pp) {
      return offsetOf(pp);
    }
    if (p instanceof ParameterRate318 pp) {
      return offsetOf(pp);
    }
    if (p instanceof ParameterString pp) {
      return offsetOf(pp);
    }
    if (p instanceof ParameterIntegerMapped pp) {
      return offsetOf(pp);
    }
    if (p instanceof ParameterIntegerDirect pp) {
      return offsetOf(pp);
    }
    if (p instanceof ParameterFractional pp) {
      return offsetOf(pp);
    }
    if (p instanceof StructureReference pp) {
      return offsetOf(pp);
    }
    if (p instanceof ParameterChain pp) {
      return offsetOf(pp);
    }

    throw new IllegalArgumentException(
      "Unrecognized parameter type: %s".formatted(p)
    );
  }

  private static long offsetOf(
    final ParameterChain p)
  {
    return parseHex(p.getOffset());
  }

  private static long offsetOf(
    final StructureReference p)
  {
    return parseHex(p.getOffset());
  }

  private static long offsetOf(
    final ParameterEnumerated p)
  {
    return parseHex(p.getOffset());
  }

  private static long offsetOf(
    final ParameterHighCut p)
  {
    return parseHex(p.getOffset());
  }

  private static long offsetOf(
    final ParameterLowCut p)
  {
    return parseHex(p.getOffset());
  }

  private static long offsetOf(
    final ParameterRate118 p)
  {
    return parseHex(p.getOffset());
  }

  private static long offsetOf(
    final ParameterRate118AndOff p)
  {
    return parseHex(p.getOffset());
  }

  private static long offsetOf(
    final ParameterRate318 p)
  {
    return parseHex(p.getOffset());
  }

  private static long offsetOf(
    final ParameterString p)
  {
    return parseHex(p.getOffset());
  }

  private static long offsetOf(
    final ParameterIntegerDirect p)
  {
    return parseHex(p.getOffset());
  }

  private static long offsetOf(
    final ParameterIntegerMapped p)
  {
    return parseHex(p.getOffset());
  }

  private static long offsetOf(
    final ParameterFractional p)
  {
    return parseHex(p.getOffset());
  }
}
