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

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.io7m.gatwick.controller.api.GWChainElementValue.AIRD_PREAMP_1;
import static com.io7m.gatwick.controller.api.GWChainElementValue.AIRD_PREAMP_2;
import static com.io7m.gatwick.controller.api.GWChainElementValue.BRANCH_SPLIT1;
import static com.io7m.gatwick.controller.api.GWChainElementValue.BRANCH_SPLIT2;
import static com.io7m.gatwick.controller.api.GWChainElementValue.BRANCH_SPLIT3;
import static com.io7m.gatwick.controller.api.GWChainElementValue.BYPASS_MAIN_L;
import static com.io7m.gatwick.controller.api.GWChainElementValue.BYPASS_MAIN_R;
import static com.io7m.gatwick.controller.api.GWChainElementValue.BYPASS_SUB_L;
import static com.io7m.gatwick.controller.api.GWChainElementValue.BYPASS_SUB_R;
import static com.io7m.gatwick.controller.api.GWChainElementValue.CHORUS;
import static com.io7m.gatwick.controller.api.GWChainElementValue.COMPRESSOR;
import static com.io7m.gatwick.controller.api.GWChainElementValue.DELAY_1;
import static com.io7m.gatwick.controller.api.GWChainElementValue.DELAY_2;
import static com.io7m.gatwick.controller.api.GWChainElementValue.DELAY_3;
import static com.io7m.gatwick.controller.api.GWChainElementValue.DELAY_4;
import static com.io7m.gatwick.controller.api.GWChainElementValue.DISTORTION_1;
import static com.io7m.gatwick.controller.api.GWChainElementValue.DISTORTION_2;
import static com.io7m.gatwick.controller.api.GWChainElementValue.DIVIDER_1;
import static com.io7m.gatwick.controller.api.GWChainElementValue.DIVIDER_2;
import static com.io7m.gatwick.controller.api.GWChainElementValue.DIVIDER_3;
import static com.io7m.gatwick.controller.api.GWChainElementValue.EQUALIZER_1;
import static com.io7m.gatwick.controller.api.GWChainElementValue.EQUALIZER_2;
import static com.io7m.gatwick.controller.api.GWChainElementValue.EQUALIZER_3;
import static com.io7m.gatwick.controller.api.GWChainElementValue.EQUALIZER_4;
import static com.io7m.gatwick.controller.api.GWChainElementValue.FOOT_VOLUME;
import static com.io7m.gatwick.controller.api.GWChainElementValue.FX_1;
import static com.io7m.gatwick.controller.api.GWChainElementValue.FX_2;
import static com.io7m.gatwick.controller.api.GWChainElementValue.FX_3;
import static com.io7m.gatwick.controller.api.GWChainElementValue.FX_4;
import static com.io7m.gatwick.controller.api.GWChainElementValue.LOOPER;
import static com.io7m.gatwick.controller.api.GWChainElementValue.MAIN_OUT_L;
import static com.io7m.gatwick.controller.api.GWChainElementValue.MAIN_OUT_R;
import static com.io7m.gatwick.controller.api.GWChainElementValue.MAIN_SP_SIMULATOR_L;
import static com.io7m.gatwick.controller.api.GWChainElementValue.MAIN_SP_SIMULATOR_R;
import static com.io7m.gatwick.controller.api.GWChainElementValue.MASTER_DELAY;
import static com.io7m.gatwick.controller.api.GWChainElementValue.MIXER_1;
import static com.io7m.gatwick.controller.api.GWChainElementValue.MIXER_2;
import static com.io7m.gatwick.controller.api.GWChainElementValue.MIXER_3;
import static com.io7m.gatwick.controller.api.GWChainElementValue.NOISE_SUPPRESSOR_1;
import static com.io7m.gatwick.controller.api.GWChainElementValue.NOISE_SUPPRESSOR_2;
import static com.io7m.gatwick.controller.api.GWChainElementValue.PEDAL_FX;
import static com.io7m.gatwick.controller.api.GWChainElementValue.RESERVED_44;
import static com.io7m.gatwick.controller.api.GWChainElementValue.REVERB;
import static com.io7m.gatwick.controller.api.GWChainElementValue.SEND_SLASH_RETURN_1;
import static com.io7m.gatwick.controller.api.GWChainElementValue.SEND_SLASH_RETURN_2;
import static com.io7m.gatwick.controller.api.GWChainElementValue.SUB_OUT_L;
import static com.io7m.gatwick.controller.api.GWChainElementValue.SUB_OUT_R;
import static com.io7m.gatwick.controller.api.GWChainElementValue.SUB_SP_SIMULATOR_L;
import static com.io7m.gatwick.controller.api.GWChainElementValue.SUB_SP_SIMULATOR_R;

