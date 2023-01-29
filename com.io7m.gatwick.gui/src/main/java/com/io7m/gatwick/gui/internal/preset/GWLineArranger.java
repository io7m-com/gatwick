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

  private static final Color BRANCH_RIGHT_EMPTY =
    Color.WHITE;

  private static final Color BRANCH_LEFT_EMPTY =
    Color.WHITE;

  private static final Color BRANCH_BOTH_EMPTY =
    Color.WHITE;

  private static final Color BRANCH_LEFT_NON_EMPTY =
    Color.WHITE;

  private static final Color BRANCH_RIGHT_NON_EMPTY =
    Color.WHITE;

  private static final Color JOIN =
    Color.WHITE;

  private static final Color BLOCK =
    Color.WHITE;

  public List<Line> arrange()
  {
    this.lines.clear();

    for (final var node : this.graph.elements()) {
      final var element = node.element();
      final var shape = this.nodeShapes.get(element);

      if (node instanceof GWChainGraphBranchType branch) {
        this.arrangeBranch(shape, branch);
        continue;
      }

      if (node instanceof GWChainGraphJoinType join) {
        this.arrangeJoin(shape, join);
        continue;
      }

      if (node instanceof GWChainGraphBranchRightLegType) {
        continue;
      }

      if (node instanceof GWChainGraphBlockType block) {
        this.arrangeBlock(shape, block);
      }
    }

    return List.copyOf(this.lines);
  }

  private void arrangeJoin(
    final GWNodeShape shape,
    final GWChainGraphJoinType join)
  {
    join.next().ifPresent(next -> {
      final var shapeRight =
        this.nodeShapes.get(next.element());

      this.createLineTo(
        JOIN,
        shape.centerX(),
        shape.centerY(),
        shapeRight.centerX(),
        shapeRight.centerY()
      );
    });
  }

  private void arrangeBlock(
    final GWNodeShape shape,
    final GWChainGraphBlockType block)
  {
    block.next().ifPresent(next -> {

      /*
       * If the "next" node is the start of the right leg of a branch,
       * then we actually want to draw a line to the end of the branch
       * instead.
       */

      final GWChainGraphNodeType nextTarget;
      if (next instanceof GWChainGraphBranchRightLegType right) {
        final var branch =
          right.branch();
        final var endOfRight =
          branch.endOfRightBranch();
        final var endOfBranch =
          endOfRight.next();

        if (endOfBranch.isEmpty()) {
          return;
        }
        nextTarget = endOfBranch.get();
      } else {
        nextTarget = next;
      }

      final var shapeNext =
        this.nodeShapes.get(nextTarget.element());

      this.createLineTo(
        BLOCK,
        shape.centerX(),
        shape.centerY(),
        shapeNext.centerX(),
        shapeNext.centerY()
      );
    });
  }

  private void arrangeBranch(
    final GWNodeShape shape,
    final GWChainGraphBranchType branch)
  {
    final var emptyLeft =
      branch.leftLength() == 0;
    final var emptyRight =
      branch.rightLength() == 0;

    /*
     * If both branches are empty, then we need to draw flat line
     * going from the start of the branch to the node after the end
     * of the branch.
     */

    if (emptyLeft && emptyRight) {
      final var shapeRight =
        this.nodeShapes.get(
          branch.right()
            .next()
            .orElseThrow()
            .element()
        );

      this.createLineTo(
        BRANCH_BOTH_EMPTY,
        shape.centerX(),
        shape.centerY(),
        shapeRight.centerX(),
        shapeRight.centerY()
      );
      return;
    }

    /*
     * If the left branch is empty, then we need to draw flat line
     * going from the start of the branch to the node after the end
     * of the branch.
     */

    if (emptyLeft) {
      final var endOfRight =
        branch.endOfRightBranch()
          .element();
      final var shapeRight =
        this.nodeShapes.get(endOfRight);

      this.createLineTo(
        BRANCH_LEFT_EMPTY,
        shape.centerX(),
        shape.centerY(),
        shapeRight.centerX(),
        shapeRight.centerY()
      );
    } else {
      final var shapeRight =
        this.nodeShapes.get(
          branch.next()
            .orElseThrow()
            .element()
        );
      this.createLineTo(
        BRANCH_LEFT_NON_EMPTY,
        shape.centerX(),
        shape.centerY(),
        shapeRight.centerX(),
        shapeRight.centerY()
      );
    }

    /*
     * If the right branch is empty, then we need to draw a segmented
     * line going through the (invisible) node that marks the start
     * of the right branch.
     */

    if (emptyRight) {
      final var endOfBranch =
        branch.endOfRightBranch();

      final var shapeTarget0 =
        this.nodeShapes.get(branch.right().element());
      final var shapeTarget1 =
        this.nodeShapes.get(endOfBranch.element());

      this.createLineTo(
        BRANCH_RIGHT_EMPTY,
        shape.centerX(),
        shape.centerY(),
        shapeTarget0.centerX(),
        shapeTarget0.centerY()
      );
      this.createLineTo(
        BRANCH_RIGHT_EMPTY,
        shapeTarget0.centerX(),
        shapeTarget0.centerY(),
        shapeTarget1.centerX(),
        shapeTarget1.centerY()
      );
    } else {
      final var shapeRight =
        this.nodeShapes.get(
          branch.right()
            .element()
        );
      this.createLineTo(
        BRANCH_RIGHT_NON_EMPTY,
        shape.centerX(),
        shape.centerY(),
        shapeRight.centerX(),
        shapeRight.centerY()
      );
    }
  }

  private void createLineTo(
    final Color color,
    final double p0x,
    final double p0y,
    final double p1x,
    final double p1y)
  {
    final var e0cx = (long) (Math.floor(p0x) + 0.5);
    final var e0cy = (long) (Math.floor(p0y) + 0.5);
    final var e1cx = (long) (Math.floor(p1x) + 0.5);
    final var e1cy = (long) (Math.floor(p1y) + 0.5);

    /*
     * The destination point is below the starting point. Create
     * an L shape.
     */

    if (e1cy > e0cy) {
      this.addLine(color, e0cx, e0cy, e0cx, e1cy);
      this.addLine(color, e0cx, e1cy, e1cx, e1cy);
      return;
    }

    /*
     * The destination point is above the starting point. Create
     * an inverted L shape.
     */

    if (e1cy < e0cy) {
      this.addLine(color, e0cx, e0cy, e1cx, e0cy);
      this.addLine(color, e1cx, e0cy, e1cx, e1cy);
      return;
    }

    /*
     * Otherwise, create a flat horizontal line.
     */

    this.addLine(color, e0cx, e0cy, e1cx, e1cy);
  }

  private void addLine(
    final Color color,
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
    line.setFill(color);
    line.setStroke(color);
    line.setStrokeWidth(2.0);
    this.lines.add(line);
  }
}
