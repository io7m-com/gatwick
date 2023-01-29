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


package com.io7m.gatwick.controller.api;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A chain graph.
 */

public final class GWChainGraph implements GWChainGraphType
{
  private final GWChainGraphNodeType first;

  private GWChainGraph(
    final GWChainGraphNodeType inFirst)
  {
    this.first = Objects.requireNonNull(inFirst, "first");
  }

  @Override
  public GWChainGraphNodeType first()
  {
    return this.first;
  }

  @Override
  public List<GWChainGraphNodeType> elements()
  {
    final var elements = new LinkedList<GWChainGraphNodeType>();
    var current = this.first;
    while (current != null) {
      elements.add(current);
      current = current.next().orElse(null);
    }
    return List.copyOf(elements);
  }

  private static final class GWChainGraphNode
    implements GWChainGraphNodeType.GWChainGraphBlockType
  {
    private final GWChainElementValue element;
    private final int depth;
    private GWChainGraphNodeType next;
    private final GWChainGraphNodeType previous;

    GWChainGraphNode(
      final GWChainGraphNodeType inPrevious,
      final GWChainElementValue inElement,
      final int inDepth)
    {
      this.previous =
        inPrevious;
      this.element =
        Objects.requireNonNull(inElement, "inElement");
      this.depth = inDepth;
    }

    @Override
    public GWChainElementValue element()
    {
      return this.element;
    }

    @Override
    public Optional<GWChainGraphNodeType> next()
    {
      return Optional.ofNullable(this.next);
    }

    @Override
    public Optional<GWChainGraphNodeType> previous()
    {
      return Optional.ofNullable(this.previous);
    }

    @Override
    public int depth()
    {
      return this.depth;
    }

    @Override
    public String toString()
    {
      return "[GWChainGraphNode %s [depth %d]]"
        .formatted(this.element, this.depth);
    }
  }

  private static final class GWChainGraphBranch
    implements GWChainGraphNodeType.GWChainGraphBranchType
  {
    private GWChainGraphBranchRightLeg right;
    private GWChainGraphNodeType next;
    private final GWChainGraphNodeType previous;
    private final GWChainElementValue element;
    private final int depth;

    GWChainGraphBranch(
      final GWChainGraphNodeType inPrevious,
      final GWChainElementValue inElement,
      final int inDepth)
    {
      this.previous =
        inPrevious;
      this.element =
        Objects.requireNonNull(inElement, "inElement");
      this.depth = inDepth;
    }

    @Override
    public GWChainElementValue element()
    {
      return this.element;
    }

    @Override
    public Optional<GWChainGraphNodeType> next()
    {
      return Optional.ofNullable(this.next);
    }

    @Override
    public Optional<GWChainGraphNodeType> previous()
    {
      return Optional.ofNullable(this.previous);
    }

    @Override
    public int depth()
    {
      return this.depth;
    }

    @Override
    public String toString()
    {
      return "[GWChainGraphBranch %s [depth %d]]"
        .formatted(this.element, this.depth);
    }

    public GWChainElementValue closingElement()
    {
      return switch (this.element) {
        case DIVIDER_1 -> GWChainElementValue.MIXER_1;
        case DIVIDER_2 -> GWChainElementValue.MIXER_2;
        case DIVIDER_3 -> GWChainElementValue.MIXER_3;
        default -> throw new IllegalStateException();
      };
    }

    public GWChainElementValue splittingElement()
    {
      return switch (this.element) {
        case DIVIDER_1 -> GWChainElementValue.BRANCH_SPLIT1;
        case DIVIDER_2 -> GWChainElementValue.BRANCH_SPLIT2;
        case DIVIDER_3 -> GWChainElementValue.BRANCH_SPLIT3;
        default -> throw new IllegalStateException();
      };
    }

    @Override
    public GWChainGraphNodeType left()
    {
      return this.next;
    }

    @Override
    public GWChainGraphBranchRightLegType right()
    {
      return this.right;
    }

    @Override
    public GWChainGraphNodeType endOfLeftBranch()
    {
      GWChainGraphNodeType current = this;

      while (true) {
        final var nextNode =
          current.next()
            .orElse(null);

        if (nextNode == null) {
          return current;
        }
        if (nextNode.element() == this.splittingElement()) {
          return current;
        }
        current = nextNode;
      }
    }

    @Override
    public GWChainGraphNodeType endOfRightBranch()
    {
      GWChainGraphNodeType current = this.right;

      while (true) {
        final var nextNode =
          current.next()
            .orElse(null);

        if (nextNode == null) {
          return current;
        }
        if (nextNode.element() == this.closingElement()) {
          return nextNode;
        }
        current = nextNode;
      }
    }

    @Override
    public int leftLength()
    {
      int length = 0;
      GWChainGraphNodeType current = this.left();
      while (current.element() != this.splittingElement()) {
        ++length;

        current = current.next().orElse(null);
        if (current == null) {
          break;
        }
      }
      return length;
    }

    @Override
    public int rightLength()
    {
      int length = -1;
      GWChainGraphNodeType current = this.right();
      while (current.element() != this.closingElement()) {
        ++length;

        current = current.next().orElse(null);
        if (current == null) {
          break;
        }
      }
      return length;
    }
  }

