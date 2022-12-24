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

import com.io7m.gatwick.gui.internal.icons.GWIconServiceType;
import com.io7m.taskrecorder.core.TRFailed;
import com.io7m.taskrecorder.core.TRStep;
import com.io7m.taskrecorder.core.TRStepType;
import com.io7m.taskrecorder.core.TRTask;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * A controller for a cell within an error tree view.
 */

public final class GWErrorTreeCellController implements Initializable
{
  private final GWIconServiceType icons;

  @FXML private ImageView stepIcon;
  @FXML private Label stepTitle;
  @FXML private Label stepResolution;
  @FXML private TextArea stepException;

  /**
   * A controller for a cell within an error tree view.
   *
   * @param inIcons The icon service
   */

  public GWErrorTreeCellController(
    final GWIconServiceType inIcons)
  {
    this.icons =
      Objects.requireNonNull(inIcons, "icons");
  }

  /**
   * Set the task step.
   *
   * @param item The task step
   */

  public void setItem(
    final TRStepType item)
  {
    if (item.resolution() instanceof TRFailed) {
      this.stepIcon.setImage(this.icons.error16());
    } else {
      this.stepIcon.setImage(this.icons.task16());
    }

    if (item instanceof TRTask<?> task) {
      this.stepTitle.setText(item.name());
      this.stepResolution.setVisible(false);
      this.stepException.setVisible(false);
      return;
    }

    if (item instanceof TRStep step) {
      this.stepIcon.setVisible(true);
      this.stepTitle.setText(item.name());

      final var resolution = step.resolution().message();
      if (resolution.isEmpty()) {
        this.stepResolution.setVisible(false);
      } else {
        this.stepResolution.setVisible(true);
        this.stepResolution.setText(resolution);
      }

      if (step.resolution() instanceof TRFailed failed) {
        final var exceptionOpt = failed.exception();
        if (exceptionOpt.isPresent()) {
          final var bytes = new ByteArrayOutputStream();
          try (var stream = new PrintStream(bytes, true, UTF_8)) {
            exceptionOpt.get().printStackTrace(stream);
          }
          this.stepException.setVisible(true);
          this.stepException.setText(bytes.toString(UTF_8));
        } else {
          this.stepException.setVisible(false);
        }
      } else {
        this.stepException.setVisible(false);
      }
      return;
    }
  }

  @Override
  public void initialize(
    final URL location,
    final ResourceBundle resources)
  {
    this.stepException.managedProperty()
      .bind(this.stepException.visibleProperty());
    this.stepResolution.managedProperty()
      .bind(this.stepResolution.visibleProperty());
  }
}
