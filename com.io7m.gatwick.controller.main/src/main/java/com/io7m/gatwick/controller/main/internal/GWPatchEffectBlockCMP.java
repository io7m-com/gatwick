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
import com.io7m.gatwick.controller.api.GWPatchCompRatioValue;
import com.io7m.gatwick.controller.api.GWPatchCompTypeValue;
import com.io7m.gatwick.controller.api.GWPatchEffectBlockCMPType;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchComp;
import com.io7m.gatwick.device.api.GWDeviceException;
import com.io7m.gatwick.iovar.GWIOVariableType;

import java.util.List;

final class GWPatchEffectBlockCMP
  extends GWPatchEffectBlock
  implements GWPatchEffectBlockCMPType
{
  private final StructPatchComp cmp;

  GWPatchEffectBlockCMP(
    final StructPatchComp s)
  {
    super(List.of(
      s.f_sw,
      s.f_type,
      s.f_threshold,
      s.f_sustain,
      s.f_attack,
      s.f_level,
      s.f_tone,
      s.f_ratio,
      s.f_direct_mix
    ));

    this.cmp = s;
  }

  @Override
  public GWIOVariableType<GWOnOffValue> enabled()
  {
    return this.cmp.f_sw;
  }

  @Override
  public void readFromDevice()
    throws InterruptedException, GWDeviceException
  {
    this.cmp.readFromDevice();
  }

  @Override
  public GWIOVariableType<Integer> effectLevel()
  {
    return this.cmp.f_level;
  }

  @Override
  public GWIOVariableType<Integer> directMix()
  {
    return this.cmp.f_direct_mix;
  }

  @Override
  public GWIOVariableType<GWPatchCompTypeValue> type()
  {
    return this.cmp.f_type;
  }

  @Override
  public GWIOVariableType<Integer> sustain()
  {
    return this.cmp.f_sustain;
  }

  @Override
  public GWIOVariableType<Integer> attack()
  {
    return this.cmp.f_attack;
  }

  @Override
  public GWIOVariableType<Integer> level()
  {
    return this.cmp.f_level;
  }

  @Override
  public GWIOVariableType<Integer> tone()
  {
    return this.cmp.f_tone;
  }

  @Override
  public GWIOVariableType<GWPatchCompRatioValue> ratio()
  {
    return this.cmp.f_ratio;
  }

  @Override
  public GWIOVariableType<Integer> threshold()
  {
    return this.cmp.f_threshold;
  }
}