  private static final class GWChainGraphBranchRightLeg
    implements GWChainGraphNodeType.GWChainGraphBranchRightLegType
  {
    private final GWChainElementValue element;
    private final int depth;
    private GWChainGraphNodeType next;
    private final GWChainGraphNodeType previous;
    private final GWChainGraphBranchType branch;

    GWChainGraphBranchRightLeg(
      final GWChainGraphNodeType inPrevious,
      final GWChainElementValue inElement,
      final int inDepth,
      final GWChainGraphBranchType inBranch)
    {
      this.previous =
        inPrevious;
      this.element =
        Objects.requireNonNull(inElement, "inElement");
      this.branch =
        Objects.requireNonNull(inBranch, "inBranch");
      this.depth =
        inDepth;
    }

    @Override
    public GWChainElementValue element()
    {
      return this.element;
    }

    @Override
    public Optional<GWChainGraphNodeType> next()
    {
      return Optional.ofNullable(this.next);
    }

    @Override
    public Optional<GWChainGraphNodeType> previous()
    {
      return Optional.ofNullable(this.previous);
    }

    @Override
    public int depth()
    {
      return 0;
    }

    @Override
    public GWChainGraphBranchType branch()
    {
      return this.branch;
    }

    @Override
    public String toString()
    {
      return "[GWChainGraphBranchRightLeg %s [depth %d]]"
        .formatted(this.element, this.depth);
    }
  }

  private static final class GWChainGraphJoin
    implements GWChainGraphNodeType.GWChainGraphJoinType
  {
    private final GWChainElementValue element;
    private final int depth;
    private GWChainGraphNodeType next;
    private final GWChainGraphNodeType previous;
    private final GWChainGraphBranchType branch;

    GWChainGraphJoin(
      final GWChainGraphNodeType inPrevious,
      final GWChainElementValue inElement,
      final int inDepth,
      final GWChainGraphBranchType inBranch)
    {
      this.previous =
        inPrevious;
      this.element =
        Objects.requireNonNull(inElement, "inElement");
      this.branch =
        Objects.requireNonNull(inBranch, "inBranch");
      this.depth =
        inDepth;
    }

    @Override
    public GWChainElementValue element()
    {
      return this.element;
    }

    @Override
    public Optional<GWChainGraphNodeType> next()
    {
      return Optional.ofNullable(this.next);
    }

    @Override
    public Optional<GWChainGraphNodeType> previous()
    {
      return Optional.ofNullable(this.previous);
    }

    @Override
    public int depth()
    {
      return this.depth;
    }

    @Override
    public GWChainGraphBranchType branch()
    {
      return this.branch;
    }

    @Override
    public String toString()
    {
      return "[GWChainGraphJoin %s [depth %d]]"
        .formatted(this.element, this.depth);
    }
  }

