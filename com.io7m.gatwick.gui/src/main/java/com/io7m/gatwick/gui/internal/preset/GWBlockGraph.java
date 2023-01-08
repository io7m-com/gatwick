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

import com.io7m.gatwick.controller.api.GWChain;
import com.io7m.gatwick.controller.api.GWChainElementValue;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

import java.util.EnumMap;
import java.util.LinkedList;

/**
 * A block graph.
 */

public final class GWBlockGraph extends Pane
{
  private final SimpleObjectProperty<GWChain> chain;
  private final EnumMap<GWChainElementValue, GWNodeShape> nodeShapes;
  private final LinkedList<Line> lines;
  private final SimpleObjectProperty<GWNodeShape> selected;

  /**
   * A block graph.
   */

  public GWBlockGraph()
  {
    this.chain =
      new SimpleObjectProperty<>(GWChain.defaultChain());
    this.nodeShapes =
      new EnumMap<>(GWChainElementValue.class);
    this.lines =
      new LinkedList<Line>();
    this.selected =
      new SimpleObjectProperty<GWNodeShape>();

    final var children = this.getChildren();
    for (final var name : GWChainElementValue.values()) {
      final var chainNode = switch (name) {
        case AIRD_PREAMP_1,
          AIRD_PREAMP_2,
          CHORUS,
          COMPRESSOR,
          DELAY_1,
          DELAY_2,
          DELAY_3,
          DELAY_4,
          DISTORTION_1,
          DISTORTION_2,
          EQUALIZER_1,
          EQUALIZER_2,
          EQUALIZER_3,
          EQUALIZER_4,
          FOOT_VOLUME,
          FX_1,
          FX_2,
          FX_3,
          FX_4,
          MASTER_DELAY,
          NOISE_SUPPRESSOR_1,
          NOISE_SUPPRESSOR_2,
          PEDAL_FX,
          REVERB -> {
          yield new GWNodeShapeBlock(this.selected, name);
        }

        case RESERVED_44 -> {
          yield new GWNodeShapeTerminator(this.selected, name);
        }

        case MAIN_SP_SIMULATOR_L,
          MAIN_SP_SIMULATOR_R,
          SUB_SP_SIMULATOR_L,
          SUB_SP_SIMULATOR_R -> {
          yield new GWNodeShapeSpeakerSim(this.selected, name);
        }

        case MAIN_OUT_L,
          MAIN_OUT_R,
          SUB_OUT_L,
          SUB_OUT_R -> {
          yield new GWNodeShapeOutput(this.selected, name);
        }

        case LOOPER -> {
          yield new GWNodeShapeLooper(this.selected, name);
        }

        case SEND_SLASH_RETURN_1,
          SEND_SLASH_RETURN_2 -> {
          yield new GWNodeShapeSendReturn(this.selected, name);
        }

        case BYPASS_MAIN_L,
          BYPASS_MAIN_R,
          BYPASS_SUB_L,
          BYPASS_SUB_R -> {
          yield new GWNodeShapeBypass(this.selected, name);
        }

        case BRANCH_SPLIT1,
          BRANCH_SPLIT2,
          BRANCH_SPLIT3 -> {
          yield new GWNodeShapeNull(this.selected, name);
        }

        case MIXER_1,
          MIXER_2,
          MIXER_3 -> {
          yield new GWNodeShapeMixer(this.selected, name);
        }

        case DIVIDER_1,
          DIVIDER_2,
          DIVIDER_3 -> {
          yield new GWNodeShapeDivider(this.selected, name);
        }
      };

      this.nodeShapes.put(name, chainNode);
      children.add(chainNode);
    }

    this.setPrefHeight(USE_COMPUTED_SIZE);
    this.setPrefWidth(USE_COMPUTED_SIZE);

    this.chain.addListener(
      observable -> this.arrange());
    this.widthProperty()
      .addListener(observable -> this.arrange());
    this.heightProperty()
      .addListener(observable -> this.arrange());

    this.arrange();
  }

  /**
   * Select the given chain element.
   *
   * @param element The element
   */

  public void select(
    final GWChainElementValue element)
  {
    this.selected.set(this.nodeShapes.get(element));
  }

  /**
   * @return The currently selected node
   */

  public ReadOnlyProperty<GWNodeShape> selectedNode()
  {
    return this.selected;
  }

  private void arrange()
  {
    final var chainNow =
      this.chain.get();
    final var graph =
      chainNow.graph();

    final var nodeArranger =
      new GWNodeArranger(graph, this.nodeShapes);
    nodeArranger.arrange();

    final var lineArranger =
      new GWLineArranger(graph, this.nodeShapes);

    final var children = this.getChildren();
    children.removeAll(this.lines);
    this.lines.clear();

    final var newLines = lineArranger.arrange();
    this.lines.addAll(newLines);
    for (final var line : newLines) {
      children.add(0, line);
    }
  }

  /**
   * @return The current chain
   */

  public Property<GWChain> chain()
  {
    return this.chain;
  }

  @Override
  public boolean isResizable()
  {
    return true;
  }
}
