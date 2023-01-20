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

/**
 * Information about an enumeration type.
 *
 * @param <T> The enumeration type
 */

public interface GWIOEnumerationInfoType<T extends Enum<T> & Comparable<T>>
{
  /**
   * @return The enumeration class
   */

  Class<T> enumerationClass();

  /**
   * @param x The integer value
   *
   * @return The enumeration case of the given integer
   */

  T fromInt(int x);

  /**
   * @param x The enumeration case
   *
   * @return The integer index of this enumeration constant
   */

  int toInt(T x);

  /**
   * @param x The enumeration case
   *
   * @return The readable label of this enumeration constant
   */

  String label(T x);

  /**
   * @param x The enumeration case
   *
   * @return The next enumeration constant (wrapping around to the first)
   */

  T next(T x);

  /**
   * @param x The enumeration case
   *
   * @return The previous enumeration constant (wrapping around to the last)
   */

  T previous(T x);

  /**
   * @return The number of cases
   */

  int caseCount();

  /**
   * @return The values in this enumeration
   */

  List<T> valueList();

  /**
   * @return The first enum value
   */

  default T first()
  {
    final var values = this.valueList();
    return values.get(0);
  }

  /**
   * @return The last enum value
   */

  default T last()
  {
    final var values = this.valueList();
    return values.get(values.size() - 1);
  }

  /**
   * @return A deserializer for the enum
   */

  GWIOVariableDeserializeType<T> deserializer();

  /**
   * @return A serializer for the enum
   */

  GWIOVariableSerializeType<T> serializer();

  /**
   * @return The serialization size for the enum
   */

  int serializeSize();

}