  private static final class GenerationState
  {
    private final ArrayDeque<GWChainGraphBranch> branchesOpen;
    private final LinkedList<GWChainElementValue> elementsRemaining;
    private GWChainGraphNodeType mostRecentNode;
    private GWChainGraphNodeType root;
    private int depthCurrent;

    private GenerationState(
      final List<GWChainElementValue> chain)
    {
      this.branchesOpen =
        new ArrayDeque<>(4);
      this.elementsRemaining =
        new LinkedList<>(chain);

      this.mostRecentNode = null;
      this.depthCurrent = 0;
    }

    public GWChainElementValue peekNext()
    {
      return this.elementsRemaining.peek();
    }

    public GWChainElementValue takeNext()
    {
      return this.elementsRemaining.poll();
    }

    public GWChainGraphNodeType beginBranch(
      final GWChainGraphNodeType previous,
      final GWChainElementValue element)
    {
      return switch (element) {
        case DIVIDER_1,
          DIVIDER_2,
          DIVIDER_3 -> {
          for (final var existing : this.branchesOpen) {
            if (existing.element() == element) {
              throw new GWChainGraphValidityException(
                "Branch '%s' is already open."
                  .formatted(element)
              );
            }
          }

          final var node =
            new GWChainGraphBranch(previous, element, this.depthCurrent);
          this.branchesOpen.push(node);
          this.updateMostRecentNext(node);
          yield node;
        }

        default -> {
          throw new GWChainGraphValidityException(
            "Cannot start a branch using element '%s'"
              .formatted(element)
          );
        }
      };
    }

    private void updateMostRecentNext(
      final GWChainGraphNodeType next)
    {
      if (this.root == null) {
        this.root = next;
        this.mostRecentNode = next;
        return;
      }

      final var recent = this.mostRecentNode;
      if (recent != null) {
        if (recent instanceof GWChainGraphJoin join) {
          join.next = next;
        } else if (recent instanceof GWChainGraphBranch branch) {
          branch.next = next;
        } else if (recent instanceof GWChainGraphNode node) {
          node.next = next;
        } else if (recent instanceof GWChainGraphBranchRightLeg leg) {
          leg.next = next;
        } else {
          throw new IllegalStateException();
        }
      }
      this.mostRecentNode = next;
    }

    public GWChainGraphNodeType closeBranch(
      final GWChainGraphNodeType previous,
      final GWChainElementValue element)
    {
      return switch (element) {
        case MIXER_1, MIXER_2, MIXER_3 -> {
          final var top = this.branchesOpen.peek();
          if (top == null) {
            throw new GWChainGraphValidityException(
              "Branch '%s' is not open."
                .formatted(element)
            );
          }

          if (top.closingElement() != element) {
            throw new GWChainGraphValidityException(
              "Cannot close branch '%s' with '%s' when '%s' is still open."
                .formatted(element, top.element, top.closingElement())
            );
          }

          --this.depthCurrent;
          final var node =
            new GWChainGraphJoin(previous, element, this.depthCurrent, top);

          this.branchesOpen.pop();
          this.updateMostRecentNext(node);
          yield node;
        }

        default -> {
          throw new GWChainGraphValidityException(
            "Cannot close a branch using element '%s'"
              .formatted(element)
          );
        }
      };
    }

