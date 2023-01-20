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

import com.io7m.gatwick.gui.internal.icons.GWIconEnumerationSetType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.util.Objects;
import java.util.function.Function;

/**
 * A factory of list cells.
 *
 * @param <S> The enumeration type
 */

public final class GWIconListCellFactory<S extends Enum<S>>
  implements Callback<ListView<S>, ListCell<S>>
{
  private final GWIconEnumerationSetType<S> icons;
  private final Function<S, String> labels;

  /**
   * A factory of list cells.
   *
   * @param inIcons  The icon set
   * @param inLabels A function from enumerations to text labels
   */

  public GWIconListCellFactory(
    final GWIconEnumerationSetType<S> inIcons,
    final Function<S, String> inLabels)
  {
    this.icons =
      Objects.requireNonNull(inIcons, "icons");
    this.labels =
      Objects.requireNonNull(inLabels, "inLabels");
  }

  @Override
  public ListCell<S> call(
    final ListView<S> param)
  {
    return new GWIconListCell<>(this.icons, this.labels);
  }
}
