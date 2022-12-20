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

import com.io7m.gatwick.controller.api.GWPatchEfctDividerChannelSelectValue;
import com.io7m.gatwick.controller.api.GWPatchEfctDividerCutoffFreqValue;
import com.io7m.gatwick.controller.api.GWPatchEfctDividerDynamicValue;
import com.io7m.gatwick.controller.api.GWPatchEfctDividerFilterValue;
import com.io7m.gatwick.controller.api.GWPatchEfctDividerModeValue;
import com.io7m.gatwick.controller.api.GWPatchEffectBlockDividerType;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchEfct;
import com.io7m.gatwick.device.api.GWDeviceException;
import com.io7m.gatwick.iovar.GWIOVariableType;

final class GWPatchEffectBlockDivider
  extends GWPatchEffectBlock
  implements GWPatchEffectBlockDividerType
{
  private final StructPatchEfct efct;
  private final GWIOVariableType<GWPatchEfctDividerModeValue> div_mode;
  private final GWIOVariableType<GWPatchEfctDividerChannelSelectValue> div_ch_select;
  private final GWIOVariableType<GWPatchEfctDividerDynamicValue> div_a_dynamic;
  private final GWIOVariableType<Integer> div_a_dyna_sens;
  private final GWIOVariableType<GWPatchEfctDividerFilterValue> div_a_filter;
  private final GWIOVariableType<GWPatchEfctDividerCutoffFreqValue> div_a_cutoff;
  private final GWIOVariableType<GWPatchEfctDividerDynamicValue> div_b_dynamic;
  private final GWIOVariableType<Integer> div_b_dyna_sens;
  private final GWIOVariableType<GWPatchEfctDividerFilterValue> div_b_filter;
  private final GWIOVariableType<GWPatchEfctDividerCutoffFreqValue> div_b_cutoff;

  GWPatchEffectBlockDivider(
    final StructPatchEfct f_efct,
    final int index)
  {
    this.efct = f_efct;

    switch (index) {
      case 1 -> {
        this.div_mode = this.efct.f_div1_mode;
        this.div_ch_select = this.efct.f_div1_ch_select;
        this.div_a_dynamic = this.efct.f_div1_a_dynamic;
        this.div_a_dyna_sens = this.efct.f_div1_a_dyna_sens;
        this.div_a_filter = this.efct.f_div1_a_filter;
        this.div_a_cutoff = this.efct.f_div1_a_cutoff_freq;
        this.div_b_dynamic = this.efct.f_div1_b_dynamic;
        this.div_b_dyna_sens = this.efct.f_div1_b_dyna_sens;
        this.div_b_filter = this.efct.f_div1_b_filter;
        this.div_b_cutoff = this.efct.f_div1_b_cutoff_freq;
      }
      case 2 -> {
        this.div_mode = this.efct.f_div2_mode;
        this.div_ch_select = this.efct.f_div2_ch_select;
        this.div_a_dynamic = this.efct.f_div2_a_dynamic;
        this.div_a_dyna_sens = this.efct.f_div2_a_dyna_sens;
        this.div_a_filter = this.efct.f_div2_a_filter;
        this.div_a_cutoff = this.efct.f_div2_a_cutoff_freq;
        this.div_b_dynamic = this.efct.f_div2_b_dynamic;
        this.div_b_dyna_sens = this.efct.f_div2_b_dyna_sens;
        this.div_b_filter = this.efct.f_div2_b_filter;
        this.div_b_cutoff = this.efct.f_div2_b_cutoff_freq;
      }
      case 3 -> {
        this.div_mode = this.efct.f_div3_mode;
        this.div_ch_select = this.efct.f_div3_ch_select;
        this.div_a_dynamic = this.efct.f_div3_a_dynamic;
        this.div_a_dyna_sens = this.efct.f_div3_a_dyna_sens;
        this.div_a_filter = this.efct.f_div3_a_filter;
        this.div_a_cutoff = this.efct.f_div3_a_cutoff_freq;
        this.div_b_dynamic = this.efct.f_div3_b_dynamic;
        this.div_b_dyna_sens = this.efct.f_div3_b_dyna_sens;
        this.div_b_filter = this.efct.f_div3_b_filter;
        this.div_b_cutoff = this.efct.f_div3_b_cutoff_freq;
      }
      default -> {
        throw new IllegalArgumentException("Unrecognized index: " + index);
      }
    }
  }

  @Override
  public void readFromDevice()
    throws InterruptedException, GWDeviceException
  {
    this.efct.readFromDevice();
  }

  @Override
  public GWIOVariableType<GWPatchEfctDividerModeValue> mode()
  {
    return this.div_mode;
  }

  @Override
  public GWIOVariableType<GWPatchEfctDividerChannelSelectValue> channel()
  {
    return this.div_ch_select;
  }

  @Override
  public GWIOVariableType<GWPatchEfctDividerDynamicValue> dynamicA()
  {
    return this.div_a_dynamic;
  }

  @Override
  public GWIOVariableType<Integer> dynamicSensitivityA()
  {
    return this.div_a_dyna_sens;
  }

  @Override
  public GWIOVariableType<GWPatchEfctDividerFilterValue> filterA()
  {
    return this.div_a_filter;
  }

  @Override
  public GWIOVariableType<GWPatchEfctDividerCutoffFreqValue> cutoffA()
  {
    return this.div_a_cutoff;
  }

  @Override
  public GWIOVariableType<GWPatchEfctDividerDynamicValue> dynamicB()
  {
    return this.div_b_dynamic;
  }

  @Override
  public GWIOVariableType<Integer> dynamicSensitivityB()
  {
    return this.div_b_dyna_sens;
  }

  @Override
  public GWIOVariableType<GWPatchEfctDividerFilterValue> filterB()
  {
    return this.div_b_filter;
  }

  @Override
  public GWIOVariableType<GWPatchEfctDividerCutoffFreqValue> cutoffB()
  {
    return this.div_b_cutoff;
  }
}
