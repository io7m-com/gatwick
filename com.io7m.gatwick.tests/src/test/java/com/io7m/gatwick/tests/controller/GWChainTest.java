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
import com.io7m.gatwick.controller.api.GWChainElementValue;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class GWChainTest
{
  @Property
  public void testMoveSelf(
    final @ForAll GWChainElementValue element)
  {
    final var elements =
      new ArrayList<>(List.of(GWChainElementValue.values()));

    Collections.shuffle(elements);

    final var chain0 =
      GWChain.of(elements);
    final var chain1 =
      chain0.moveBefore(element, element);

    assertEquals(chain0, chain1);
  }

  @Property
  public void testMoveFirst(
    final @ForAll GWChainElementValue element)
  {
    final var elements =
      new ArrayList<>(List.of(GWChainElementValue.values()));

    Collections.shuffle(elements);

    final var chain0 =
      GWChain.of(elements);
    final var chain1 =
      chain0.moveBefore(element, elements.get(0));

    assertEquals(element, chain1.elements().get(0));
  }

  @Property
  public void testTooShort()
  {
    final var elements =
      new ArrayList<>(List.of(GWChainElementValue.values()));

    Collections.shuffle(elements);
    elements.remove(0);

    assertThrows(IllegalArgumentException.class, () -> {
      GWChain.of(elements);
    });
  }

  @Property
  public void testDuplicates(
    final @ForAll GWChainElementValue element)
  {
    final var elements = new ArrayList<GWChainElementValue>();
    for (int index = 0; index < 49; ++index) {
      elements.add(element);
    }

    assertThrows(IllegalArgumentException.class, () -> {
      GWChain.of(elements);
    });
  }
}
