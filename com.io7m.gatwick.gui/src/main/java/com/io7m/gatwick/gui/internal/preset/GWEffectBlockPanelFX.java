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


package com.io7m.gatwick.gui.internal.preset;

import com.io7m.gatwick.controller.api.GWControllerType;
import com.io7m.gatwick.controller.api.GWOnOffValue;
import com.io7m.gatwick.controller.api.GWPatchEffectBlockFXType;
import com.io7m.gatwick.controller.api.GWPatchFXTypeValue;
import com.io7m.gatwick.gui.internal.GWStrings;
import com.io7m.gatwick.gui.internal.icons.GWIconServiceType;
import com.io7m.gatwick.iovar.GWIOVariableContainerType;
import com.io7m.gatwick.iovar.GWIOVariableType;
import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import static com.io7m.gatwick.gui.internal.gt.GWGTK1LongRunning.TASK_LONG;

/**
 * A panel for the FX block.
 */

public abstract class GWEffectBlockPanelFX
  extends GWEffectBlockPanel<GWPatchFXTypeValue>
{
  private final GWStrings strings;
  private final GWIconServiceType icons;

  /**
   * A panel for the FX block.
   *
   * @param services The service directory
   */

  public GWEffectBlockPanelFX(
    final RPServiceDirectoryType services)
  {
    super(services);

    this.strings =
      services.requireService(GWStrings.class);
    this.icons =
      services.requireService(GWIconServiceType.class);
  }

  protected abstract GWPatchEffectBlockFXType fx(
    GWControllerType device);

  @Override
  protected final Optional<GWIOVariableType<GWPatchFXTypeValue>> selectableType(
    final GWControllerType device)
  {
    return Optional.of(this.fx(device).type());
  }

  private static List<GWIOVariableType<?>> withEnabled(
    final GWIOVariableType<GWOnOffValue> enabled,
    final GWIOVariableContainerType container)
  {
    return Stream.concat(
      Stream.of(enabled),
      container.variables().stream()
    ).toList();
  }

  @Override
  protected final List<GWIOVariableType<?>> variablesForDials(
    final GWControllerType device,
    final GWPatchFXTypeValue type)
  {
    final var p =
      this.fx(device);

    return switch (type) {
      case AC_GUITAR_SIM -> withEnabled(p.enabled(), p.agSim());
      case AC_RESONANCE -> withEnabled(p.enabled(), p.acReso());
      case AUTO_WAH -> withEnabled(p.enabled(), p.autoWah());
      case CHORUS -> withEnabled(p.enabled(), p.chorus());
      case CLASSIC_NEGATIVE_VIBE -> withEnabled(p.enabled(), p.classicVibe());
      case COMPRESSOR -> withEnabled(p.enabled(), p.compressor());
      case DEFRETTER -> withEnabled(p.enabled(), p.defretter());
      case FEEDBACKER -> withEnabled(p.enabled(), p.feedbacker());
      case FLANGER -> withEnabled(p.enabled(), p.flanger());
      case HARMONIST -> withEnabled(p.enabled(), p.harmonizer());
      case HUMANIZER -> withEnabled(p.enabled(), p.humanizer());
      case OCTAVE -> withEnabled(p.enabled(), p.octave());
      case OVERTONE -> withEnabled(p.enabled(), p.overtone());
      case PAN -> withEnabled(p.enabled(), p.pan());
      case PHASER -> withEnabled(p.enabled(), p.phaser());
      case PITCH_SHIFTER -> withEnabled(p.enabled(), p.pitchShifter());
      case RING_MOD -> withEnabled(p.enabled(), p.ringModulator());
      case ROTARY -> withEnabled(p.enabled(), p.rotary());
      case SITAR_SIM -> withEnabled(p.enabled(), p.sitar());
      case SLICER -> withEnabled(p.enabled(), p.slicer());
      case SLOW_GEAR -> withEnabled(p.enabled(), p.slowGear());
      case SOUND_HOLD -> withEnabled(p.enabled(), p.soundHold());
      case S_NEGATIVE_BEND -> withEnabled(p.enabled(), p.sBend());
      case TOUCH_WAH -> withEnabled(p.enabled(), p.touchWah());
      case TREMOLO -> withEnabled(p.enabled(), p.tremolo());
      case VIBRATO -> withEnabled(p.enabled(), p.vibrato());
      case CHORUS_BASS -> withEnabled(p.enabled(), p.chorusBass());
      case DEFRETTER_BASS -> withEnabled(p.enabled(), p.defretterBass());
      case FLANGER_BASS -> withEnabled(p.enabled(), p.flangerBass());
      case OCTAVE_BASS -> withEnabled(p.enabled(), p.octaveBass());
      case SLOW_GEAR_BASS -> withEnabled(p.enabled(), p.slowGearBass());
      case TOUCH_WAH_BASS -> withEnabled(p.enabled(), p.touchWahBass());
      case DISTORTION -> withEnabled(p.enabled(), p.distortion());
    };
  }

  @Override
  protected final void readFromDevice()
  {
    final var service = this.gtService();
    service.executeOnDevice(TASK_LONG, ctrl -> {
      final var p = this.fx(ctrl);
      final var type = p.type();
      type.readFromDevice();
      switch (type.get()) {
        case AC_GUITAR_SIM -> p.agSim().readFromDevice();
        case AC_RESONANCE -> p.acReso().readFromDevice();
        case AUTO_WAH -> p.autoWah().readFromDevice();
        case CHORUS -> p.chorus().readFromDevice();
        case CLASSIC_NEGATIVE_VIBE -> p.classicVibe().readFromDevice();
        case COMPRESSOR -> p.compressor().readFromDevice();
        case DEFRETTER -> p.defretter().readFromDevice();
        case FEEDBACKER -> p.feedbacker().readFromDevice();
        case FLANGER -> p.flanger().readFromDevice();
        case HARMONIST -> p.harmonizer().readFromDevice();
        case HUMANIZER -> p.humanizer().readFromDevice();
        case OCTAVE -> p.octave().readFromDevice();
        case OVERTONE -> p.overtone().readFromDevice();
        case PAN -> p.pan().readFromDevice();
        case PHASER -> p.phaser().readFromDevice();
        case PITCH_SHIFTER -> p.pitchShifter().readFromDevice();
        case RING_MOD -> p.ringModulator().readFromDevice();
        case ROTARY -> p.rotary().readFromDevice();
        case SITAR_SIM -> p.sitar().readFromDevice();
        case SLICER -> p.slicer().readFromDevice();
        case SLOW_GEAR -> p.slowGear().readFromDevice();
        case SOUND_HOLD -> p.soundHold().readFromDevice();
        case S_NEGATIVE_BEND -> p.sBend().readFromDevice();
        case TOUCH_WAH -> p.touchWah().readFromDevice();
        case TREMOLO -> p.tremolo().readFromDevice();
        case VIBRATO -> p.vibrato().readFromDevice();
        case CHORUS_BASS -> p.chorusBass().readFromDevice();
        case DEFRETTER_BASS -> p.defretter().readFromDevice();
        case FLANGER_BASS -> p.flangerBass().readFromDevice();
        case OCTAVE_BASS -> p.octaveBass().readFromDevice();
        case SLOW_GEAR_BASS -> p.slowGearBass().readFromDevice();
        case TOUCH_WAH_BASS -> p.touchWahBass().readFromDevice();
        case DISTORTION -> p.distortion().readFromDevice();
      }
    });
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {

  }
}
