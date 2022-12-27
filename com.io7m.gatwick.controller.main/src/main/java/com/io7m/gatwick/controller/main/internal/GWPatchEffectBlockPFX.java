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
import com.io7m.gatwick.controller.api.GWPatchEffectBlockPFXType;
import com.io7m.gatwick.controller.api.GWPatchPedalFXTypeValue;
import com.io7m.gatwick.controller.api.GWPatchPedalFXWahTypeValue;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchPedalFX;
import com.io7m.gatwick.device.api.GWDeviceException;
import com.io7m.gatwick.iovar.GWIOVariableType;

import java.util.List;

final class GWPatchEffectBlockPFX
  extends GWPatchEffectBlock
  implements GWPatchEffectBlockPFXType
{
  private final StructPatchPedalFX pfx;

  GWPatchEffectBlockPFX(
    final StructPatchPedalFX s)
  {
    super(List.of(
      s.f_sw,
      s.f_effect_level,
      s.f_direct_mix,
      s.f_type,
      s.f_wah_type,
      s.f_wah_pedal_position,
      s.f_pedal_min,
      s.f_pedal_max,
      s.f_pedal_bend_pedal_position,
      s.f_pitch_min,
      s.f_pitch_max
    ));

    this.pfx = s;
  }

  @Override
  public GWIOVariableType<GWOnOffValue> enabled()
  {
    return this.pfx.f_sw;
  }

  @Override
  public GWIOVariableType<Integer> effectLevel()
  {
    return this.pfx.f_effect_level;
  }

  @Override
  public GWIOVariableType<Integer> directMix()
  {
    return this.pfx.f_direct_mix;
  }

  @Override
  public void readFromDevice()
    throws InterruptedException, GWDeviceException
  {
    this.pfx.readFromDevice();
  }

  @Override
  public GWIOVariableType<GWPatchPedalFXTypeValue> type()
  {
    return this.pfx.f_type;
  }

  @Override
  public GWIOVariableType<GWPatchPedalFXWahTypeValue> wahType()
  {
    return this.pfx.f_wah_type;
  }

  @Override
  public GWIOVariableType<Integer> wahPosition()
  {
    return this.pfx.f_wah_pedal_position;
  }

  @Override
  public GWIOVariableType<Integer> pedalMinimum()
  {
    return this.pfx.f_pedal_min;
  }

  @Override
  public GWIOVariableType<Integer> pedalMaximum()
  {
    return this.pfx.f_pedal_max;
  }

  @Override
  public GWIOVariableType<Integer> bendPosition()
  {
    return this.pfx.f_pedal_bend_pedal_position;
  }

  @Override
  public GWIOVariableType<Integer> bendPitchMinimum()
  {
    return this.pfx.f_pitch_min;
  }

  @Override
  public GWIOVariableType<Integer> bendPitchMaximum()
  {
    return this.pfx.f_pitch_max;
  }
}
