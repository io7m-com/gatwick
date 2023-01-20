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
import com.io7m.gatwick.iovar.GWIOVariableType;
import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * A panel for the NS2 block.
 */

public final class GWEffectBlockPanelNS2
  extends GWEffectBlockPanel<GWOnOffValue>
{
  /**
   * A panel for the NS2 block.
   *
   * @param services The service directory
   */

  public GWEffectBlockPanelNS2(
    final RPServiceDirectoryType services)
  {
    super(services);
  }

  @Override
  protected Optional<GWIOVariableType<GWOnOffValue>>
  selectableType(
    final GWControllerType device)
  {
    return Optional.empty();
  }

  @Override
  protected List<GWIOVariableType<?>> variablesForDials(
    final GWControllerType device,
    final GWOnOffValue type)
  {
    return device.patchCurrent()
      .ns2()
      .variables();
  }

  @Override
  protected void readFromDevice()
  {
    final var service = this.gtService();
    service.executeOnDevice(ctrl -> {
      ctrl.patchCurrent()
        .ns2()
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
