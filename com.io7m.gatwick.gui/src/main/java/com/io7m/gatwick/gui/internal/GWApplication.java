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


package com.io7m.gatwick.gui.internal;

import com.io7m.gatwick.gui.GWConfiguration;
import com.io7m.gatwick.gui.internal.errors.GWErrorDialogs;
import com.io7m.gatwick.gui.internal.exec.GWBackgroundExecutor;
import com.io7m.gatwick.gui.internal.exec.GWBackgroundExecutorType;
import com.io7m.gatwick.gui.internal.gt.GWGT1KServiceType;
import com.io7m.gatwick.gui.internal.gt.GWGT1KService;
import com.io7m.gatwick.gui.internal.icons.GWIconService;
import com.io7m.gatwick.gui.internal.icons.GWIconServiceType;
import com.io7m.repetoir.core.RPServiceDirectory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * The main JavaFX application.
 */

public final class GWApplication extends Application
{
  private final GWConfiguration configuration;

  /**
   * The main JavaFX application.
   *
   * @param inConfiguration The configuration
   */

  public GWApplication(
    final GWConfiguration inConfiguration)
  {
    this.configuration =
      Objects.requireNonNull(inConfiguration, "configuration");
  }

  @Override
  public void start(
    final Stage stage)
    throws Exception
  {
    final var mainXML =
      GWApplication.class.getResource(
        "/com/io7m/gatwick/gui/internal/main.fxml");
    Objects.requireNonNull(mainXML, "mainXML");

    final var services = new RPServiceDirectory();
    final var strings = new GWStrings(this.configuration.locale());
    services.register(GWStrings.class, strings);

    final var executor = GWBackgroundExecutor.create();
    services.register(GWBackgroundExecutorType.class, executor);

    final var controllers = new GWScreenControllerFactory(services);
    services.register(GWScreenControllerFactory.class, controllers);

    final var gtservice = GWGT1KService.create(services);
    services.register(GWGT1KServiceType.class, gtservice);

    final var icons = GWIconService.create();
    services.register(GWIconServiceType.class, icons);

    final var errors = new GWErrorDialogs(services);
    services.register(GWErrorDialogs.class, errors);

    final var mainLoader = new FXMLLoader(mainXML, strings.resources());
    mainLoader.setControllerFactory(controllers);

    final Pane pane = mainLoader.load();
    GWCSS.setCSS(pane);

    stage.setTitle(strings.format("title"));
    stage.setWidth(1280.0);
    stage.setHeight(720.0);
    stage.setScene(new Scene(pane));
    stage.show();
  }
}
