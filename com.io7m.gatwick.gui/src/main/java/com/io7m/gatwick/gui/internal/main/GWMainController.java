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


package com.io7m.gatwick.gui.internal.main;

import com.io7m.gatwick.gui.internal.GWBootCompleted;
import com.io7m.gatwick.gui.internal.GWPerpetualSubscriber;
import com.io7m.gatwick.gui.internal.GWScreenControllerFactory;
import com.io7m.gatwick.gui.internal.GWScreenControllerType;
import com.io7m.gatwick.gui.internal.GWStrings;
import com.io7m.gatwick.gui.internal.errors.GWErrorDialogs;
import com.io7m.gatwick.gui.internal.exec.GWBackgroundExecutorType;
import com.io7m.gatwick.gui.internal.gt.GWGT1KDeviceSelectionController;
import com.io7m.gatwick.gui.internal.gt.GWGT1KServiceStatusType;
import com.io7m.gatwick.gui.internal.gt.GWGT1KServiceStatusType.Connected;
import com.io7m.gatwick.gui.internal.gt.GWGT1KServiceStatusType.DeviceError;
import com.io7m.gatwick.gui.internal.gt.GWGT1KServiceStatusType.Disconnected;
import com.io7m.gatwick.gui.internal.gt.GWGT1KServiceStatusType.OpenFailed;
import com.io7m.gatwick.gui.internal.gt.GWGT1KServiceStatusType.PerformingIO;
import com.io7m.gatwick.gui.internal.gt.GWGT1KServiceType;
import com.io7m.gatwick.gui.internal.preferences.GWPreferencesController;
import com.io7m.jattribute.core.AttributeSubscriptionType;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import com.io7m.repetoir.core.RPServiceEventType;
import com.io7m.repetoir.core.RPServiceEventType.RPServiceRegistered;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.io7m.gatwick.gui.internal.gt.GWGT1KServiceStatusType.Disconnected.DISCONNECTED;
import static com.io7m.gatwick.gui.internal.gt.GWGTK1LongRunning.TASK_LONG;
import static javafx.animation.Interpolator.EASE_BOTH;
import static javafx.scene.paint.Color.DARKGREY;
import static javafx.scene.paint.Color.GOLD;
import static javafx.scene.paint.Color.LIMEGREEN;
import static javafx.scene.paint.Color.RED;

/**
 * The main controller that handles screen transitions and dispatching calls to
 * services and other controllers.
 */

