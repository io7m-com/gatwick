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
import com.io7m.gatwick.controller.api.GWChainGraph;
import com.io7m.gatwick.controller.api.GWChainGraphType;
import com.io7m.gatwick.controller.api.GWChainGraphValidityException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.io7m.gatwick.controller.api.GWChainElementValue.BRANCH_SPLIT1;
import static com.io7m.gatwick.controller.api.GWChainElementValue.BRANCH_SPLIT2;
import static com.io7m.gatwick.controller.api.GWChainElementValue.BRANCH_SPLIT3;
import static com.io7m.gatwick.controller.api.GWChainElementValue.DIVIDER_1;
import static com.io7m.gatwick.controller.api.GWChainElementValue.DIVIDER_2;
import static com.io7m.gatwick.controller.api.GWChainElementValue.DIVIDER_3;
import static com.io7m.gatwick.controller.api.GWChainElementValue.MIXER_1;
import static com.io7m.gatwick.controller.api.GWChainElementValue.MIXER_2;
import static com.io7m.gatwick.controller.api.GWChainElementValue.MIXER_3;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class GWChainGraphTest
{
  @Test
  public void testDefaultGraph()
  {
    final var g =
      GWChain.defaultChain().elements();
    final var t =
      GWChainGraph.create(g);
    dumpGraph(t);
  }

  private static void dumpGraph(
    final GWChainGraphType t)
  {
    t.elements().forEach(System.out::println);
  }

  @Test
  public void testBranch1MixerNotOpen()
  {
    assertThrows(GWChainGraphValidityException.class, () -> {
      GWChainGraph.create(
        List.of(MIXER_1)
      );
    });
  }

  @Test
  public void testBranch2MixerNotOpen()
  {
    assertThrows(GWChainGraphValidityException.class, () -> {
      GWChainGraph.create(
        List.of(MIXER_2)
      );
    });
  }

  @Test
  public void testBranch3MixerNotOpen()
  {
    assertThrows(GWChainGraphValidityException.class, () -> {
      GWChainGraph.create(
        List.of(MIXER_3)
      );
    });
  }

  @Test
  public void testBranch1SplitNotOpen()
  {
    assertThrows(GWChainGraphValidityException.class, () -> {
      GWChainGraph.create(
        List.of(BRANCH_SPLIT1)
      );
    });
  }

  @Test
  public void testBranch2SplitNotOpen()
  {
    assertThrows(GWChainGraphValidityException.class, () -> {
      GWChainGraph.create(
        List.of(BRANCH_SPLIT2)
      );
    });
  }

  @Test
  public void testBranch3SplitNotOpen()
  {
    assertThrows(GWChainGraphValidityException.class, () -> {
      GWChainGraph.create(
        List.of(BRANCH_SPLIT3)
      );
    });
  }

  @Test
  public void testBranch1MixerAlreadyOpen()
  {
    assertThrows(GWChainGraphValidityException.class, () -> {
      GWChainGraph.create(
        List.of(DIVIDER_1, DIVIDER_1)
      );
    });
  }

  @Test
  public void testBranch2MixerAlreadyOpen()
  {
    assertThrows(GWChainGraphValidityException.class, () -> {
      GWChainGraph.create(
        List.of(DIVIDER_2, DIVIDER_2)
      );
    });
  }

  @Test
  public void testBranch3MixerAlreadyOpen()
  {
    assertThrows(GWChainGraphValidityException.class, () -> {
      GWChainGraph.create(
        List.of(DIVIDER_3, DIVIDER_3)
      );
    });
  }

  @Test
  public void testBranch1Split2Wrong()
  {
    assertThrows(GWChainGraphValidityException.class, () -> {
      GWChainGraph.create(
        List.of(DIVIDER_1, BRANCH_SPLIT2)
      );
    });
  }

  @Test
  public void testBranch1Split3Wrong()
  {
    assertThrows(GWChainGraphValidityException.class, () -> {
      GWChainGraph.create(
        List.of(DIVIDER_1, BRANCH_SPLIT3)
      );
    });
  }
}
