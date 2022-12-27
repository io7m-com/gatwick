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
import com.io7m.gatwick.controller.api.GWPatchEffectBlockPreampType;
import com.io7m.gatwick.controller.api.GWPatchPreampGainValue;
import com.io7m.gatwick.controller.api.GWPatchPreampTypeValue;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchPreamp;
import com.io7m.gatwick.device.api.GWDeviceException;
import com.io7m.gatwick.iovar.GWIOVariableType;

import java.util.List;

final class GWPatchEffectBlockPreamp
  extends GWPatchEffectBlock
  implements GWPatchEffectBlockPreampType
{
  private final StructPatchPreamp preamp;

  GWPatchEffectBlockPreamp(
    final StructPatchPreamp s)
  {
    super(List.of(
      s.f_sw,
      s.f_type,
      s.f_gain,
      s.f_sag,
      s.f_resonance,
      s.f_level,
      s.f_bass,
      s.f_middle,
      s.f_treble,
      s.f_presence,
      s.f_bright,
      s.f_gain_sw,
      s.f_solo_sw,
      s.f_solo_level
    ));

    this.preamp = s;
  }

  @Override
  public GWIOVariableType<GWOnOffValue> enabled()
  {
    return this.preamp.f_sw;
  }

  @Override
  public void readFromDevice()
    throws InterruptedException, GWDeviceException
  {
    this.preamp.readFromDevice();
  }

  @Override
  public GWIOVariableType<GWPatchPreampTypeValue> type()
  {
    return this.preamp.f_type;
  }

  @Override
  public GWIOVariableType<Integer> gain()
  {
    return this.preamp.f_gain;
  }

  @Override
  public GWIOVariableType<Integer> sag()
  {
    return this.preamp.f_sag;
  }

  @Override
  public GWIOVariableType<Integer> resonance()
  {
    return this.preamp.f_resonance;
  }

  @Override
  public GWIOVariableType<Integer> level()
  {
    return this.preamp.f_level;
  }

  @Override
  public GWIOVariableType<Integer> bass()
  {
    return this.preamp.f_bass;
  }

  @Override
  public GWIOVariableType<Integer> middle()
  {
    return this.preamp.f_middle;
  }

  @Override
  public GWIOVariableType<Integer> treble()
  {
    return this.preamp.f_treble;
  }

  @Override
  public GWIOVariableType<Integer> presence()
  {
    return this.preamp.f_presence;
  }

  @Override
  public GWIOVariableType<GWOnOffValue> bright()
  {
    return this.preamp.f_bright;
  }

  @Override
  public GWIOVariableType<GWPatchPreampGainValue> gainSwitch()
  {
    return this.preamp.f_gain_sw;
  }

  @Override
  public GWIOVariableType<GWOnOffValue> solo()
  {
    return this.preamp.f_solo_sw;
  }

  @Override
  public GWIOVariableType<Integer> soloLevel()
  {
    return this.preamp.f_solo_level;
  }
}
