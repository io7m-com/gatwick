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


package com.io7m.gatwick.gui.internal.preferences;

import com.io7m.gatwick.gui.internal.GWApplication;
import com.io7m.gatwick.gui.internal.GWCSS;
import com.io7m.gatwick.gui.internal.GWScreenControllerFactory;
import com.io7m.gatwick.gui.internal.GWScreenControllerType;
import com.io7m.gatwick.gui.internal.GWStrings;
import com.io7m.gatwick.preferences.GWPreferences;
import com.io7m.gatwick.preferences.GWPreferencesDevice;
import com.io7m.gatwick.preferences.GWPreferencesServiceType;
import com.io7m.jattribute.core.AttributeSubscriptionType;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import com.io7m.repetoir.core.RPServiceDirectoryWritableType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Objects;
import java.util.ResourceBundle;

import static javafx.scene.control.SelectionMode.SINGLE;
import static javafx.stage.Modality.APPLICATION_MODAL;

/**
 * The preferences screen.
 */

public final class GWPreferencesController implements GWScreenControllerType
{
  private final GWPreferencesServiceType preferences;
  private final GWStrings strings;
  private AttributeSubscriptionType preferencesSubscription;
  private EnumMap<Section, Pane> panes;

  @FXML private TextField directoryConfiguration;
  @FXML private TextField directoryData;
  @FXML private TextField directoryCache;
  @FXML private Pane paneDirectories;
  @FXML private Pane paneDevices;
  @FXML private ListView<Section> paneList;
  @FXML private CheckBox devicesShowFake;

  private enum Section
  {
    DEVICES,
    DIRECTORIES
  }

  /**
   * The preferences screen.
   *
   * @param inServices The service directory
   */

  public GWPreferencesController(
    final RPServiceDirectoryWritableType inServices)
  {
    Objects.requireNonNull(inServices, "services");

    this.preferences =
      inServices.requireService(GWPreferencesServiceType.class);
    this.strings =
      inServices.requireService(GWStrings.class);
  }

  /**
   * Open a new window with a preferences controller.
   *
   * @param services The services
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
        "/com/io7m/gatwick/gui/internal/preferences.fxml");
    Objects.requireNonNull(mainXML, "mainXML");

    final var mainLoader = new FXMLLoader(mainXML, strings.resources());
    mainLoader.setControllerFactory(controllers);

    final Pane pane = mainLoader.load();
    GWCSS.setCSS(pane);

    final var stage = new Stage();

    stage.setOnCloseRequest(event -> {
      final GWPreferencesController controller =
        mainLoader.getController();

      controller.close();
      stage.close();
    });

    stage.setTitle(strings.format("preferences.title"));
    stage.initModality(APPLICATION_MODAL);
    stage.setWidth(800.0);
    stage.setHeight(600.0);
    stage.setScene(new Scene(pane));
    stage.show();
  }

  private void close()
  {
    this.preferencesSubscription.close();
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.panes = new EnumMap<>(Section.class);
    this.panes.put(Section.DIRECTORIES, this.paneDirectories);
    this.panes.put(Section.DEVICES, this.paneDevices);
    this.panes.values().forEach(pane -> {
      pane.managedProperty().bind(pane.visibleProperty());
      pane.setVisible(false);
    });

    this.paneList.setItems(
      FXCollections.observableArrayList(Section.values())
        .sorted()
    );
    this.paneList.getSelectionModel().setSelectionMode(SINGLE);
    this.paneList.setCellFactory(param -> this.sectionListCell());
    this.paneList.getSelectionModel()
      .selectedItemProperty()
      .addListener((observable, oldValue, newValue) -> {
        for (final var entry : this.panes.entrySet()) {
          final var section = entry.getKey();
          final var pane = entry.getValue();
          pane.setVisible(section == newValue);
        }
      });

    this.preferencesSubscription =
      this.preferences.preferences()
        .subscribe((oldValue, newValue) -> {
          Platform.runLater(() -> this.onPreferencesChanged(newValue));
        });

    final var directories =
      this.preferences.directories();
    this.directoryCache.setText(
      directories.cacheDirectory().toString());
    this.directoryData.setText(
      directories.dataDirectory().toString());
    this.directoryConfiguration.setText(
      directories.configurationDirectory().toString());

    this.onPreferencesChanged(
      this.preferences.preferences()
        .get()
    );
  }

  private ListCell<Section> sectionListCell()
  {
    return new ListCell<>()
    {
      @Override
      public void updateItem(
        final Section section,
        final boolean empty)
      {
        super.updateItem(section, empty);
        if (empty || section == null) {
          this.setText(null);
        } else {
          this.setText(GWPreferencesController.this.labelFor(section));
        }
      }
    };
  }

  private String labelFor(
    final Section section)
  {
    return switch (section) {
      case DEVICES -> {
        yield this.strings.format("preferences.devices");
      }
      case DIRECTORIES -> {
        yield this.strings.format("preferences.directories");
      }
    };
  }

  private void onPreferencesChanged(
    final GWPreferences newPreferences)
  {
    this.devicesShowFake.setSelected(
      newPreferences.device()
        .showFakeDevices()
    );
  }

  @FXML
  private void onCancelSelected()
  {
    final Stage stage =
      (Stage) this.paneDevices.getScene()
        .getWindow();

    stage.close();
  }

  @FXML
  private void onSaveSelected()
  {
    final var newPreferences =
      new GWPreferences(
        new GWPreferencesDevice(this.devicesShowFake.isSelected()));

    this.preferences.preferencesUpdate(ignored -> {
      return newPreferences;
    });

    final Stage stage =
      (Stage) this.paneDevices.getScene()
        .getWindow();

    stage.close();
  }
}
