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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Functions to retrieve information about enums reflectively.
 */

public final class GWIOEnumerationInfo
{
  private GWIOEnumerationInfo()
  {

  }

  /**
   * Retrieve info for the given enum.
   *
   * @param valueClass The enum class
   * @param <T>        The type
   *
   * @return The info
   *
   * @throws IllegalStateException If the enum class does not define a static method
   *                               called {@code info} that returns a value of
   *                               type {@code GWIOEnumerationInfoType}
   */

  public static <T extends Enum<T>> GWIOEnumerationInfoType<T> findInfo(
    final Class<T> valueClass)
    throws IllegalStateException
  {
    Objects.requireNonNull(valueClass, "valueClass");

    final Method infoMethod;
    try {
      infoMethod = valueClass.getMethod("info");
    } catch (final NoSuchMethodException e) {
      throw new IllegalStateException(e);
    }

    final GWIOEnumerationInfoType<T> enumInfo;
    try {
      enumInfo = (GWIOEnumerationInfoType<T>) infoMethod.invoke(null);
    } catch (final IllegalAccessException | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }

    return enumInfo;
  }
}
