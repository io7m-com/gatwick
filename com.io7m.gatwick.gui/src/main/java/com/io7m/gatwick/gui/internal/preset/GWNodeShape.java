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
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Objects;

abstract sealed class GWNodeShape
  extends Region permits GWNodeShapeBlock,
  GWNodeShapeBypass,
  GWNodeShapeDivider,
  GWNodeShapeLooper,
  GWNodeShapeMixer,
  GWNodeShapeNull,
  GWNodeShapeOutput,
  GWNodeShapeSendReturn,
  GWNodeShapeSpeakerSim,
  GWNodeShapeTerminator
{
  protected static final double HOVER_BRIGHTNESS_FACTOR = 1.25;

  protected static final Font NODE_LABEL_FONT =
    Font.font("Monospaced", FontWeight.EXTRA_BOLD, 13.0);

  protected static final Font NODE_LABEL_SHADOW_FONT =
    Font.font("Monospaced", FontWeight.BOLD, 13.0);

  protected static final Color NODE_SELECTION_COLOR =
    Color.web("00f9c9");

  protected static final double NODE_SELECTION_WEIGHT = 7.0;

  private final GWChainElementValue name;
  private final SimpleObjectProperty<GWNodeShape> selected;
  private final Image icon;

  protected GWNodeShape(
    final SimpleObjectProperty<GWNodeShape> inSelected,
    final Image inIcon,
    final GWChainElementValue inName)
  {
    this.name =
      Objects.requireNonNull(inName, "inName");
    this.icon =
      Objects.requireNonNull(inIcon, "inIcon");
    this.selected =
      Objects.requireNonNull(inSelected, "inSelected");

    this.selected.addListener((observable, oldValue, newValue) -> {
      final var node = this.highlightable();
      if (Objects.equals(newValue, this)) {
        node.setVisible(true);
      } else {
        node.setVisible(false);
      }
    });
  }

  protected final SimpleObjectProperty<GWNodeShape> selected()
  {
    return this.selected;
  }

  static String nodeLabel(
    final GWChainElementValue value)
  {
    return switch (value) {
      case AIRD_PREAMP_1 -> "AMP1";
      case AIRD_PREAMP_2 -> "AMP2";
      case BRANCH_SPLIT1 -> "";
      case BRANCH_SPLIT2 -> "";
      case BRANCH_SPLIT3 -> "";
      case BYPASS_MAIN_L -> "BYPASS MAIN/L";
      case BYPASS_MAIN_R -> "BYPASS MAIN/R";
      case BYPASS_SUB_L -> "BYPASS SUB/L";
      case BYPASS_SUB_R -> "BYPASS SUB/R";
      case CHORUS -> "CHO";
      case COMPRESSOR -> "CMP";
      case DELAY_1 -> "DLY1";
      case DELAY_2 -> "DLY2";
      case DELAY_3 -> "DLY3";
      case DELAY_4 -> "DLY4";
      case DISTORTION_1 -> "DS1";
      case DISTORTION_2 -> "DS2";
      case DIVIDER_1 -> "DIV1";
      case DIVIDER_2 -> "DIV2";
      case DIVIDER_3 -> "DIV3";
      case EQUALIZER_1 -> "EQ1";
      case EQUALIZER_2 -> "EQ2";
      case EQUALIZER_3 -> "EQ3";
      case EQUALIZER_4 -> "EQ4";
      case FOOT_VOLUME -> "FVOL";
      case FX_1 -> "FX1";
      case FX_2 -> "FX2";
      case FX_3 -> "FX3";
      case FX_4 -> "FX4";
      case LOOPER -> "LOOPER";
      case MAIN_OUT_L -> "MAIN OUT/L";
      case MAIN_OUT_R -> "MAIN OUT/R";
      case MAIN_SP_SIMULATOR_L -> "MAIN SP/L";
      case MAIN_SP_SIMULATOR_R -> "MAIN SP/R";
      case MASTER_DELAY -> "MDLY";
      case MIXER_1 -> "MIX1";
      case MIXER_2 -> "MIX2";
      case MIXER_3 -> "MIX3";
      case NOISE_SUPPRESSOR_1 -> "NS1";
      case NOISE_SUPPRESSOR_2 -> "NS2";
      case PEDAL_FX -> "PFX";
      case RESERVED_44 -> "";
      case REVERB -> "REV";
      case SEND_SLASH_RETURN_1 -> "SEND/RET 1";
      case SEND_SLASH_RETURN_2 -> "SEND/RET 2";
      case SUB_OUT_L -> "SUB OUT/L";
      case SUB_OUT_R -> "SUB OUT/R";
      case SUB_SP_SIMULATOR_L -> "SUB SP/L";
      case SUB_SP_SIMULATOR_R -> "SUB SP/R";
    };
  }

  protected abstract Node highlightable();

  public abstract Color mainColor();

  public final Image icon()
  {
    return this.icon;
  }

  public abstract Node clickable();

  public final GWChainElementValue name()
  {
    return this.name;
  }

  public abstract double centerX();

  public abstract double centerY();
}
