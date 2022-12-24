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

package com.io7m.gatwick.gui.internal.errors;

import com.io7m.gatwick.gui.internal.GWScreenControllerType;
import com.io7m.gatwick.gui.internal.GWStrings;
import com.io7m.gatwick.gui.internal.icons.GWIconServiceType;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import com.io7m.taskrecorder.core.TRStep;
import com.io7m.taskrecorder.core.TRStepType;
import com.io7m.taskrecorder.core.TRTask;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * A controller for the error screen.
 */

public final class GWErrorController implements GWScreenControllerType
{
  private final TRTask<?> task;
  private final Stage stage;
  private final GWStrings strings;
  private final GWIconServiceType icons;

  @FXML private ImageView errorIcon;
  @FXML private Label errorTaskTitle;
  @FXML private Label errorTaskMessage;
  @FXML private TreeView<TRStepType> errorDetails;

  /**
   * A controller for the error screen.
   *
   * @param inServices The service directory
   * @param inTask     The failed task
   * @param inStage    The containing window
   */

  public GWErrorController(
    final RPServiceDirectoryType inServices,
    final TRTask<?> inTask,
    final Stage inStage)
  {
    final RPServiceDirectoryType services =
      Objects.requireNonNull(inServices, "inServices");

    this.strings =
      inServices.requireService(GWStrings.class);
    this.icons =
      inServices.requireService(GWIconServiceType.class);
    this.task =
      Objects.requireNonNull(inTask, "task");
    this.stage =
      Objects.requireNonNull(inStage, "stage");
  }

  private static TreeItem<TRStepType> buildTree(
    final TRStepType node)
  {
    if (node instanceof TRStep step) {
      return new TreeItem<>(step);
    }

    if (node instanceof TRTask<?> task) {
      final var taskNode = new TreeItem<TRStepType>(task);
      for (final var step : task.steps()) {
        taskNode.getChildren().add(buildTree(step));
      }
      return taskNode;
    }

    throw new IllegalStateException();
  }

  @Override
  public void initialize(
    final URL location,
    final ResourceBundle resources)
  {
    this.errorIcon.setImage(this.icons.error16());

    this.errorTaskTitle.setText(this.task.name());
    this.errorTaskMessage.setText(this.task.resolution().message());

    this.errorDetails.setCellFactory(param -> {
      return new GWErrorTreeCell(this.strings, this.icons);
    });
    this.errorDetails.setRoot(buildTree(this.task));
    this.errorDetails.setShowRoot(false);
  }

  @FXML
  private void onDismissSelected()
  {
    this.stage.close();
  }

  @FXML
  private void onReportSelected()
  {

  }
}
