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
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

final class GWNodeShapeNull
  extends GWNodeShape
{
  @Override
  public double centerX()
  {
    return this.getLayoutX();
  }

  @Override
  public double centerY()
  {
    return this.getLayoutY();
  }

  GWNodeShapeNull(
    final SimpleObjectProperty<GWNodeShape> inSelected,
    final GWChainElementValue inName)
  {
    super(inSelected, loadIcon(), inName);

    this.setWidth(0.0);
    this.setMinWidth(0.0);
    this.setMaxWidth(0.0);

    this.setHeight(0.0);
    this.setMinHeight(0.0);
    this.setMaxHeight(0.0);

    this.clickable().setOnMouseClicked(event -> this.selected().set(this));
  }

  private static Image loadIcon()
  {
    return new Image(
      GWNodeShapeBypass.class.getResource(
          "/com/io7m/gatwick/gui/internal/null.png")
        .toString(),
      24.0,
      24.0,
      true,
      false,
      true
    );
  }

  @Override
  protected Node highlightable()
  {
    return this;
  }

  @Override
  public Color mainColor()
  {
    return Color.BLACK;
  }

  @Override
  public Node clickable()
  {
    return this;
  }
}