public final class GWMainController implements GWScreenControllerType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(GWMainController.class);

  private final RPServiceDirectoryType services;
  private final GWStrings strings;
  private final GWScreenControllerFactory controllers;
  private final GWBackgroundExecutorType executor;
  private final GWErrorDialogs errors;
  private GWPerpetualSubscriber<RPServiceEventType> eventSubscriber;

  @FXML private AnchorPane mainContent;
  @FXML private StackPane mainContentStack;
  @FXML private Region transitionLine;
  @FXML private Rectangle statusConnectionLED;
  @FXML private Label statusConnectionText;
  @FXML private Label statusText;
  @FXML private ProgressBar statusProgress;
  @FXML private MenuBar menuBar;
  @FXML private MenuItem menuDeviceOpen;
  @FXML private Label statusLatency;

  private ObservableList<Node> children;
  private volatile Node latestPane;
  private GWGT1KServiceType gtService;
  private AttributeSubscriptionType statusLatencySub;

  /**
   * The main controller that handles screen transitions and dispatching calls
   * to services and other controllers.
   *
   * @param inServices The service directory
   */

  public GWMainController(
    final RPServiceDirectoryType inServices)
  {
    this.services =
      Objects.requireNonNull(inServices, "services");
    this.strings =
      inServices.requireService(GWStrings.class);
    this.controllers =
      inServices.requireService(GWScreenControllerFactory.class);
    this.executor =
      inServices.requireService(GWBackgroundExecutorType.class);
    this.errors =
      inServices.requireService(GWErrorDialogs.class);
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.menuBar.setDisable(true);

    this.children =
      this.mainContentStack.getChildren();
    this.latestPane =
      this.children.get(0);

    this.eventSubscriber =
      new GWPerpetualSubscriber<>(this::onServiceEvent);
    this.services.events()
      .subscribe(this.eventSubscriber);

    this.transitionLine.setVisible(false);
    this.statusConnectionText.setText("");
    this.statusText.setText(this.strings.format("booting"));

    this.gtService = this.services.requireService(GWGT1KServiceType.class);
    this.gtService.status().addListener((observable, oldValue, newValue) -> {
      this.gtStatusChanged(newValue);
    });

    this.gtStatusChanged(DISCONNECTED);
    if (this.services.optionalService(GWBootCompleted.class).isPresent()) {
      Platform.runLater(() -> {
        this.unlockUI();
        this.openPresetScreen();
      });
    }
  }

  private static String version()
  {
    final var pack = GWMainController.class.getPackage();
    final var title =
      Optional.ofNullable(pack.getImplementationTitle())
        .orElse("com.io7m.gatwick");
    final var implementationVersion =
      Optional.ofNullable(pack.getImplementationVersion())
        .orElse("0.0.0");
    return String.format("%s %s", title, implementationVersion);
  }

  private void gtStatusChanged(
    final GWGT1KServiceStatusType status)
  {
    if (status instanceof Disconnected) {
      if (this.statusLatencySub != null) {
        this.statusLatencySub.close();
        this.statusLatencySub = null;
      }

      this.statusLatency.setText("");
      this.mainContent.setDisable(false);
      this.statusConnectionText.setText(
        this.strings.format("statusDisconnected")
      );
      this.statusProgress.setVisible(false);
      this.statusConnectionLED.setFill(DARKGREY);
      this.menuDeviceOpen.setText(this.strings.format("menu.device.open"));
      return;
    }

    if (status instanceof OpenFailed failed) {
      this.mainContent.setDisable(false);
      this.statusConnectionText.setText(
        this.strings.format("statusFailed")
      );
      this.statusProgress.setVisible(false);
      this.statusConnectionLED.setFill(RED);
      this.errors.open(failed.task());
      this.menuDeviceOpen.setText(this.strings.format("menu.device.open"));
      return;
    }

    if (status instanceof Connected connected) {
      this.statusLatencySub =
        connected.device()
          .device()
          .commandRoundTripTime()
          .subscribe((oldValue, newValue) -> {
        Platform.runLater(() -> {
          this.statusLatency.setText(
            String.format("%.2fms", (double) newValue.toMillis())
          );
        });
      });

      this.mainContent.setDisable(false);
      this.statusConnectionText.setText(
        this.strings.format("statusConnected")
      );
      this.statusProgress.setVisible(false);
      this.statusConnectionLED.setFill(LIMEGREEN);
      this.menuDeviceOpen.setText(this.strings.format("menu.device.close"));
      return;
    }

    if (status instanceof PerformingIO io) {
      this.mainContent.setDisable(io.longRunning() == TASK_LONG);
      this.statusConnectionText.setText(
        this.strings.format("statusPerformingIO")
      );
      this.statusProgress.setVisible(true);
      this.statusConnectionLED.setFill(GOLD);
      this.menuDeviceOpen.setText(this.strings.format("menu.device.close"));
      return;
    }

    if (status instanceof DeviceError) {
      this.mainContent.setDisable(false);
      this.statusConnectionText.setText(
        this.strings.format("statusDeviceError")
      );
      this.statusProgress.setVisible(false);
      this.statusConnectionLED.setFill(RED);
      this.menuDeviceOpen.setText(this.strings.format("menu.device.close"));
      return;
    }
  }

  private void openPresetScreen()
  {
    this.doTransition(
      this.openScreen("preset.fxml"),
      GWScreenTransition.WIPE
    );
  }

  private void onServiceEvent(
    final RPServiceEventType event)
  {
    if (event instanceof RPServiceRegistered registered) {
      if (Objects.equals(registered.serviceType(), GWBootCompleted.class)) {
        Platform.runLater(() -> {
          this.unlockUI();
          this.openPresetScreen();
        });
      }
    }
  }

  private void unlockUI()
  {
    this.statusText.setText(version());
    this.menuBar.setDisable(false);
  }

  private Pane openScreen(
    final String name)
  {
    try {
      final var xml =
        GWMainController.class.getResource(
          "/com/io7m/gatwick/gui/internal/" + name);
      Objects.requireNonNull(xml, "xml");

      final var loader =
        new FXMLLoader(xml, this.strings.resources());

      loader.setControllerFactory(this.controllers);
      return loader.load();
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private void doTransition(
    final Pane newPane,
    final GWScreenTransition transition)
  {
    Platform.runLater(() -> {
      switch (transition) {
        case IMMEDIATE -> {
          this.children.setAll(List.of(newPane));
          this.latestPane = newPane;
        }

        case WIPE -> {
          final var previousPane =
            this.latestPane;
          final var w =
            this.mainContentStack.getWidth();
          final var h =
            this.mainContentStack.getHeight();

          final var targetRectangle =
            new Rectangle(w, h);
          final var sourceRectangle =
            new Rectangle(w, h);

          this.mainContentStack.widthProperty()
            .addListener((observable, oldValue, newValue) -> {
              sourceRectangle.setWidth(newValue.doubleValue());
              targetRectangle.setWidth(newValue.doubleValue());
            });

          this.mainContentStack.heightProperty()
            .addListener((observable, oldValue, newValue) -> {
              sourceRectangle.setHeight(newValue.doubleValue());
              targetRectangle.setHeight(newValue.doubleValue());
            });

          final var transitionTime = Duration.millis(300L);

          final var targetClipTrans = new TranslateTransition(transitionTime);
          targetClipTrans.setNode(targetRectangle);
          targetClipTrans.setFromX(w);
          targetClipTrans.setToX(0.0);
          targetClipTrans.setInterpolator(EASE_BOTH);

          final var sourceClipTrans = new TranslateTransition(transitionTime);
          sourceClipTrans.setNode(sourceRectangle);
          sourceClipTrans.setFromX(0.0);
          sourceClipTrans.setToX(-w);
          sourceClipTrans.setInterpolator(EASE_BOTH);

          final var lineTransition = new TranslateTransition(transitionTime);
          lineTransition.setNode(this.transitionLine);
          lineTransition.setFromX(w);
          lineTransition.setToX(-this.transitionLine.getWidth());
          lineTransition.setInterpolator(EASE_BOTH);
          lineTransition.setOnFinished(event -> {
            this.transitionLine.setVisible(false);
            if (previousPane != null) {
              this.children.remove(previousPane);
            }
            this.latestPane = newPane;
            newPane.setClip(null);
          });

          if (previousPane != null) {
            previousPane.setClip(sourceRectangle);
          }
          newPane.setClip(targetRectangle);

          this.children.add(newPane);
          this.children.remove(this.transitionLine);
          this.children.add(this.transitionLine);

          this.transitionLine.setVisible(true);
          sourceClipTrans.play();
          targetClipTrans.play();
          lineTransition.play();
        }
      }
    });
  }

  @FXML
  private void onMenuDeviceOpenSelected()
    throws IOException
  {
    if (!this.gtService.isOpen()) {
      GWGT1KDeviceSelectionController.open(this.services);
    } else {
      this.gtService.closeDevice();
    }
  }

  @FXML
  private void onMenuFilePreferencesSelected()
    throws IOException
  {
    GWPreferencesController.open(this.services);
  }

  @FXML
  private void onMenuFileQuitSelected()
  {
    try {
      this.services.close();
    } catch (final Throwable e) {
      LOG.error("error closing service directory: ", e);
    }

    Platform.exit();
  }
}
