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
import com.io7m.gatwick.controller.api.GWPatchEffectBlockPreampType;
import com.io7m.gatwick.controller.api.GWPatchPreampTypeValue;
import com.io7m.gatwick.gui.internal.GWStrings;
import com.io7m.gatwick.gui.internal.icons.GWIconServiceType;
import com.io7m.gatwick.iovar.GWIOVariableType;
import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.io7m.gatwick.gui.internal.gt.GWGTK1LongRunning.TASK_LONG;

/**
 * A panel for the preamp block.
 */

public abstract class GWEffectBlockPanelPreamp
  extends GWEffectBlockPanel<GWPatchPreampTypeValue>
{
  private final GWStrings strings;
  private final GWIconServiceType icons;

  /**
   * A panel for the preamp block.
   *
   * @param services The service directory
   */

  public GWEffectBlockPanelPreamp(
    final RPServiceDirectoryType services)
  {
    super(services);

    this.strings =
      services.requireService(GWStrings.class);
    this.icons =
      services.requireService(GWIconServiceType.class);
  }

  protected abstract GWPatchEffectBlockPreampType preamp(
    GWControllerType device);

  @Override
  protected final Optional<GWIOVariableType<GWPatchPreampTypeValue>> selectableType(
    final GWControllerType device)
  {
    return Optional.of(this.preamp(device).type());
  }

  private static boolean hasBrightSwitch(
    final GWPatchPreampTypeValue type)
  {
    return switch (type) {
      case BGNR_UB_METAL,
        BG_COMBO,
        BRIT_STACK,
        CONCERT,
        DELUXE_COMBO,
        DIAMOND_AMP,
        JUGGERNAUT,
        MATCH_COMBO,
        MAXIMUM,
        ORNG_STACK,
        RECTI_STACK,
        SUPREME,
        TRANSPARENT,
        X_NEGATIVE_HI_GAIN,
        X_NEGATIVE_MODDED -> false;

      case BOUTIQUE,
        JC_NEGATIVE_120,
        NATURAL,
        NATURAL_BASS,
        TWEED_COMBO,
        TWIN_COMBO,
        X_NEGATIVE_CRUNCH,
        X_NEGATIVE_DRIVE_BASS -> true;
    };
  }

  @Override
  protected final List<GWIOVariableType<?>> variablesForDials(
    final GWControllerType device,
    final GWPatchPreampTypeValue type)
  {
    final var p =
      this.preamp(device);

    if (hasBrightSwitch(type)) {
      return List.of(
        p.enabled(),
        p.gain(),
        p.sag(),
        p.resonance(),
        p.level(),
        p.bass(),
        p.middle(),
        p.treble(),
        p.presence(),
        p.bright(),
        p.gainSwitch(),
        p.solo(),
        p.soloLevel()
      );
    }

    return List.of(
      p.enabled(),
      p.gain(),
      p.sag(),
      p.resonance(),
      p.level(),
      p.bass(),
      p.middle(),
      p.treble(),
      p.presence(),
      p.gainSwitch(),
      p.solo(),
      p.soloLevel()
    );
  }

  @Override
  protected final void readFromDevice()
  {
    final var service = this.gtService();
    service.executeOnDevice(TASK_LONG, ctrl -> {
      this.preamp(ctrl)
        .readFromDevice();
    });
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {

  }
}
