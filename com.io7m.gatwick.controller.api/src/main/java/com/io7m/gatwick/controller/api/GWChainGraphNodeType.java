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

import java.util.Optional;

/**
 * The type of nodes in a chain graph.
 */

public sealed interface GWChainGraphNodeType
{
  /**
   * @return The chain element associated with this node
   */

  GWChainElementValue element();

  /**
   * @return The next node in the graph, if any
   */

  Optional<GWChainGraphNodeType> next();

  /**
   * @return The previous node in the graph, if any
   */

  Optional<GWChainGraphNodeType> previous();

  /**
   * @return The branch depth of the node in the graph
   */

  int depth();

  /**
   * The type of nodes that represent branches (dividers).
   */

  non-sealed interface GWChainGraphBranchType
    extends GWChainGraphNodeType
  {
    /**
     * @return The left leg of the branch
     */

    GWChainGraphNodeType left();

    /**
     * @return The right leg of the branch
     */

    GWChainGraphBranchRightLegType right();

    /**
     * @return The last node of the left branch
     */

    GWChainGraphNodeType endOfLeftBranch();

    /**
     * @return The last node of the right branch
     */

    GWChainGraphNodeType endOfRightBranch();

    /**
     * @return The number of nodes in the left leg (that aren't the splitter node)
     */

    int leftLength();

    /**
     * @return The number of nodes in the right leg (that aren't the mixer node)
     */

    int rightLength();
  }

  /**
   * The type of nodes that represent the start of the right leg of a branch (divider).
   */

  non-sealed interface GWChainGraphBranchRightLegType
    extends GWChainGraphNodeType
  {
    /**
     * @return The branch that owns this leg
     */

    GWChainGraphBranchType branch();
  }

  /**
   * The type of nodes that represent the two legs of a branch joining together (mixer).
   */

  non-sealed interface GWChainGraphJoinType
    extends GWChainGraphNodeType
  {
    /**
     * @return The branch that owns this join
     */

    GWChainGraphBranchType branch();
  }

  /**
   * The type of nodes that represent simple effects blocks.
   */

  non-sealed interface GWChainGraphBlockType
    extends GWChainGraphNodeType
  {

  }
}
