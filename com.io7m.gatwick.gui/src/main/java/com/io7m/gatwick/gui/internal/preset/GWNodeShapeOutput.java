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
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

final class GWNodeShapeOutput
  extends GWNodeShape
{
  private static final Color NODE_FILL =
    Color.DIMGRAY;
  private static final Color NODE_FILL_HOVER =
    NODE_FILL.deriveColor(
      0.0,
      1.0,
      HOVER_BRIGHTNESS_FACTOR,
      1.0
    );

  private final Rectangle rectangle;
  private final Label labelShadow;
  private final Label labelBright;
  private final ImageView imageView;
  private final Rectangle rectangleOuter;
  private final Rectangle rectangleSelect;

  GWNodeShapeOutput(
    final SimpleObjectProperty<GWNodeShape> inSelected,
    final GWChainElementValue inName)
  {
    super(inSelected, loadIcon(), inName);

    final var nodeShapeWidth = 128.0;
    final var nodeShapeHeight = 32.0;
    final var nodePaddedWidth = nodeShapeWidth + 8.0 + 8.0;

    this.setWidth(nodePaddedWidth);
    this.setMinWidth(nodePaddedWidth);
    this.setMaxWidth(nodePaddedWidth);

    final var nodePaddedHeight = nodeShapeHeight + 8.0 + 8.0;
    this.setHeight(nodePaddedHeight);
    this.setMinHeight(nodePaddedHeight);
    this.setMaxHeight(nodePaddedHeight);

    this.setPrefSize(nodePaddedWidth, nodePaddedHeight);

    this.rectangleSelect =
      createRectangleSelect(nodeShapeWidth, nodeShapeHeight);
    this.rectangleOuter =
      createRectangleOuter(nodeShapeWidth, nodeShapeHeight);
    this.rectangle =
      createRectangle(nodeShapeWidth, nodeShapeHeight);
    this.imageView =
      createImageView(this.icon());
    this.labelShadow =
      createLabelShadow(inName, nodeShapeWidth, nodeShapeHeight);
    this.labelBright =
      createLabelBright(inName, nodeShapeWidth, nodeShapeHeight);

    this.getChildren()
      .addAll(
        this.rectangleSelect,
        this.rectangleOuter,
        this.rectangle,
        this.imageView,
        this.labelShadow,
        this.labelBright
      );

    this.clickable().setOnMouseClicked(event -> this.selected().set(this));
  }

  private static Rectangle createRectangleSelect(
    final double nodeShapeWidth,
    final double nodeShapeHeight)
  {
    final var rectangleSelect = new Rectangle(
      nodeShapeWidth,
      nodeShapeHeight);
    rectangleSelect.setX(16.5);
    rectangleSelect.setY(16.5);
    rectangleSelect.setStroke(NODE_SELECTION_COLOR);
    rectangleSelect.setStrokeWidth(NODE_SELECTION_WEIGHT);
    rectangleSelect.setFill(null);
    rectangleSelect.setFocusTraversable(false);
    rectangleSelect.setMouseTransparent(false);
    return rectangleSelect;
  }

  private static Rectangle createRectangleOuter(
    final double nodeShapeWidth,
    final double nodeShapeHeight)
  {
    final var rectangleOuter = new Rectangle(
      nodeShapeWidth + 2,
      nodeShapeHeight + 2);
    rectangleOuter.setX(15.5);
    rectangleOuter.setY(15.5);
    rectangleOuter.setStroke(Color.WHITE);
    rectangleOuter.setFill(null);
    rectangleOuter.setFocusTraversable(false);
    rectangleOuter.setMouseTransparent(true);
    return rectangleOuter;
  }

  private static Rectangle createRectangle(
    final double nodeShapeWidth,
    final double nodeShapeHeight)
  {
    final var rectangle = new Rectangle(nodeShapeWidth, nodeShapeHeight);
    rectangle.setX(16.5);
    rectangle.setY(16.5);
    rectangle.setStroke(Color.BLACK);
    rectangle.setFill(NODE_FILL);
    rectangle.setOnMouseEntered(e -> {
      rectangle.setFill(NODE_FILL_HOVER);
    });
    rectangle.setOnMouseExited(event -> {
      rectangle.setFill(NODE_FILL);
    });
    return rectangle;
  }

  private static ImageView createImageView(final Image icon)
  {
    final var imageView = new ImageView(icon);
    imageView.setX(16.0 + 4.0);
    imageView.setY(16.0 + 4.0);
    imageView.setFocusTraversable(false);
    imageView.setMouseTransparent(true);
    return imageView;
  }

  private static Label createLabelShadow(
    final GWChainElementValue inName,
    final double nodeShapeWidth,
    final double nodeShapeHeight)
  {
    final var labelShadow = new Label();
    labelShadow.setPrefSize(nodeShapeWidth - 8.0, nodeShapeHeight);
    labelShadow.setLayoutX(15.0);
    labelShadow.setLayoutY(15.0);
    labelShadow.setFont(NODE_LABEL_SHADOW_FONT);
    labelShadow.setTextFill(Color.BLACK);
    labelShadow.setAlignment(Pos.CENTER_RIGHT);
    labelShadow.setText(nodeLabel(inName));
    labelShadow.setFocusTraversable(false);
    labelShadow.setMouseTransparent(true);
    return labelShadow;
  }

  private static Label createLabelBright(
    final GWChainElementValue inName,
    final double nodeShapeWidth,
    final double nodeShapeHeight)
  {
    final var labelBright = new Label();
    labelBright.setPrefSize(nodeShapeWidth - 8.0, nodeShapeHeight);
    labelBright.setLayoutX(16.0);
    labelBright.setLayoutY(16.0);
    labelBright.setFont(NODE_LABEL_FONT);
    labelBright.setTextFill(Color.WHITE);
    labelBright.setAlignment(Pos.CENTER_RIGHT);
    labelBright.setText(nodeLabel(inName));
    labelBright.setFocusTraversable(false);
    labelBright.setMouseTransparent(true);
    return labelBright;
  }

  private static Image loadIcon()
  {
    return new Image(
      GWNodeShapeOutput.class.getResource(
          "/com/io7m/gatwick/gui/internal/output24.png")
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
    cx += this.rectangle.getX();
    cx += (this.rectangle.getWidth() / 2.0);
    return cx;
  }

  @Override
  public double centerY()
  {
    var cy = this.getLayoutY();
    cy += this.rectangle.getY();
    cy += (this.rectangle.getHeight() / 2.0);
    return cy;
  }

  @Override
  protected Node highlightable()
  {
    return this.rectangleSelect;
  }

  @Override
  public Color mainColor()
  {
    return NODE_FILL;
  }

  @Override
  public Node clickable()
  {
    return this.rectangle;
  }
}
