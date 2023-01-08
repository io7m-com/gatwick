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
import javafx.scene.shape.Circle;

final class GWNodeShapeTerminator
  extends GWNodeShape
{
  private final Circle circle;
  private final Circle circleSelect;

  @Override
  public double centerX()
  {
    var cx = this.getLayoutX();
    cx += this.circle.getCenterX();
    return cx;
  }

  @Override
  public double centerY()
  {
    var cy = this.getLayoutY();
    cy += this.circle.getCenterY();
    return cy;
  }

  GWNodeShapeTerminator(
    final SimpleObjectProperty<GWNodeShape> inSelected,
    final GWChainElementValue inName)
  {
    super(inSelected, loadIcon(), inName);

    final var nodeSize = 8;
    final var nodePaddedSize = nodeSize + 32.0 + 32.0;

    this.setWidth(nodePaddedSize);
    this.setMinWidth(nodePaddedSize);
    this.setMaxWidth(nodePaddedSize);

    this.setHeight(nodePaddedSize);
    this.setMinHeight(nodePaddedSize);
    this.setMaxHeight(nodePaddedSize);

    this.setPrefSize(nodePaddedSize, nodePaddedSize);

    this.circleSelect = new Circle(nodeSize / 2.0);
    this.circleSelect.setCenterX(32.0);
    this.circleSelect.setCenterY(32.0);
    this.circleSelect.setStroke(NODE_SELECTION_COLOR);
    this.circleSelect.setStrokeWidth(3.0);
    this.circleSelect.setFill(null);
    this.circleSelect.setFocusTraversable(false);
    this.circleSelect.setMouseTransparent(true);

    this.circle = new Circle(nodeSize / 2.0);
    this.circle.setCenterX(32.0);
    this.circle.setCenterY(32.0);
    this.circle.setStroke(Color.WHITE);
    this.circle.setFill(Color.DIMGRAY);

    this.getChildren()
      .addAll(this.circleSelect, this.circle);
  }

  private static Image loadIcon()
  {
    return new Image(
      GWNodeShapeBypass.class.getResource(
          "/com/io7m/gatwick/gui/internal/terminator24.png")
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
    return this.circleSelect;
  }

  @Override
  public Color mainColor()
  {
    return Color.DIMGRAY;
  }

  @Override
  public Node clickable()
  {
    return this.circle;
  }
}
