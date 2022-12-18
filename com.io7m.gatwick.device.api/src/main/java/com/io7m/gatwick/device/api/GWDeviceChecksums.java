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


package com.io7m.gatwick.device.api;

import java.util.Objects;

/**
 * Functions to calculate checksums.
 */

public final class GWDeviceChecksums
{
  private GWDeviceChecksums()
  {

  }

  /**
   * The standard Roland checksum function.
   *
   * @param data The input data
   *
   * @return The checksum
   */

  public static byte rolandChecksum(
    final byte[] data)
  {
    Objects.requireNonNull(data, "data");

    int sum = 0;
    for (int index = 0; index < data.length; ++index) {
      sum = sum + (int) data[index];
    }

    sum %= 128;
    return (byte) (128 - sum);
  }
}
