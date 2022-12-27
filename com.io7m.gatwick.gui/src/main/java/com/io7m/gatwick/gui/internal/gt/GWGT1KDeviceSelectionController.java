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


package com.io7m.gatwick.gui.internal.gt;

import com.io7m.gatwick.controller.api.GWControllerConfiguration;
import com.io7m.gatwick.device.api.GWDeviceConfiguration;
import com.io7m.gatwick.device.api.GWDeviceFactoryProperty;
import com.io7m.gatwick.device.api.GWDeviceFactoryType;
import com.io7m.gatwick.device.api.GWDeviceMIDIDescription;
import com.io7m.gatwick.gui.internal.GWApplication;
import com.io7m.gatwick.gui.internal.GWCSS;
import com.io7m.gatwick.gui.internal.GWScreenControllerFactory;
import com.io7m.gatwick.gui.internal.GWScreenControllerType;
import com.io7m.gatwick.gui.internal.GWStrings;
import com.io7m.gatwick.gui.internal.errors.GWErrorDialogs;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import com.io7m.taskrecorder.core.TRTask;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import static javafx.stage.Modality.APPLICATION_MODAL;

/**
 * The controller for device selection.
 */

public final class GWGT1KDeviceSelectionController
  implements GWScreenControllerType
{
  private final RPServiceDirectoryType services;
  private final GWGT1KServiceType gt;
  private final GWStrings strings;
  private final GWErrorDialogs errors;

  @FXML private Button openButton;
  @FXML private TableView<GWDeviceMIDIDescription> midiDevices;
  @FXML private Button refreshButton;
  @FXML private Label refreshStatusText;
  @FXML private ProgressBar refreshProgress;
  @FXML private Button refreshWhy;

  private TRTask<List<GWDeviceMIDIDescription>> taskLast;

  /**
   * The controller for device selection.
   *
   * @param inServices The service directory
   */

  public GWGT1KDeviceSelectionController(
    final RPServiceDirectoryType inServices)
  {
    this.services =
      Objects.requireNonNull(inServices, "services");
    this.gt =
      inServices.requireService(GWGT1KServiceType.class);
    this.strings =
      inServices.requireService(GWStrings.class);
    this.errors =
      inServices.requireService(GWErrorDialogs.class);
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.openButton.setDisable(true);

    final var tableColumns =
      this.midiDevices.getColumns();

    final var nameColumn =
      (TableColumn<GWDeviceMIDIDescription, String>) tableColumns.get(0);
    final var descriptionColumn =
      (TableColumn<GWDeviceMIDIDescription, String>) tableColumns.get(1);

    nameColumn.setCellValueFactory(param -> {
      final var description = param.getValue();
      return new ReadOnlyStringWrapper(description.midiDeviceName());
    });

    descriptionColumn.setCellValueFactory(param -> {
      final var description = param.getValue();
      return new ReadOnlyStringWrapper(
        String.format(
          "%s, %s, %s",
          description.midiDeviceDescription(),
          description.midiDeviceVendor(),
          description.midiDeviceVersion())
      );
    });

    this.midiDevices.setPlaceholder(new Label(""));

    this.midiDevices.getSelectionModel()
      .setSelectionMode(SelectionMode.SINGLE);

    this.midiDevices.getSelectionModel()
      .selectedItemProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.openButton.setDisable(newValue == null);
      });

    this.refreshDevices();
  }

  private void refreshDevices()
  {
    this.refreshButton.setDisable(true);
    this.refreshProgress.setVisible(true);
    this.refreshWhy.setVisible(false);
    this.refreshStatusText.setText(
      this.strings.format("devices.detecting")
    );

    this.gt.detectDevices(GWGT1KDeviceSelectionController::filterDeviceFactory)
      .whenComplete((task, exception) -> {
        Platform.runLater(() -> {
          this.refreshButton.setDisable(false);
          this.refreshProgress.setVisible(false);

          if (task != null) {
            this.onDeviceListReceived(task);
          } else {
            this.refreshStatusText.setText("");
          }
        });
      });
  }

  private static boolean filterDeviceFactory(
    final GWDeviceFactoryType deviceFactory)
  {
    final var properties = deviceFactory.properties();
    if (properties.contains(new GWDeviceFactoryProperty("fake"))) {
      return true;
    }
    return false;
  }

  private void onDeviceListReceived(
    final TRTask<List<GWDeviceMIDIDescription>> task)
  {
    this.taskLast = task;

    final var deviceList =
      task.result().orElse(List.of());

    this.refreshWhy.setVisible(true);
    this.refreshStatusText.setText(
      this.strings.format("devices.detected", deviceList.size())
    );
    this.midiDevices.setItems(
      FXCollections.observableList(deviceList)
    );
  }

  @FXML
  private void onCancelSelected()
  {
    final var stage =
      (Stage) this.openButton.getScene()
        .getWindow();

    stage.close();
  }

  @FXML
  private void onOpenSelected()
  {
    final var device =
      this.midiDevices.getSelectionModel()
        .selectedItemProperty()
        .get();

    this.gt.open(new GWControllerConfiguration(
      GWGT1KDeviceSelectionController::filterDeviceFactory,
      new GWDeviceConfiguration(
        device,
        Duration.ofSeconds(3L),
        Duration.ofSeconds(3L),
        3,
        Duration.ofMillis(100L)
      )
    ));

    final var stage =
      (Stage) this.openButton.getScene()
        .getWindow();

    stage.close();
  }

  @FXML
  private void onRefreshSelected()
  {
    this.refreshDevices();
  }

  /**
   * Open a controller.
   *
   * @param services The service directory
   *
   * @throws IOException On errors
   */

  public static void open(
    final RPServiceDirectoryType services)
    throws IOException
  {
    final var controllers =
      services.requireService(GWScreenControllerFactory.class);
    final var strings =
      services.requireService(GWStrings.class);

    final var mainXML =
      GWApplication.class.getResource(
        "/com/io7m/gatwick/gui/internal/openDevice.fxml");
    Objects.requireNonNull(mainXML, "mainXML");

    final var mainLoader = new FXMLLoader(mainXML, strings.resources());
    mainLoader.setControllerFactory(controllers);

    final Pane pane = mainLoader.load();
    GWCSS.setCSS(pane);

    final var stage = new Stage();
    stage.setTitle(strings.format("devices.openTitle"));
    stage.initModality(APPLICATION_MODAL);
    stage.setWidth(800.0);
    stage.setHeight(600.0);
    stage.setScene(new Scene(pane));
    stage.show();
  }

  @FXML
  private void onRefreshWhySelected()
  {
    final var task = this.taskLast;
    if (task != null) {
      this.errors.open(task);
    }
  }
}
