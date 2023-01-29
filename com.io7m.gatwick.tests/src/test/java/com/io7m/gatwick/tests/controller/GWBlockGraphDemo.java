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

package com.io7m.gatwick.tests.controller;

import com.io7m.gatwick.controller.api.GWChain;
import com.io7m.gatwick.gui.internal.preset.GWBlockGraph;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

import static com.io7m.gatwick.controller.api.GWChainElementValue.*;

public final class GWBlockGraphDemo
{
  private GWBlockGraphDemo()
  {

  }

  public static void main(
    final String[] args)
  {
    Platform.startup(() -> {
      final var elements = List.of(
        PEDAL_FX,
        COMPRESSOR,
        EQUALIZER_3,
        FX_1,
        DIVIDER_1,
        DISTORTION_1,
        SEND_SLASH_RETURN_1,
        AIRD_PREAMP_1,
        NOISE_SUPPRESSOR_1,
        EQUALIZER_1,
        BRANCH_SPLIT1,
        DISTORTION_2,
        SEND_SLASH_RETURN_2,
        AIRD_PREAMP_2,
        NOISE_SUPPRESSOR_2,
        EQUALIZER_2,
        MIXER_1,
        FOOT_VOLUME,
        DIVIDER_2,
        BRANCH_SPLIT2,
        DIVIDER_3,
        EQUALIZER_4,
        FX_2,
        FX_3,
        FX_4,
        DELAY_3,
        BRANCH_SPLIT3,
        DELAY_4,
        MIXER_3,
        MIXER_2,
        DELAY_1,
        DELAY_2,
        MASTER_DELAY,
        CHORUS,
        LOOPER,
        REVERB,
        BYPASS_MAIN_R,
        MAIN_SP_SIMULATOR_L,
        MAIN_SP_SIMULATOR_R,
        BYPASS_MAIN_L,
        MAIN_OUT_L,
        MAIN_OUT_R,
        BYPASS_SUB_R,
        SUB_SP_SIMULATOR_L,
        SUB_SP_SIMULATOR_R,
        BYPASS_SUB_L,
        SUB_OUT_L,
        SUB_OUT_R,
        RESERVED_44
      );

      final var graph = new GWBlockGraph(GWChain.of(elements));
      graph.setPrefWidth(Region.USE_COMPUTED_SIZE);
      graph.setPrefHeight(Region.USE_COMPUTED_SIZE);
      graph.select(PEDAL_FX);
      graph.setBackground(Background.fill(Color.DARKGREY));

      final var pane = new ScrollPane(graph);
      final var stage = new Stage();
      stage.setMinWidth(1280.0);
      stage.setMinHeight(300);
      stage.setWidth(1280.0);
      stage.setHeight(300);
      stage.setScene(new Scene(pane));
      stage.show();
    });
  }
}