/**
 * An effect chain. Chains are immutable.
 */

public final class GWChain
{
  private static final GWChain DEFAULT;
  private static final int CHAIN_SIZE = 49;

  static {
    DEFAULT = new GWChain(
      List.of(
        PEDAL_FX,
        COMPRESSOR,
        EQUALIZER_3,
        FX_1,
        DIVIDER_1,
        DISTORTION_1,
        SEND_SLASH_RETURN_1,
        NOISE_SUPPRESSOR_1,
        AIRD_PREAMP_1,
        NOISE_SUPPRESSOR_2,
        EQUALIZER_1,
        BRANCH_SPLIT1,
        DISTORTION_2,
        SEND_SLASH_RETURN_2,
        AIRD_PREAMP_2,
        EQUALIZER_2,
        MIXER_1,
        REVERB,
        FX_2,
        FX_3,
        EQUALIZER_4,
        FOOT_VOLUME,
        DELAY_1,
        DELAY_2,
        DELAY_3,
        DELAY_4,
        MASTER_DELAY,
        FX_4,
        CHORUS,
        LOOPER,
        DIVIDER_2,
        BRANCH_SPLIT2,
        MIXER_2,
        DIVIDER_3,
        BRANCH_SPLIT3,
        MIXER_3,
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
      )
    );
  }

  private final GWChainGraphType graph;
  private final List<GWChainElementValue> chain;

  private GWChain(
    final List<GWChainElementValue> inChain)
  {
    this.chain = List.copyOf(
      Objects.requireNonNull(inChain, "chain")
    );

    if (this.chain.size() != CHAIN_SIZE) {
      throw new GWChainGraphValidityException(
        "Chain must have 49 elements."
      );
    }

    final var noDup =
      this.chain.stream()
        .distinct()
        .count();

    if (noDup != 49L) {
      throw new GWChainGraphValidityException(
        "Chain must have 49 distinct elements."
      );
    }

    /*
     * Build graph for validity checking.
     */

    this.graph =
      GWChainGraph.create(inChain);
  }

  /**
   * @return The required size of an effects chain
   */

  public static int chainSize()
  {
    return CHAIN_SIZE;
  }

  /**
   * @return The default chain
   */

  public static GWChain defaultChain()
  {
    return DEFAULT;
  }

  /**
   * Create an effect chain from the given list of elements. Every value of
   * {@link GWChainElementValue} must be present in the list exactly once.
   *
   * @param inChain The elements
   *
   * @return The chain
   */

  public static GWChain of(
    final List<GWChainElementValue> inChain)
  {
    return new GWChain(inChain);
  }

  /**
   * Move the given element {@code toMove} before the element
   * {@code destination}.
   *
   * @param toMove      The element to move
   * @param destination The destination
   *
   * @return A new chain with the elements moved
   */

  public GWChain moveBefore(
    final GWChainElementValue toMove,
    final GWChainElementValue destination)
  {
    Objects.requireNonNull(toMove, "toMove");
    Objects.requireNonNull(destination, "destination");

    if (toMove == destination) {
      return this;
    }

    final var newChain = new LinkedList<>(this.chain);
    newChain.remove(toMove);
    final var index = newChain.indexOf(destination);
    newChain.add(index, toMove);
    return new GWChain(newChain);
  }

  /**
   * @return This chain as a graph
   */

  public GWChainGraphType graph()
  {
    return this.graph;
  }

  /**
   * @return The chain elements
   */

  public List<GWChainElementValue> elements()
  {
    return this.chain;
  }
}
