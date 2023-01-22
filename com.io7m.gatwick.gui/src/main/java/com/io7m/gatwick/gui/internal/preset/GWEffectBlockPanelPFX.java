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
import com.io7m.gatwick.controller.api.GWPatchPedalFXTypeValue;
import com.io7m.gatwick.iovar.GWIOVariableType;
import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.io7m.gatwick.gui.internal.gt.GWGTK1LongRunning.TASK_LONG;

/**
 * A panel for the PFX block.
 */

public final class GWEffectBlockPanelPFX
  extends GWEffectBlockPanel<GWPatchPedalFXTypeValue>
{
  /**
   * A panel for the PFX block.
   *
   * @param services The service directory
   */

  public GWEffectBlockPanelPFX(
    final RPServiceDirectoryType services)
  {
    super(services);
  }

  @Override
  protected Optional<GWIOVariableType<GWPatchPedalFXTypeValue>> selectableType(
    final GWControllerType device)
  {
    return Optional.of(
      device.patchCurrent()
        .pfx()
        .type()
    );
  }

  @Override
  protected List<GWIOVariableType<?>> variablesForDials(
    final GWControllerType device,
    final GWPatchPedalFXTypeValue type)
  {
    final var pfx =
      device.patchCurrent().pfx();

    return switch (type) {
      case WAH -> {
        yield List.of(
          pfx.enabled(),
          pfx.wahType(),
          pfx.wahPosition(),
          pfx.pedalMinimum(),
          pfx.pedalMaximum(),
          pfx.directMix(),
          pfx.effectLevel()
        );
      }
      case PEDAL_BEND -> {
        yield List.of(
          pfx.enabled(),
          pfx.bendPitchMinimum(),
          pfx.bendPitchMaximum(),
          pfx.bendPosition(),
          pfx.pedalMinimum(),
          pfx.pedalMaximum(),
          pfx.directMix(),
          pfx.effectLevel()
        );
      }
    };
  }

  @Override
  protected void readFromDevice()
  {
    final var service = this.gtService();
    service.executeOnDevice(TASK_LONG, ctrl -> {
      ctrl.patchCurrent()
        .pfx()
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
