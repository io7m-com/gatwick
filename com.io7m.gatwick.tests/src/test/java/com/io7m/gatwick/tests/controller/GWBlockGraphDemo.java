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

import com.io7m.gatwick.gui.internal.preset.GWBlockGraph;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public final class GWBlockGraphDemo
{
  private GWBlockGraphDemo()
  {

  }

  public static void main(
    final String[] args)
  {
    Platform.startup(() -> {
      final var graph = new GWBlockGraph();
      graph.setPrefWidth(Region.USE_COMPUTED_SIZE);
      graph.setPrefHeight(Region.USE_COMPUTED_SIZE);

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
