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
import com.io7m.gatwick.controller.api.GWChainGraphType;
import com.io7m.jaffirm.core.Postconditions;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Objects;

final class GWNodeArranger
{
  private final GWChainGraphType graph;
  private final EnumMap<GWChainElementValue, GWNodeShape> nodeShapes;
  private final EnumSet<GWChainElementValue> processed;
  private final GWXOffsetStack xStack;

  GWNodeArranger(
    final GWChainGraphType inGraph,
    final EnumMap<GWChainElementValue, GWNodeShape> inNodeShapes)
  {
    this.graph =
      Objects.requireNonNull(inGraph, "graph");
    this.nodeShapes =
      Objects.requireNonNull(inNodeShapes, "chainNodes");
    this.xStack =
      new GWXOffsetStack();
    this.processed =
      EnumSet.allOf(GWChainElementValue.class);
  }

  public void arrange()
  {
    this.arrangeFromNode(this.graph.first());

    Postconditions.checkPostconditionV(
      this.processed,
      this.processed.isEmpty(),
      "Processed elements must be empty"
    );
  }

  private void arrangeFromNode(
    final GWChainGraphNodeType start)
  {
    GWChainGraphNodeType current = start;
    while (current != null) {
      if (current instanceof GWChainGraphNodeType.GWChainGraphBranchType branch) {
        current = this.arrangeBranch(branch);
        continue;
      }

      if (current instanceof GWChainGraphNodeType.GWChainGraphBlockType) {
        this.doSingleNodeLayout(current);
      }

      current = current.next().orElse(null);
    }
  }

  private GWChainGraphNodeType arrangeBranch(
    final GWChainGraphNodeType.GWChainGraphBranchType branch)
  {
    this.doSingleNodeLayout(branch);

    this.xStack.push();
    final var endL =
      this.arrangeBranchLegLeft(branch.left());
    final var endLx =
      this.xStack.peek();
    this.doSingleNodeLayout(endL);
    this.xStack.pop();

    this.xStack.push();
    final var endR =
      this.arrangeBranchLegRight(branch.right());
    final var endRx =
      this.xStack.peek();

    final var maxOffset = Math.max(endLx, endRx);
    this.xStack.pop();
    this.xStack.set(maxOffset);

    this.doSingleNodeLayout(endR);
    return endR.next().orElse(null);
  }

  private GWChainGraphNodeType arrangeBranchLegRight(
    final GWChainGraphNodeType.GWChainGraphBranchRightLegType start)
  {
    GWChainGraphNodeType current = start;
    while (current != null) {
      if (current instanceof GWChainGraphNodeType.GWChainGraphJoinType) {
        return current;
      }

      if (current instanceof GWChainGraphNodeType.GWChainGraphBlockType) {
        this.doSingleNodeLayout(current);
      }

      current = current.next().orElse(null);
    }
    return current;
  }

  private GWChainGraphNodeType arrangeBranchLegLeft(
    final GWChainGraphNodeType start)
  {
    GWChainGraphNodeType current = start;
    while (current != null) {
      if (current instanceof GWChainGraphNodeType.GWChainGraphBranchType branch) {
        if (current.equals(start)) {
          this.doSingleNodeLayout(branch);
        } else {
          current = this.arrangeBranch(branch);
          continue;
        }
      }

      if (current instanceof GWChainGraphNodeType.GWChainGraphBlockType) {
        this.doSingleNodeLayout(current);
      }

      if (current instanceof GWChainGraphNodeType.GWChainGraphBranchRightLegType) {
        return current;
      }
      current = current.next().orElse(null);
    }
    return current;
  }

  private void doSingleNodeLayout(
    final GWChainGraphNodeType node)
  {
    this.processed.remove(node.element());

    final var shape =
      this.nodeShapes.get(node.element());

    final var depth =
      node.depth();
    final var x =
      this.xStack.peek();

    final var y =
      (double) depth * shape.getHeight();

    this.xStack.set(x + shape.getWidth());

    shape.setLayoutX(x);
    shape.setLayoutY(y);
  }
}
