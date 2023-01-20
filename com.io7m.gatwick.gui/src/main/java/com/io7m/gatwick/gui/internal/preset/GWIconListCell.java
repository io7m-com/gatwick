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
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.Objects;
import java.util.function.Function;

/**
 * A list cell with an icon.
 *
 * @param <S> The enumeration type
 */

public final class GWIconListCell<S extends Enum<S>>
  extends ListCell<S>
{
  private static final double HEIGHT = 40.0;
  private final HBox root;
  private final GWIconEnumerationSetType<S> icons;
  private final Function<S, String> labels;

  @FXML private Label itemText;
  @FXML private ImageView itemIcon;

  /**
   * A list cell with an icon.
   *
   * @param inIcons  The icon set
   * @param inLabels A function from enumerations to text labels
   */

  public GWIconListCell(
    final GWIconEnumerationSetType<S> inIcons,
    final Function<S, String> inLabels)
  {
    this.icons =
      Objects.requireNonNull(inIcons, "icons");
    this.labels =
      Objects.requireNonNull(inLabels, "inLabels");

    this.root = new HBox();
    this.root.setPrefHeight(HEIGHT);
    this.root.setMinHeight(HEIGHT);
    this.root.setMaxHeight(HEIGHT);
    this.root.setAlignment(Pos.CENTER_LEFT);

    this.itemIcon = new ImageView();
    this.itemText = new Label();

    HBox.setMargin(this.itemText, new Insets(0.0, 0.0, 0.0, 8.0));

    this.root.getChildren().addAll(this.itemIcon, this.itemText);
  }

  @Override
  protected void updateItem(
    final S item,
    final boolean empty)
  {
    super.updateItem(item, empty);

    if (empty || item == null) {
      this.setGraphic(null);
    } else {
      this.setGraphic(this.root);
      this.itemIcon.setImage(this.icons.iconFor(item).orElse(null));
      this.itemText.setText(this.labels.apply(item));
    }
  }
}
