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
import com.io7m.gatwick.gui.internal.config.GWConfigurationServiceType;
import com.io7m.gatwick.gui.internal.debug.GWDebugService;
import com.io7m.gatwick.gui.internal.exec.GWBackgroundExecutorType;
import com.io7m.gatwick.gui.internal.icons.GWIconSetService;
import com.io7m.gatwick.gui.internal.icons.GWIconSetServiceType;
import com.io7m.gatwick.preferences.GWPreferencesService;
import com.io7m.gatwick.preferences.GWPreferencesServiceType;
import com.io7m.jade.api.ApplicationDirectoriesType;
import com.io7m.jattribute.core.Attributes;
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
import java.time.temporal.ChronoUnit;
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
  private final GWConfigurationServiceType configs;
  private final Attributes attributes;
  private final ApplicationDirectoriesType directories;

  @FXML private ImageView splashLogo;
  @FXML private Pane splashPane;
  @FXML private Label splashText;
  @FXML private ProgressBar splashProgress;

  /**
   * The splash screen.
   *
   * @param inServices    The service directory
   * @param inAttributes  An attribute creator
   * @param inDirectories The application directories
   */

  public GWSplashController(
    final RPServiceDirectoryWritableType inServices,
    final Attributes inAttributes,
    final ApplicationDirectoriesType inDirectories)
  {
    this.services =
      Objects.requireNonNull(inServices, "services");
    this.executor =
      inServices.requireService(GWBackgroundExecutorType.class);
    this.configs =
      inServices.requireService(GWConfigurationServiceType.class);
    this.attributes =
      Objects.requireNonNull(inAttributes, "attributes");
    this.directories =
      Objects.requireNonNull(inDirectories, "directories");
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
      .execute(() -> {
        try {
          this.loadServices();
        } finally {
          this.publishBootCompletedService();
        }
      });
  }

  private void loadServices()
  {
    this.services.register(
      GWPreferencesServiceType.class,
      GWPreferencesService.create(
        this.directories,
        java.time.Duration.of(10L, ChronoUnit.SECONDS),
        this.attributes
      )
    );

    this.services.register(
      GWDebugService.class,
      GWDebugService.create(this.services)
    );

    this.services.register(
      GWIconSetServiceType.class,
      GWIconSetService.create()
    );
  }

  private void publishBootCompletedService()
  {
    this.executor.executor()
      .schedule(() -> {
        this.services.register(GWBootCompleted.class, new GWBootCompleted());
      }, 0L, TimeUnit.SECONDS);
  }
}
