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


package com.io7m.gatwick.gui.internal.icons;

import com.io7m.gatwick.controller.api.GWPatchPreampTypeValue;

import static com.io7m.gatwick.controller.api.GWPatchPreampTypeValue.BGNR_UB_METAL;
import static com.io7m.gatwick.controller.api.GWPatchPreampTypeValue.BG_COMBO;
import static com.io7m.gatwick.controller.api.GWPatchPreampTypeValue.BOUTIQUE;
import static com.io7m.gatwick.controller.api.GWPatchPreampTypeValue.BRIT_STACK;
import static com.io7m.gatwick.controller.api.GWPatchPreampTypeValue.DELUXE_COMBO;
import static com.io7m.gatwick.controller.api.GWPatchPreampTypeValue.DIAMOND_AMP;
import static com.io7m.gatwick.controller.api.GWPatchPreampTypeValue.JC_NEGATIVE_120;
import static com.io7m.gatwick.controller.api.GWPatchPreampTypeValue.JUGGERNAUT;
import static com.io7m.gatwick.controller.api.GWPatchPreampTypeValue.MATCH_COMBO;
import static com.io7m.gatwick.controller.api.GWPatchPreampTypeValue.MAXIMUM;
import static com.io7m.gatwick.controller.api.GWPatchPreampTypeValue.NATURAL;
import static com.io7m.gatwick.controller.api.GWPatchPreampTypeValue.ORNG_STACK;
import static com.io7m.gatwick.controller.api.GWPatchPreampTypeValue.RECTI_STACK;
import static com.io7m.gatwick.controller.api.GWPatchPreampTypeValue.SUPREME;
import static com.io7m.gatwick.controller.api.GWPatchPreampTypeValue.TRANSPARENT;
import static com.io7m.gatwick.controller.api.GWPatchPreampTypeValue.TWEED_COMBO;
import static com.io7m.gatwick.controller.api.GWPatchPreampTypeValue.TWIN_COMBO;
import static com.io7m.gatwick.controller.api.GWPatchPreampTypeValue.X_NEGATIVE_CRUNCH;
import static com.io7m.gatwick.controller.api.GWPatchPreampTypeValue.X_NEGATIVE_HI_GAIN;
import static com.io7m.gatwick.controller.api.GWPatchPreampTypeValue.X_NEGATIVE_MODDED;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;

/**
 * Icons for preamps.
 */

public final class GWIconEnumerationSetPreamp
  extends GWIconEnumerationSetAbstract<GWPatchPreampTypeValue>
{
  /**
   * Icons for preamps.
   */

  public GWIconEnumerationSetPreamp()
  {
    super(
      GWPatchPreampTypeValue.class,
      ofEntries(
        entry(BG_COMBO, "amp_bgcombo.png"),
        entry(BGNR_UB_METAL, "amp_bgnr.png"),
        entry(BOUTIQUE, "amp_boutique.png"),
        entry(BRIT_STACK, "amp_britstk.png"),
        entry(DELUXE_COMBO, "amp_deluxe.png"),
        entry(DIAMOND_AMP, "amp_diamond.png"),
        entry(JC_NEGATIVE_120, "amp_jc120.png"),
        entry(JUGGERNAUT, "amp_juggernaut.png"),
        entry(MATCH_COMBO, "amp_matchless.png"),
        entry(MAXIMUM, "amp_maximum.png"),
        entry(NATURAL, "amp_natural.png"),
        entry(ORNG_STACK, "amp_orange.png"),
        entry(RECTI_STACK, "amp_rectistk.png"),
        entry(SUPREME, "amp_supreme.png"),
        entry(TRANSPARENT, "amp_transparent.png"),
        entry(TWEED_COMBO, "amp_tweed.png"),
        entry(TWIN_COMBO, "amp_twin.png"),
        entry(X_NEGATIVE_CRUNCH, "amp_xcrunch.png"),
        entry(X_NEGATIVE_HI_GAIN, "amp_xhighgain.png"),
        entry(X_NEGATIVE_MODDED, "amp_xmodded.png")
      ),
      "amp_natural.png"
    );
  }
}
