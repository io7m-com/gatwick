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
import com.io7m.gatwick.controller.api.GWPatchEffectAGSimType;
import com.io7m.gatwick.controller.api.GWPatchEffectAcResoType;
import com.io7m.gatwick.controller.api.GWPatchEffectAutoWahType;
import com.io7m.gatwick.controller.api.GWPatchEffectBlockFXType;
import com.io7m.gatwick.controller.api.GWPatchEffectChorusBassType;
import com.io7m.gatwick.controller.api.GWPatchEffectChorusType;
import com.io7m.gatwick.controller.api.GWPatchEffectClassicVibeType;
import com.io7m.gatwick.controller.api.GWPatchEffectCompressorType;
import com.io7m.gatwick.controller.api.GWPatchEffectDefretterBassType;
import com.io7m.gatwick.controller.api.GWPatchEffectDefretterType;
import com.io7m.gatwick.controller.api.GWPatchEffectDistortionType;
import com.io7m.gatwick.controller.api.GWPatchEffectFeedbackerType;
import com.io7m.gatwick.controller.api.GWPatchEffectFlangerBassType;
import com.io7m.gatwick.controller.api.GWPatchEffectFlangerType;
import com.io7m.gatwick.controller.api.GWPatchEffectHarmonizerType;
import com.io7m.gatwick.controller.api.GWPatchEffectHumanizerType;
import com.io7m.gatwick.controller.api.GWPatchEffectOctaveBassType;
import com.io7m.gatwick.controller.api.GWPatchEffectOctaveType;
import com.io7m.gatwick.controller.api.GWPatchEffectOvertoneType;
import com.io7m.gatwick.controller.api.GWPatchEffectPanType;
import com.io7m.gatwick.controller.api.GWPatchEffectPhaserType;
import com.io7m.gatwick.controller.api.GWPatchEffectPitchShifterType;
import com.io7m.gatwick.controller.api.GWPatchEffectRingModulatorType;
import com.io7m.gatwick.controller.api.GWPatchEffectRotaryType;
import com.io7m.gatwick.controller.api.GWPatchEffectSBendType;
import com.io7m.gatwick.controller.api.GWPatchEffectSitarSimulationType;
import com.io7m.gatwick.controller.api.GWPatchEffectSlicerType;
import com.io7m.gatwick.controller.api.GWPatchEffectSlowGearBassType;
import com.io7m.gatwick.controller.api.GWPatchEffectSlowGearType;
import com.io7m.gatwick.controller.api.GWPatchEffectSoundHoldType;
import com.io7m.gatwick.controller.api.GWPatchEffectTouchWahBassType;
import com.io7m.gatwick.controller.api.GWPatchEffectTouchWahType;
import com.io7m.gatwick.controller.api.GWPatchEffectTremoloType;
import com.io7m.gatwick.controller.api.GWPatchEffectVibratoType;
import com.io7m.gatwick.controller.api.GWPatchFXTypeValue;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFX;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXAGSim;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXAWah;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXAcReso;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXCVibe;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXChorus;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXChorusBass;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXComp;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXDefretter;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXDefretterBass;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXDist;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXFeedbacker;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXFlanger;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXFlangerBass;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXHarmonizer;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXHumanizer;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXOctave;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXOctaveBass;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXOvertone;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXPan;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXPhaser;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXPitchShift;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXRingMod;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXRotary;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXSBend;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXSitarSim;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXSlicer;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXSlowGear;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXSlowGearBass;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXSoundHold;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXTWah;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXTWahBass;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXTremolo;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchFXVibrato;
import com.io7m.gatwick.device.api.GWDeviceException;
import com.io7m.gatwick.iovar.GWIOVariableContainerType;
import com.io7m.gatwick.iovar.GWIOVariableType;

import java.util.List;
import java.util.Objects;

