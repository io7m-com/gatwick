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

import java.util.ArrayDeque;

final class GWXOffsetStack
{
  private final ArrayDeque<MutableOffsetX> stack;

  GWXOffsetStack()
  {
    this.stack = new ArrayDeque<>();
    this.stack.push(new MutableOffsetX());
  }

  public void set(
    final double x)
  {
    final var offsetX = this.stack.peek();
    offsetX.value = x;
  }

  public double peek()
  {
    final var offsetX = this.stack.peek();
    return offsetX.value;
  }

  public void push()
  {
    final var offsetXThen = this.stack.peek();
    final var offsetXNew = new MutableOffsetX();
    offsetXNew.value = offsetXThen.value;
    this.stack.push(offsetXNew);
  }

  public void pop()
  {
    this.stack.pop();
  }

  @Override
  public String toString()
  {
    final var builder = new StringBuilder();
    builder.append("[GWXOffsetStack ");
    for (final var offset : this.stack) {
      builder.append(offset.value);
      builder.append(" ");
    }
    builder.append("]");
    return builder.toString();
  }

  private static final class MutableOffsetX
  {
    private double value;

    MutableOffsetX()
    {

    }
  }
}
