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

import com.io7m.gatwick.controller.api.GWChainElementValue;
import com.io7m.gatwick.controller.api.GWControllerType;
import com.io7m.gatwick.gui.internal.GWScreenControllerType;
import com.io7m.gatwick.gui.internal.gt.GWGT1KServiceStatusType;
import com.io7m.gatwick.gui.internal.gt.GWGT1KServiceType;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.EnumMap;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.io7m.gatwick.controller.api.GWChainElementValue.PEDAL_FX;

/**
 * A controller for a single preset.
 */

public final class GWPresetController implements GWScreenControllerType
{
  private final RPServiceDirectoryType services;
  private final GWGT1KServiceType gt;
  private final EnumMap<GWChainElementValue, GWEffectBlockPanel<?>> panels;

  @FXML private Pane deviceIsClosedContainer;
  @FXML private Pane deviceIsOpenContainer;
  @FXML private Pane dialsContainer;
  @FXML private ScrollPane blockGraphContainer;
  @FXML private Pane presetHeader;
  @FXML private Label presetHeaderText;
  @FXML private Label presetHeaderTextShadow;
  @FXML private ImageView presetHeaderIcon;

  private GWBlockGraph blockGraph;

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
    this.panels =
      new EnumMap<>(GWChainElementValue.class);

    for (final var name : GWChainElementValue.values()) {
      this.panels.put(name, switch (name) {
        case AIRD_PREAMP_1 -> new GWEffectBlockPanelPreamp1(this.services);
        case AIRD_PREAMP_2 -> new GWEffectBlockPanelPreamp2(this.services);
        case BRANCH_SPLIT1 -> null;
        case BRANCH_SPLIT2 -> null;
        case BRANCH_SPLIT3 -> null;
        case BYPASS_MAIN_L -> null;
        case BYPASS_MAIN_R -> null;
        case BYPASS_SUB_L -> null;
        case BYPASS_SUB_R -> null;
        case CHORUS -> null;
        case COMPRESSOR -> new GWEffectBlockPanelCMP(this.services);
        case DELAY_1 -> null;
        case DELAY_2 -> null;
        case DELAY_3 -> null;
        case DELAY_4 -> null;
        case DISTORTION_1 -> new GWEffectBlockPanelDS1(this.services);
        case DISTORTION_2 -> new GWEffectBlockPanelDS2(this.services);
        case DIVIDER_1 -> null;
        case DIVIDER_2 -> null;
        case DIVIDER_3 -> null;
        case EQUALIZER_1 -> null;
        case EQUALIZER_2 -> null;
        case EQUALIZER_3 -> null;
        case EQUALIZER_4 -> null;
        case FOOT_VOLUME -> null;
        case FX_1 -> new GWEffectBlockPanelFX1(this.services);
        case FX_2 -> new GWEffectBlockPanelFX2(this.services);
        case FX_3 -> new GWEffectBlockPanelFX3(this.services);
        case FX_4 -> null;
        case LOOPER -> null;
        case MAIN_OUT_L -> null;
        case MAIN_OUT_R -> null;
        case MAIN_SP_SIMULATOR_L -> null;
        case MAIN_SP_SIMULATOR_R -> null;
        case MASTER_DELAY -> null;
        case MIXER_1 -> null;
        case MIXER_2 -> null;
        case MIXER_3 -> null;
        case NOISE_SUPPRESSOR_1 -> new GWEffectBlockPanelNS1(this.services);
        case NOISE_SUPPRESSOR_2 -> new GWEffectBlockPanelNS2(this.services);
        case PEDAL_FX -> new GWEffectBlockPanelPFX(this.services);
        case RESERVED_44 -> null;
        case REVERB -> null;
        case SEND_SLASH_RETURN_1 -> null;
        case SEND_SLASH_RETURN_2 -> null;
        case SUB_OUT_L -> null;
        case SUB_OUT_R -> null;
        case SUB_SP_SIMULATOR_L -> null;
        case SUB_SP_SIMULATOR_R -> null;
      });
    }

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
    this.blockGraph = new GWBlockGraph();

    this.presetHeader.setBackground(null);
    this.presetHeaderTextShadow.setText("");
    this.presetHeaderText.setText("");
    this.presetHeaderIcon.setImage(null);

    this.blockGraph.selectedNode()
      .addListener((observable, oldValue, newValue) -> {
        this.onNodeSelected(newValue);
      });

    this.deviceIsClosedContainer.managedProperty()
      .bind(this.deviceIsClosedContainer.visibleProperty());
    this.deviceIsOpenContainer.managedProperty()
      .bind(this.deviceIsOpenContainer.visibleProperty());

    this.blockGraphContainer.contentProperty()
      .set(this.blockGraph);

    this.deviceIsClosedContainer.setVisible(true);
    this.deviceIsOpenContainer.setVisible(false);

    this.blockGraph.select(PEDAL_FX);
  }

  private void onNodeSelected(
    final GWNodeShape node)
  {
    final var dials = this.dialsContainer.getChildren();
    dials.clear();

    if (node == null) {
      this.presetHeaderTextShadow.setText("");
      this.presetHeaderText.setText("");
      this.presetHeaderIcon.setImage(null);
      return;
    }

    final var info =
      GWChainElementValue.info();
    final var label =
      info.label(node.name());

    final var newBackground =
      new Background(new BackgroundFill(node.mainColor(), null, null));

    this.presetHeaderTextShadow.setText(label);
    this.presetHeaderText.setText(label);
    this.presetHeaderIcon.setImage(node.icon());
    this.presetHeader.setBackground(newBackground);

    final var panel =
      this.panels.get(node.name());

    if (panel != null) {
      dials.add(panel);
      panel.readFromDevice();
    }
  }
}
