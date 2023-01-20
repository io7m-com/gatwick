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


package com.io7m.gatwick.controller.main.internal;

import com.io7m.gatwick.controller.api.GWOnOffValue;
import com.io7m.gatwick.controller.api.GWPatchDistTypeValue;
import com.io7m.gatwick.controller.api.GWPatchEffectBlockDistortionType;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchDist;
import com.io7m.gatwick.device.api.GWDeviceException;
import com.io7m.gatwick.iovar.GWIOVariableType;

import java.util.List;

final class GWPatchEffectBlockDistortion
  extends GWPatchEffectBlock
  implements GWPatchEffectBlockDistortionType
{
  private final StructPatchDist dist;

  GWPatchEffectBlockDistortion(
    final StructPatchDist s)
  {
    super(List.of(
      s.f_sw,
      s.f_type,
      s.f_drive,
      s.f_tone,
      s.f_level,
      s.f_bottom,
      s.f_direct_mix,
      s.f_solo_sw,
      s.f_solo_level
    ));

    this.dist = s;
  }

  @Override
  public GWIOVariableType<GWOnOffValue> enabled()
  {
    return this.dist.f_sw;
  }

  @Override
  public void readFromDevice()
    throws InterruptedException, GWDeviceException
  {
    this.dist.readFromDevice();
  }

  @Override
  public GWIOVariableType<GWPatchDistTypeValue> type()
  {
    return this.dist.f_type;
  }
}
