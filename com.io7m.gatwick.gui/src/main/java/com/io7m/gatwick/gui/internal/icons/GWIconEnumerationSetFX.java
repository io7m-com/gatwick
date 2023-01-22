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

import com.io7m.gatwick.controller.api.GWPatchFXTypeValue;

import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.AC_GUITAR_SIM;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.AC_RESONANCE;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.AUTO_WAH;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.CHORUS;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.CHORUS_BASS;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.CLASSIC_NEGATIVE_VIBE;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.COMPRESSOR;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.DEFRETTER;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.DEFRETTER_BASS;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.DISTORTION;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.FEEDBACKER;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.FLANGER;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.FLANGER_BASS;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.HARMONIST;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.HUMANIZER;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.OCTAVE;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.OCTAVE_BASS;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.OVERTONE;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.PAN;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.PHASER;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.PITCH_SHIFTER;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.RING_MOD;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.ROTARY;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.SITAR_SIM;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.SLICER;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.SLOW_GEAR;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.SLOW_GEAR_BASS;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.SOUND_HOLD;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.TOUCH_WAH;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.TOUCH_WAH_BASS;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.TREMOLO;
import static com.io7m.gatwick.controller.api.GWPatchFXTypeValue.VIBRATO;
import static java.util.Map.entry;
import static java.util.Map.ofEntries;

/**
 * Icons for FX.
 */

public final class GWIconEnumerationSetFX
  extends GWIconEnumerationSetAbstract<GWPatchFXTypeValue>
{
  /**
   * Icons for FX.
   */

  public GWIconEnumerationSetFX()
  {
    super(
      GWPatchFXTypeValue.class,
      ofEntries(
        entry(AC_GUITAR_SIM, "fx_acsim.png"),
        entry(AC_RESONANCE, "fx_acres.png"),
        entry(AUTO_WAH, "fx_autowah.png"),
        entry(CHORUS, "fx_chorus.png"),
        entry(CHORUS_BASS, "fx_chorus.png"),
        entry(CLASSIC_NEGATIVE_VIBE, "fx_vibrato.png"),
        entry(COMPRESSOR, "fx_compressor.png"),
        entry(DEFRETTER, "fx_defretter.png"),
        entry(DEFRETTER_BASS, "fx_defretter.png"),
        entry(DISTORTION, "fx_distortion.png"),
        entry(FEEDBACKER, "fx_feedbacker.png"),
        entry(FLANGER, "fx_flanger.png"),
        entry(FLANGER_BASS, "fx_flanger.png"),
        entry(HARMONIST, "fx_harmonist.png"),
        entry(HUMANIZER, "fx_humanizer.png"),
        entry(OCTAVE, "fx_octave.png"),
        entry(OCTAVE_BASS, "fx_octave.png"),
        entry(OVERTONE, "fx_overtone.png"),
        entry(PAN, "fx_pan.png"),
        entry(PHASER, "fx_phaser.png"),
        entry(PITCH_SHIFTER, "fx_pitchshifter.png"),
        entry(RING_MOD, "fx_ringmod.png"),
        entry(ROTARY, "fx_rotary.png"),
        entry(SITAR_SIM, "fx_sitar.png"),
        entry(SLICER, "fx_slicer.png"),
        entry(SLOW_GEAR, "fx_slowgear.png"),
        entry(SLOW_GEAR_BASS, "fx_slowgear.png"),
        entry(SOUND_HOLD, "fx_soundhold.png"),
        entry(TOUCH_WAH, "fx_touchwah.png"),
        entry(TOUCH_WAH_BASS, "fx_touchwah.png"),
        entry(TREMOLO, "fx_tremolo.png"),
        entry(VIBRATO, "fx_vibrato.png")
      ),
      "fx_fallback.png"
    );
  }
}
