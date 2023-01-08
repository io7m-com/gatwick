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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

final class GWNodeShapeMixer
  extends GWNodeShape
{
  private static final Color NODE_FILL =
    Color.PALEVIOLETRED.desaturate().darker();
  private static final Color NODE_FILL_HOVER =
    NODE_FILL.deriveColor(
      0.0,
      1.0,
      HOVER_BRIGHTNESS_FACTOR,
      1.0
    );

  private final Circle circle;
  private final Label labelShadow;
  private final Label labelBright;
  private final Circle circleOuter;
  private final Circle circleSelect;

  GWNodeShapeMixer(
    final SimpleObjectProperty<GWNodeShape> inSelected,
    final GWChainElementValue inName)
  {
    super(inSelected, loadIcon(), inName);

    final var nodeSize = 32.0;
    final var nodePaddedSize = nodeSize + 8.0 + 8.0;

    this.setWidth(nodePaddedSize);
    this.setMinWidth(nodePaddedSize);
    this.setMaxWidth(nodePaddedSize);

    this.setHeight(nodePaddedSize);
    this.setMinHeight(nodePaddedSize);
    this.setMaxHeight(nodePaddedSize);

    this.setPrefSize(nodePaddedSize, nodePaddedSize);

    this.circleSelect =
      createCircleSelect(nodeSize);
    this.circleOuter =
      createCircleOuter(nodeSize);
    this.circle =
      createCircle(nodeSize);
    this.labelShadow =
      createLabelShadow(inName, nodeSize);
    this.labelBright =
      createLabelBright(inName, nodeSize);

    this.getChildren()
      .addAll(
        this.circleSelect,
        this.circleOuter,
        this.circle,
        this.labelShadow,
        this.labelBright);

    this.clickable().setOnMouseClicked(event -> this.selected().set(this));
  }

  private static Circle createCircleSelect(final double nodeSize)
  {
    final var circleSelect = new Circle(nodeSize / 1.5);
    circleSelect.setCenterX(32.0);
    circleSelect.setCenterY(32.0);
    circleSelect.setStroke(NODE_SELECTION_COLOR);
    circleSelect.setStrokeWidth(NODE_SELECTION_WEIGHT);
    circleSelect.setFill(null);
    circleSelect.setFocusTraversable(false);
    circleSelect.setMouseTransparent(true);
    return circleSelect;
  }

  private static Circle createCircleOuter(final double nodeSize)
  {
    final var circleOuter = new Circle(nodeSize / 1.5);
    circleOuter.setCenterX(32.0);
    circleOuter.setCenterY(32.0);
    circleOuter.setStroke(Color.WHITE);
    circleOuter.setStrokeWidth(3.0);
    circleOuter.setFill(null);
    circleOuter.setFocusTraversable(false);
    circleOuter.setMouseTransparent(true);
    return circleOuter;
  }

  private static Circle createCircle(final double nodeSize)
  {
    final var circle = new Circle(nodeSize / 1.5);
    circle.setCenterX(32.0);
    circle.setCenterY(32.0);
    circle.setStroke(Color.BLACK);
    circle.setFill(NODE_FILL);
    circle.setOnMouseEntered(e -> {
      circle.setFill(NODE_FILL_HOVER);
    });
    circle.setOnMouseExited(event -> {
      circle.setFill(NODE_FILL);
    });
    return circle;
  }

  private static Label createLabelShadow(
    final GWChainElementValue inName,
    final double nodeSize)
  {
    final var labelShadow = new Label();
    labelShadow.setPrefSize(nodeSize, nodeSize);
    labelShadow.setLayoutX(15.0);
    labelShadow.setLayoutY(15.0);
    labelShadow.setFont(NODE_LABEL_SHADOW_FONT);
    labelShadow.setTextFill(Color.BLACK);
    labelShadow.setAlignment(Pos.CENTER);
    labelShadow.setText(nodeLabel(inName));
    labelShadow.setFocusTraversable(false);
    labelShadow.setMouseTransparent(true);
    return labelShadow;
  }

  private static Label createLabelBright(
    final GWChainElementValue inName,
    final double nodeSize)
  {
    final var labelBright = new Label();
    labelBright.setPrefSize(nodeSize, nodeSize);
    labelBright.setLayoutX(16.0);
    labelBright.setLayoutY(16.0);
    labelBright.setFont(NODE_LABEL_FONT);
    labelBright.setTextFill(Color.WHITE);
    labelBright.setAlignment(Pos.CENTER);
    labelBright.setText(nodeLabel(inName));
    labelBright.setFocusTraversable(false);
    labelBright.setMouseTransparent(true);
    return labelBright;
  }

  private static Image loadIcon()
  {
    return new Image(
      GWNodeShapeBypass.class.getResource(
          "/com/io7m/gatwick/gui/internal/mixer24.png")
        .toString(),
      24.0,
      24.0,
      true,
      false,
      true
    );
  }

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

  @Override
  protected Node highlightable()
  {
    return this.circleSelect;
  }

  @Override
  public Color mainColor()
  {
    return NODE_FILL;
  }

  @Override
  public Node clickable()
  {
    return this.circle;
  }
}
