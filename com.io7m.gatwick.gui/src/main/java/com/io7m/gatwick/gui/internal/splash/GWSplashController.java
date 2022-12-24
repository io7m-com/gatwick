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


package com.io7m.gatwick.gui.internal.splash;

import com.io7m.gatwick.gui.internal.GWBootCompleted;
import com.io7m.gatwick.gui.internal.GWScreenControllerType;
import com.io7m.gatwick.gui.internal.exec.GWBackgroundExecutorType;
import com.io7m.repetoir.core.RPServiceDirectoryWritableType;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import static javafx.animation.Interpolator.LINEAR;

/**
 * The splash screen.
 */

public final class GWSplashController implements GWScreenControllerType
{
  private final RPServiceDirectoryWritableType services;
  private final GWBackgroundExecutorType executor;

  @FXML private ImageView splashLogo;
  @FXML private Pane splashPane;
  @FXML private Label splashText;
  @FXML private ProgressBar splashProgress;

  /**
   * The splash screen.
   *
   * @param inServices The service directory
   */

  public GWSplashController(
    final RPServiceDirectoryWritableType inServices)
  {
    this.services =
      Objects.requireNonNull(inServices, "services");
    this.executor =
      inServices.requireService(GWBackgroundExecutorType.class);
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.splashText.setText("Loading...");

    final var neon = new FadeTransition(Duration.millis(250L));
    neon.setNode(this.splashLogo);
    neon.setFromValue(1.0);
    neon.setToValue(0.9);
    neon.setInterpolator(LINEAR);
    neon.setCycleCount(Animation.INDEFINITE);
    neon.playFromStart();

    this.executor.executor()
      .schedule(() -> {
        this.services.register(GWBootCompleted.class, new GWBootCompleted());
      }, 3L, TimeUnit.SECONDS);
  }
}
