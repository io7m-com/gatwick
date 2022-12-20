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

/**
 * Request to write {@code data} to address {@code address}.
 *
 * @param address The address
 * @param data    The data
 */

public record GWDeviceCommandSetData(
  int address,
  byte[] data)
  implements GWDeviceCommandType<GWDeviceResponseOK>
{
  @Override
  public int commandCode()
  {
    return 0x12;
  }

  @Override
  public Class<GWDeviceResponseOK> responseClass()
  {
    return GWDeviceResponseOK.class;
  }

  @Override
  public String toString()
  {
    return String.format(
      "[GWDeviceCommandSetData 0x%08x [%d bytes]]",
      Integer.valueOf(this.address),
      Integer.valueOf(this.data.length)
    );
  }
}
