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
import com.io7m.gatwick.controller.api.GWChainGraphNodeType;
import com.io7m.gatwick.controller.api.GWChainGraphNodeType.GWChainGraphBlockType;
import com.io7m.gatwick.controller.api.GWChainGraphNodeType.GWChainGraphBranchRightLegType;
import com.io7m.gatwick.controller.api.GWChainGraphNodeType.GWChainGraphBranchType;
import com.io7m.gatwick.controller.api.GWChainGraphNodeType.GWChainGraphJoinType;
import com.io7m.gatwick.controller.api.GWChainGraphType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;

final class GWLineArranger
{
  private final GWChainGraphType graph;
  private final EnumMap<GWChainElementValue, GWNodeShape> nodeShapes;
  private final ArrayList<Line> lines;

  GWLineArranger(
    final GWChainGraphType inGraph,
    final EnumMap<GWChainElementValue, GWNodeShape> inNodeShapes)
  {
    this.graph =
      Objects.requireNonNull(inGraph, "graph");
    this.nodeShapes =
      Objects.requireNonNull(inNodeShapes, "chainNodes");
    this.lines =
      new ArrayList<Line>();
  }

  public List<Line> arrange()
  {
    this.lines.clear();
    this.arrangeFromNode(this.graph.first());
    return List.copyOf(this.lines);
  }

  private void arrangeFromNode(
    final GWChainGraphNodeType start)
  {
    GWChainGraphNodeType current = start;
    while (current != null) {
      if (current instanceof GWChainGraphBranchType branch) {
        current = this.arrangeBranch(branch);
        continue;
      }

      if (current instanceof GWChainGraphBlockType block) {
        this.arrangeLineToPrevious(block);
      }

      current = current.next().orElse(null);
    }
  }

  private GWChainGraphNodeType arrangeBranch(
    final GWChainGraphBranchType branch)
  {
    final var left =
      branch.left();
    final var right =
      branch.right();

    this.arrangeLineToPrevious(branch);

    if (Objects.equals(left, right)) {
      final var mixer =
        right.next().orElse(null);
      this.arrangeLineBetweenDirect(
        this.nodeShapes.get(branch.element()),
        this.nodeShapes.get(mixer.element())
      );
      return mixer;
    }

    final var shapeBranch =
      this.nodeShapes.get(branch.element());
    final var shapeFirstLeft =
      this.nodeShapes.get(left.element());
    final var shapeFirstRight =
      this.nodeShapes.get(right.next().orElse(right).element());

    /*
     * Add connecting lines from the branch to the start of the left and
     * right legs of the branch.
     */

    final var bcx = shapeBranch.centerX();
    final var bcy = shapeBranch.centerY();
    final var lcx = shapeFirstLeft.centerX();
    final var lcy = shapeFirstLeft.centerY();
    this.addLine(bcx, bcy, lcx, lcy);

    final var rcy = shapeFirstRight.centerY();
    this.addLine(bcx, bcy, bcx, rcy);

    final var rcx = shapeFirstRight.centerX();
    this.addLine(bcx, rcy, rcx, rcy);

    final var endL =
      this.arrangeBranchLegLeft(left)
        .previous()
        .orElse(null);

    final var mixer =
      this.arrangeBranchLegRight(right);

    final var endR =
      mixer.previous()
        .orElse(null);

    final var shapeMixer =
      this.nodeShapes.get(mixer.element());
    final var shapeEndLeft =
      this.nodeShapes.get(endL.element());
    final var shapeEndRight =
      this.nodeShapes.get(endR.element());

    /*
     * Add connecting lines from the ends of the left and right branch legs
     * to the mixer node.
     */

    {
      final var elcx = shapeEndLeft.centerX();
      final var elcy = shapeEndLeft.centerY();
      final var mcx = shapeMixer.centerX();
      final var mcy = shapeMixer.centerY();
      this.addLine(elcx, elcy, mcx, mcy);
    }

    {
      final var ercx = shapeEndRight.centerX();
      final var ercy = shapeEndRight.centerY();
      final var mcx = shapeMixer.centerX();
      final var mcy = shapeMixer.centerY();
      this.addLine(ercx, ercy, mcx, ercy);
      this.addLine(mcx, mcy, mcx, ercy);
    }

    return mixer;
  }

  private GWChainGraphNodeType arrangeBranchLegRight(
    final GWChainGraphBranchRightLegType start)
  {
    GWChainGraphNodeType current = start;
    while (current != null) {
      if (current instanceof GWChainGraphJoinType) {
        return current;
      }

      if (current instanceof GWChainGraphBlockType block) {
        this.arrangeLineToPrevious(block);
      }

      current = current.next().orElse(null);
    }
    return current;
  }

  private void arrangeLineToPrevious(
    final GWChainGraphNodeType current)
  {
    final var shapeCurrent =
      this.nodeShapes.get(current.element());

    final var previous =
      current.previous().orElse(null);

    if (previous == null) {
      return;
    }

    var suitable = previous instanceof GWChainGraphBlockType;
    suitable |= previous instanceof GWChainGraphJoinType;

    if (suitable) {
      final var shapePrevious =
        this.nodeShapes.get(previous.element());
      this.arrangeLineBetweenDirect(shapeCurrent, shapePrevious);
    }
  }

  private void arrangeLineBetweenDirect(
    final GWNodeShape shapeCurrent,
    final GWNodeShape shapePrevious)
  {
    final var p0x = shapePrevious.centerX();
    final var p0y = shapePrevious.centerY();
    final var p1x = shapeCurrent.centerX();
    final var p1y = shapeCurrent.centerY();
    this.addLine(p0x, p0y, p1x, p1y);
  }

  private void addLine(
    final double p0x,
    final double p0y,
    final double p1x,
    final double p1y)
  {
    final var e0cx = Math.floor(p0x) + 0.5;
    final var e0cy = Math.floor(p0y) + 0.5;
    final var e1cx = Math.floor(p1x) + 0.5;
    final var e1cy = Math.floor(p1y) + 0.5;
    final var line = new Line(e0cx, e0cy, e1cx, e1cy);
    line.setFill(Color.WHITE);
    line.setStroke(Color.WHITE);
    this.lines.add(line);
  }

  private GWChainGraphNodeType arrangeBranchLegLeft(
    final GWChainGraphNodeType start)
  {
    GWChainGraphNodeType current = start;
    while (current != null) {
      if (current instanceof GWChainGraphBranchType branch) {
        if (!current.equals(start)) {
          current = this.arrangeBranch(branch);
          continue;
        }
      }

      if (current instanceof GWChainGraphBlockType block) {
        this.arrangeLineToPrevious(block);
      }

      if (current instanceof GWChainGraphBranchRightLegType) {
        return current;
      }
      current = current.next().orElse(null);
    }
    return current;
  }
}
