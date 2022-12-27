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
import com.io7m.gatwick.gui.internal.GWScreenControllerType;
import com.io7m.gatwick.gui.internal.gt.GWGT1EffectBlockPanelCMP;
import com.io7m.gatwick.gui.internal.gt.GWGT1KServiceStatusType;
import com.io7m.gatwick.gui.internal.gt.GWGT1KServiceType;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * A controller for a single preset.
 */

public final class GWPresetController implements GWScreenControllerType
{
  private final RPServiceDirectoryType services;
  private final GWGT1KServiceType gt;

  @FXML private Pane deviceIsClosedContainer;
  @FXML private Pane deviceIsOpenContainer;
  @FXML private Pane dialsContainer;
  @FXML private Pane blockGraphContainer;

  /**
   * A controller for a single preset.
   *
   * @param inServices The service directory
   */

  public GWPresetController(
    final RPServiceDirectoryType inServices)
  {
    this.services =
      Objects.requireNonNull(inServices, "services");
    this.gt =
      this.services.requireService(GWGT1KServiceType.class);
    this.gt.status()
      .addListener((observable, oldValue, newValue) -> {
        this.onGTStateChanged(oldValue, newValue);
      });
  }

  private void onGTStateChanged(
    final GWGT1KServiceStatusType oldStatus,
    final GWGT1KServiceStatusType newStatus)
  {
    if (oldStatus instanceof GWGT1KServiceStatusType.GWGT1KServiceStatusClosedType) {
      if (newStatus instanceof GWGT1KServiceStatusType.GWGT1KServiceStatusOpenType statusOpen) {
        this.onDeviceBecameAvailable(statusOpen.device());
        return;
      }
    }

    if (oldStatus instanceof GWGT1KServiceStatusType.GWGT1KServiceStatusOpenType) {
      if (newStatus instanceof GWGT1KServiceStatusType.GWGT1KServiceStatusClosedType) {
        this.onDeviceBecameUnavailable();
        return;
      }
    }
  }

  private void onDeviceBecameUnavailable()
  {
    this.deviceIsClosedContainer.setVisible(true);
    this.deviceIsOpenContainer.setVisible(false);
  }

  private void onDeviceBecameAvailable(
    final GWControllerType device)
  {
    this.deviceIsClosedContainer.setVisible(false);
    this.deviceIsOpenContainer.setVisible(true);
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.deviceIsClosedContainer.managedProperty()
      .bind(this.deviceIsClosedContainer.visibleProperty());
    this.deviceIsOpenContainer.managedProperty()
      .bind(this.deviceIsOpenContainer.visibleProperty());

    this.deviceIsClosedContainer.setVisible(true);
    this.deviceIsOpenContainer.setVisible(false);

    final var dials =
      this.dialsContainer.getChildren();

    dials.add(new GWGT1EffectBlockPanelCMP(this.services));
  }
}
