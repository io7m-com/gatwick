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
import com.io7m.gatwick.controller.api.GWChainGraphNodeType.GWChainGraphBranchRightLegType;
import com.io7m.gatwick.controller.api.GWChainGraphNodeType.GWChainGraphBranchType;
import com.io7m.gatwick.controller.api.GWChainGraphNodeType.GWChainGraphJoinType;
import com.io7m.gatwick.controller.api.GWChainGraphType;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;

import static java.lang.Math.max;

final class GWNodeArranger
{
  private final GWChainGraphType graph;
  private final EnumMap<GWChainElementValue, GWNodeShape> nodeShapes;
  private final EnumSet<GWChainElementValue> processed;
  private final HashMap<GWChainElementValue, State> states;

  private static final class State
  {
    private int x;
    private int depth;

    State()
    {

    }
  }

  GWNodeArranger(
    final GWChainGraphType inGraph,
    final EnumMap<GWChainElementValue, GWNodeShape> inNodeShapes)
  {
    this.graph =
      Objects.requireNonNull(inGraph, "graph");
    this.nodeShapes =
      Objects.requireNonNull(inNodeShapes, "chainNodes");
    this.processed =
      EnumSet.allOf(GWChainElementValue.class);
    this.states =
      new HashMap<>();
  }

  public void arrange()
  {
    for (final var value : this.processed) {
      this.states.put(value, new State());
    }

    /*
     * Determine the depths of each node based on the branch they
     * appear in.
     */

    {
      int depth = 0;
      for (final var node : this.graph.elements()) {
        final var element = node.element();
        final var state = this.states.get(element);
        if (node instanceof GWChainGraphJoinType) {
          --depth;
        }
        if (node instanceof GWChainGraphBranchRightLegType) {
          ++depth;
        }
        state.depth = depth;
      }
    }

    /*
     * Determine the horizontal offsets of each node.
     */

    {
      final var memorizer = new XOffsetMemorizer();
      for (final var node : this.graph.elements()) {
        final var element = node.element();
        final var state = this.states.get(element);
        final var shape = this.nodeShapes.get(element);

        if (node instanceof GWChainGraphBranchType) {
          state.x = memorizer.startBranchLeft(shape);
        } else if (node instanceof GWChainGraphBranchRightLegType) {
          state.x = memorizer.startBranchRight();
        } else if (node instanceof GWChainGraphJoinType) {
          state.x = memorizer.finishBranch(shape);
        } else {
          state.x = memorizer.addNode(shape);
        }
      }
    }

    /*
     * Configure all the node shapes.
     */

    for (final var node : this.graph.elements()) {
      final var element = node.element();
      final var state = this.states.get(element);
      final var shape = this.nodeShapes.get(element);
      shape.setLayoutX(state.x);
      shape.setLayoutY(state.depth * 48.0);
    }
  }

  private static final class XOffsetMemorizer
  {
    private final LinkedList<BranchState> branches;

    private static final class BranchState
    {
      private final int xStart;
      private int xMax;
      private int xNow;
      private GWNodeShape branchStart;

      BranchState(
        final int inXStart)
      {
        this.xStart = inXStart;
        this.xMax = inXStart;
        this.xNow = inXStart;
      }

      BranchState(
        final GWNodeShape inBranchStart,
        final int inXStart)
      {
        this(inXStart);
        this.branchStart = inBranchStart;
        this.add((int) inBranchStart.getWidth());
      }

      void add(
        final int x)
      {
        this.setX(this.xNow + x);
      }

      void resetX()
      {
        final var offset =
          Optional.ofNullable(this.branchStart)
            .stream()
            .mapToInt(s -> (int) s.getWidth())
            .findFirst()
            .orElse(0);

        this.setX(this.xStart + offset);
      }

      void setX(
        final int x)
      {
        this.xNow = x;
        this.xMax = max(this.xMax, this.xNow);
      }
    }

    XOffsetMemorizer()
    {
      this.branches = new LinkedList<>();
      this.branches.add(new BranchState(0));
    }

    int startBranchLeft(
      final GWNodeShape branch)
    {
      final var branchNow = this.branches.peek();
      final var x = branchNow.xNow;
      final var branchNew = new BranchState(branch, branchNow.xNow);
      this.branches.push(branchNew);
      return x;
    }

    int startBranchRight()
    {
      final var branchNow = this.branches.peek();
      branchNow.resetX();
      return branchNow.xNow;
    }

    int finishBranch(
      final GWNodeShape shape)
    {
      final var branchThen = this.branches.pop();
      final var branchNow = this.branches.peek();
      branchNow.setX(branchThen.xMax);
      final var x = branchNow.xNow;
      branchNow.add((int) shape.getWidth());
      return x;
    }

    int addNode(
      final GWNodeShape shape)
    {
      final var branchNow = this.branches.peek();
      final var x = branchNow.xNow;
      branchNow.add((int) shape.getWidth());
      return x;
    }
  }
}