final class GWPatchEffectBlockFX
  extends GWPatchEffectBlock
  implements GWPatchEffectBlockFXType
{
  private final StructPatchFX fx;
  private final StructPatchFXAGSim agSim;
  private final StructPatchFXAcReso acReso;
  private final StructPatchFXAWah awah;
  private final StructPatchFXChorus chorus;
  private final StructPatchFXCVibe cvibe;
  private final StructPatchFXComp comp;
  private final StructPatchFXDefretter defretter;
  private final StructPatchFXFeedbacker feedbacker;
  private final StructPatchFXFlanger flanger;
  private final StructPatchFXHarmonizer harmonizer;
  private final StructPatchFXHumanizer humanizer;
  private final StructPatchFXOctave octave;
  private final StructPatchFXOvertone overtone;
  private final StructPatchFXPan pan;
  private final StructPatchFXPhaser phaser;
  private final StructPatchFXPitchShift pitchshift;
  private final StructPatchFXRingMod ringmod;
  private final StructPatchFXRotary rotary;
  private final StructPatchFXSitarSim sitarsim;
  private final StructPatchFXSlicer slicer;
  private final StructPatchFXSlowGear slowgear;
  private final StructPatchFXSoundHold soundhold;
  private final StructPatchFXSBend sbend;
  private final StructPatchFXTWah twah;
  private final StructPatchFXTremolo tremolo;
  private final StructPatchFXVibrato vibrato;
  private final StructPatchFXChorusBass chorusbass;
  private final StructPatchFXFlangerBass flangerbass;
  private final StructPatchFXDist distortion;
  private final StructPatchFXSlowGearBass slowGearBass;
  private final StructPatchFXOctaveBass octaveBass;
  private final StructPatchFXDefretterBass defretterBass;
  private final StructPatchFXTWahBass twahBass;

  GWPatchEffectBlockFX(
    final StructPatchFX inFx,
    final StructPatchFXAGSim inAgSim,
    final StructPatchFXAcReso inAcReso,
    final StructPatchFXAWah inAwah,
    final StructPatchFXChorus inChorus,
    final StructPatchFXCVibe inCvibe,
    final StructPatchFXComp inComp,
    final StructPatchFXDefretter inDefretter,
    final StructPatchFXFeedbacker inFeedbacker,
    final StructPatchFXFlanger inFlanger,
    final StructPatchFXHarmonizer inHarmonizer,
    final StructPatchFXHumanizer inHumanizer,
    final StructPatchFXOctave inOctave,
    final StructPatchFXOvertone inOvertone,
    final StructPatchFXPan inPan,
    final StructPatchFXPhaser inPhaser,
    final StructPatchFXPitchShift inPitchshift,
    final StructPatchFXRingMod inRingmod,
    final StructPatchFXRotary inRotary,
    final StructPatchFXSitarSim inSitarsim,
    final StructPatchFXSlicer inSlicer,
    final StructPatchFXSlowGear inSlowgear,
    final StructPatchFXSoundHold inSoundhold,
    final StructPatchFXSBend inSbend,
    final StructPatchFXTWah inTwah,
    final StructPatchFXTremolo inTremolo,
    final StructPatchFXVibrato inVibrato,
    final StructPatchFXChorusBass inChorusbass,
    final StructPatchFXFlangerBass inFlangerbass,
    final StructPatchFXDist inDistortion,
    final StructPatchFXSlowGearBass inSlowGearBass,
    final StructPatchFXOctaveBass inOctaveBass,
    final StructPatchFXDefretterBass inDefretterBass,
    final StructPatchFXTWahBass inTwahBass)
  {
    super(
      parameters(
        List.of(
          inAcReso,
          inAgSim,
          inAwah,
          inChorus,
          inChorusbass,
          inComp,
          inCvibe,
          inDefretter,
          inDistortion,
          inFeedbacker,
          inFlanger,
          inFlangerbass,
          inFx,
          inHarmonizer,
          inHumanizer,
          inOctave,
          inOvertone,
          inPan,
          inPhaser,
          inPitchshift,
          inRingmod,
          inRotary,
          inSbend,
          inSitarsim,
          inSlicer,
          inSlowgear,
          inSoundhold,
          inTremolo,
          inTwah,
          inVibrato
        )
      )
    );

    this.fx = Objects.requireNonNull(inFx, "s");

    this.acReso = inAcReso;
    this.agSim = inAgSim;
    this.awah = inAwah;
    this.chorus = inChorus;
    this.chorusbass = inChorusbass;
    this.comp = inComp;
    this.cvibe = inCvibe;
    this.defretter = inDefretter;
    this.distortion = inDistortion;
    this.feedbacker = inFeedbacker;
    this.flanger = inFlanger;
    this.flangerbass = inFlangerbass;
    this.harmonizer = inHarmonizer;
    this.humanizer = inHumanizer;
    this.octave = inOctave;
    this.overtone = inOvertone;
    this.pan = inPan;
    this.phaser = inPhaser;
    this.pitchshift = inPitchshift;
    this.ringmod = inRingmod;
    this.rotary = inRotary;
    this.sbend = inSbend;
    this.sitarsim = inSitarsim;
    this.slicer = inSlicer;
    this.slowgear = inSlowgear;
    this.soundhold = inSoundhold;
    this.tremolo = inTremolo;
    this.twah = inTwah;
    this.vibrato = inVibrato;
    this.slowGearBass = inSlowGearBass;
    this.octaveBass = inOctaveBass;
    this.defretterBass = inDefretterBass;
    this.twahBass = inTwahBass;
  }

  private static List<GWIOVariableType<?>> parameters(
    final List<GWIOVariableContainerType> vars)
  {
    return vars.stream()
      .flatMap(v -> v.variables().stream())
      .toList();
  }

  @Override
  public GWIOVariableType<GWOnOffValue> enabled()
  {
    return this.fx.f_fx_sw;
  }

  @Override
  public GWIOVariableType<GWPatchFXTypeValue> type()
  {
    return this.fx.f_fx_type;
  }

  @Override
  public GWPatchEffectAGSimType agSim()
  {
    return this.agSim;
  }

  @Override
  public GWPatchEffectAcResoType acReso()
  {
    return this.acReso;
  }

  @Override
  public GWPatchEffectAutoWahType autoWah()
  {
    return this.awah;
  }

  @Override
  public GWPatchEffectCompressorType compressor()
  {
    return this.comp;
  }

  @Override
  public GWPatchEffectDefretterBassType defretterBass()
  {
    return this.defretterBass;
  }

  @Override
  public GWPatchEffectChorusType chorus()
  {
    return this.chorus;
  }

  @Override
  public GWPatchEffectChorusBassType chorusBass()
  {
    return this.chorusbass;
  }

  @Override
  public GWPatchEffectClassicVibeType classicVibe()
  {
    return this.cvibe;
  }

  @Override
  public GWPatchEffectDistortionType distortion()
  {
    return this.distortion;
  }

  @Override
  public GWPatchEffectDefretterType defretter()
  {
    return this.defretter;
  }

  @Override
  public GWPatchEffectFeedbackerType feedbacker()
  {
    return this.feedbacker;
  }

  @Override
  public GWPatchEffectFlangerType flanger()
  {
    return this.flanger;
  }

  @Override
  public GWPatchEffectHarmonizerType harmonizer()
  {
    return this.harmonizer;
  }

  @Override
  public GWPatchEffectHumanizerType humanizer()
  {
    return this.humanizer;
  }

  @Override
  public GWPatchEffectOctaveBassType octaveBass()
  {
    return this.octaveBass;
  }

  @Override
  public GWPatchEffectOctaveType octave()
  {
    return this.octave;
  }

  @Override
  public GWPatchEffectOvertoneType overtone()
  {
    return this.overtone;
  }

  @Override
  public GWPatchEffectPanType pan()
  {
    return this.pan;
  }

  @Override
  public GWPatchEffectPhaserType phaser()
  {
    return this.phaser;
  }

  @Override
  public GWPatchEffectPitchShifterType pitchShifter()
  {
    return this.pitchshift;
  }

  @Override
  public GWPatchEffectRingModulatorType ringModulator()
  {
    return this.ringmod;
  }

  @Override
  public GWPatchEffectRotaryType rotary()
  {
    return this.rotary;
  }

  @Override
  public GWPatchEffectSBendType sBend()
  {
    return this.sbend;
  }

  @Override
  public GWPatchEffectSitarSimulationType sitar()
  {
    return this.sitarsim;
  }

  @Override
  public GWPatchEffectSlicerType slicer()
  {
    return this.slicer;
  }

  @Override
  public GWPatchEffectSlowGearBassType slowGearBass()
  {
    return this.slowGearBass;
  }

  @Override
  public GWPatchEffectSlowGearType slowGear()
  {
    return this.slowgear;
  }

  @Override
  public GWPatchEffectSoundHoldType soundHold()
  {
    return this.soundhold;
  }

  @Override
  public GWPatchEffectTouchWahBassType touchWahBass()
  {
    return this.twahBass;
  }

  @Override
  public GWPatchEffectTouchWahType touchWah()
  {
    return this.twah;
  }

  @Override
  public GWPatchEffectTremoloType tremolo()
  {
    return this.tremolo;
  }

  @Override
  public GWPatchEffectVibratoType vibrato()
  {
    return this.vibrato;
  }

  @Override
  public GWPatchEffectFlangerBassType flangerBass()
  {
    return this.flangerbass;
  }

  @Override
  public void readFromDevice()
    throws InterruptedException, GWDeviceException
  {
    for (final var v : this.variables()) {
      v.readFromDevice();
    }
  }
}
