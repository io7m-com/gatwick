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


package com.io7m.gatwick.controller.api;

import com.io7m.gatwick.iovar.GWIOVariableType;

/**
 * The patch FX effect block.
 */

public interface GWPatchEffectBlockFXType
  extends GWPatchEffectBlockType
{
  /**
   * @return The on/off value
   */

  GWIOVariableType<GWOnOffValue> enabled();

  /**
   * @return The FX type
   */

  GWIOVariableType<GWPatchFXTypeValue> type();

  GWPatchEffectAGSimType agSim();

  GWPatchEffectAcResoType acReso();

  GWPatchEffectAutoWahType autoWah();

  GWPatchEffectChorusBassType chorusBass();

  GWPatchEffectChorusType chorus();

  GWPatchEffectClassicVibeType classicVibe();

  GWPatchEffectCompressorType compressor();

  GWPatchEffectDefretterBassType defretterBass();

  GWPatchEffectDefretterType defretter();

  GWPatchEffectDistortionType distortion();

  GWPatchEffectFeedbackerType feedbacker();

  GWPatchEffectFlangerBassType flangerBass();

  GWPatchEffectFlangerType flanger();

  GWPatchEffectHarmonizerType harmonizer();

  GWPatchEffectHumanizerType humanizer();

  GWPatchEffectOctaveBassType octaveBass();

  GWPatchEffectOctaveType octave();

  GWPatchEffectOvertoneType overtone();

  GWPatchEffectPanType pan();

  GWPatchEffectPhaserType phaser();

  GWPatchEffectPitchShifterType pitchShifter();

  GWPatchEffectRingModulatorType ringModulator();

  GWPatchEffectRotaryType rotary();

  GWPatchEffectSBendType sBend();

  GWPatchEffectSitarSimulationType sitar();

  GWPatchEffectSlicerType slicer();

  GWPatchEffectSlowGearBassType slowGearBass();

  GWPatchEffectSlowGearType slowGear();

  GWPatchEffectSoundHoldType soundHold();

  GWPatchEffectTouchWahBassType touchWahBass();

  GWPatchEffectTouchWahType touchWah();

  GWPatchEffectTremoloType tremolo();

  GWPatchEffectVibratoType vibrato();
}