    public GWChainGraphNodeType beginNode(
      final GWChainGraphNodeType previous,
      final GWChainElementValue element)
    {
      return switch (element) {
        case AIRD_PREAMP_1,
          AIRD_PREAMP_2,
          BYPASS_MAIN_L,
          BYPASS_MAIN_R,
          BYPASS_SUB_L,
          BYPASS_SUB_R,
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
          LOOPER,
          MAIN_OUT_L,
          MAIN_OUT_R,
          MAIN_SP_SIMULATOR_L,
          MAIN_SP_SIMULATOR_R,
          MASTER_DELAY,
          NOISE_SUPPRESSOR_1,
          NOISE_SUPPRESSOR_2,
          PEDAL_FX,
          RESERVED_44,
          REVERB,
          SEND_SLASH_RETURN_1,
          SEND_SLASH_RETURN_2,
          SUB_OUT_L,
          SUB_OUT_R,
          SUB_SP_SIMULATOR_L,
          SUB_SP_SIMULATOR_R -> {
          final var node =
            new GWChainGraphNode(previous, element, this.depthCurrent);
          this.updateMostRecentNext(node);
          yield node;
        }

        default -> {
          throw new GWChainGraphValidityException(
            "Cannot create a plain node using element '%s'"
              .formatted(element)
          );
        }
      };
    }

    public GWChainGraphNodeType beginBranchSplit(
      final GWChainGraphNodeType previous,
      final GWChainElementValue element)
    {
      return switch (element) {
        case BRANCH_SPLIT1, BRANCH_SPLIT2, BRANCH_SPLIT3 -> {
          final var top = this.branchesOpen.peek();
          if (top == null) {
            throw new GWChainGraphValidityException(
              "Branch '%s' is not open."
                .formatted(element)
            );
          }

          if (top.splittingElement() != element) {
            throw new GWChainGraphValidityException(
              "Cannot split branch '%s' with '%s' when '%s' is still open."
                .formatted(element, top.element, top.closingElement())
            );
          }

          ++this.depthCurrent;
          final var node =
            new GWChainGraphBranchRightLeg(
              previous,
              element,
              this.depthCurrent,
              top
            );

          top.right = node;
          this.updateMostRecentNext(node);
          yield node;
        }

        default -> {
          throw new GWChainGraphValidityException(
            "Cannot close a branch using element '%s'"
              .formatted(element)
          );
        }
      };
    }
  }

  /**
   * Create a chain graph from the list of elements.
   *
   * @param chain The elements
   *
   * @return A graph
   *
   * @throws GWChainGraphValidityException If the list of elements does not
   *                                       result in a valid graph
   */

  public static GWChainGraphType create(
    final List<GWChainElementValue> chain)
    throws GWChainGraphValidityException
  {
    return new GWChainGraph(createStep(new GenerationState(chain)));
  }

  private static GWChainGraphNodeType createStep(
    final GenerationState state)
    throws GWChainGraphValidityException
  {
    GWChainGraphNodeType previous = null;

    while (true) {
      final var current = state.peekNext();
      if (current == null) {
        break;
      }

      state.takeNext();

      previous = switch (current) {
        case AIRD_PREAMP_1,
          AIRD_PREAMP_2,
          BYPASS_MAIN_L,
          BYPASS_MAIN_R,
          BYPASS_SUB_L,
          BYPASS_SUB_R,
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
          LOOPER,
          MAIN_OUT_L,
          MAIN_OUT_R,
          MAIN_SP_SIMULATOR_L,
          MAIN_SP_SIMULATOR_R,
          MASTER_DELAY,
          NOISE_SUPPRESSOR_1,
          NOISE_SUPPRESSOR_2,
          PEDAL_FX,
          RESERVED_44,
          REVERB,
          SEND_SLASH_RETURN_1,
          SEND_SLASH_RETURN_2,
          SUB_OUT_L,
          SUB_OUT_R,
          SUB_SP_SIMULATOR_L,
          SUB_SP_SIMULATOR_R -> {
          yield state.beginNode(previous, current);
        }

        case DIVIDER_1, DIVIDER_2, DIVIDER_3 -> {
          yield state.beginBranch(previous, current);
        }

        case MIXER_1, MIXER_2, MIXER_3 -> {
          yield state.closeBranch(previous, current);
        }

        case BRANCH_SPLIT1,
          BRANCH_SPLIT2,
          BRANCH_SPLIT3 -> {
          yield state.beginBranchSplit(previous, current);
        }
      };
    }

    return state.root;
  }
}
