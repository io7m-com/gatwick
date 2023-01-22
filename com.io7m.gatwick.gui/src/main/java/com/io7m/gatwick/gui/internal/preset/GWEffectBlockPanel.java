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

import com.io7m.digal.core.DialBoundedIntegerConverter;
import com.io7m.digal.core.DialControl;
import com.io7m.digal.core.DialControlLabelled;
import com.io7m.gatwick.controller.api.GWControllerType;
import com.io7m.gatwick.gui.internal.GWStrings;
import com.io7m.gatwick.gui.internal.icons.GWIconEnumerationSetType;
import com.io7m.gatwick.gui.internal.icons.GWIconSetServiceType;
import com.io7m.gatwick.iovar.GWIOEnumerationInfo;
import com.io7m.gatwick.iovar.GWIOEnumerationInfoType;
import com.io7m.gatwick.iovar.GWIOVariableInformation;
import com.io7m.gatwick.iovar.GWIOVariableType;
import com.io7m.jmulticlose.core.CloseableCollection;
import com.io7m.jmulticlose.core.CloseableCollectionType;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An abstract panel of controls.
 *
 * @param <S> The type of enumeration used to define the "type" of the panel
 */

public abstract class GWEffectBlockPanel<S extends Enum<S>>
  extends GWDeviceAwarePanel
{
  private final GWStrings strings;
  private final GWIconSetServiceType iconSets;
  private CloseableCollectionType<RuntimeException> subscriptions;

  /**
   * An abstract panel of controls.
   *
   * @param services The service directory
   */

  public GWEffectBlockPanel(
    final RPServiceDirectoryType services)
  {
    super(services);

    this.subscriptions =
      createSubscriptionCollection();
    this.strings =
      services.requireService(GWStrings.class);
    this.iconSets =
      services.requireService(GWIconSetServiceType.class);
  }

  private static CloseableCollectionType<RuntimeException> createSubscriptionCollection()
  {
    return CloseableCollection.create(() -> {
      return new RuntimeException("Closing device subscriptions failed.");
    });
  }

  private static boolean isEnumerated(
    final Class<?> valueClass)
  {
    return valueClass.isEnum();
  }

  protected abstract Optional<GWIOVariableType<S>> selectableType(
    GWControllerType device);

  protected abstract List<GWIOVariableType<?>> variablesForDials(
    GWControllerType device,
    S type);

  @Override
  protected final void onDeviceBecameUnavailable()
  {
    this.getChildren().clear();
    this.subscriptions.close();
  }

  @Override
  protected final void onDeviceBecameAvailable(
    final GWControllerType device)
  {
    this.subscriptions =
      createSubscriptionCollection();

    final var selectableOpt =
      this.selectableType(device);

    if (selectableOpt.isPresent()) {
      this.configureSelectablePanel(device, selectableOpt.get());
    } else {
      this.configureNonSelectablePanel(device);
    }
  }

  private void configureNonSelectablePanel(
    final GWControllerType device)
  {
    this.getChildren().add(this.createDialsForVariables(device, null));
  }

  private FlowPane createDialsForVariables(
    final GWControllerType device,
    final S type)
  {
    final var variables =
      this.variablesForDials(device, type);

    final var flowPane =
      new FlowPane(Orientation.HORIZONTAL);

    for (final var variable : variables) {
      final var info =
        variable.information();

      if (Objects.equals(info.valueClass(), String.class)) {
        continue;
      }

      final var control =
        this.createDialForVariable(variable, info);

      flowPane.getChildren().add(control);
    }
    return flowPane;
  }

  private void configureSelectablePanel(
    final GWControllerType device,
    final GWIOVariableType<S> typeVariable)
  {
    final GWIOVariableInformation<S> info =
      typeVariable.information();

    final S valueInitial =
      info.valueInitial();
    
    final Class<S> valueClass =
      info.valueClass();

    final GWIOEnumerationInfoType<S> enumInfo =
      GWIOEnumerationInfo.findInfo(valueClass);

    final List<S> values =
      enumInfo.valueList();

    /*
     * Create panels of controls for each "type" the effect block provides.
     */

    final var panels = new HashMap<S, Pane>();
    for (final var value : values) {
      final var pane = this.createDialsForVariables(device, value);
      pane.managedProperty().bind(pane.visibleProperty());
      pane.setVisible(false);
      panels.put(value, pane);
    }

    /*
     * Create a menu for selecting the effect "type".
     */


    final List<S> valuesSorted = new ArrayList<>(values);
    valuesSorted.sort(Comparator.comparing(enumInfo::label));

    final ObservableList<S> menuValues =
      FXCollections.observableList(valuesSorted);

    final ComboBox<S> menu = new ComboBox<>(menuValues);
    menu.setPrefWidth(512.0);
    menu.getSelectionModel().select(valueInitial);

    final GWIconEnumerationSetType<S> iconSet =
      this.iconSets.iconSetFor(valueClass);

    final var cellFactory =
      new GWIconListCellFactory<>(iconSet, enumInfo::label);

    menu.setCellFactory(cellFactory);
    menu.setConverter(new StringConverter<S>()
    {
      @Override
      public String toString(
        final S object)
      {
        return enumInfo.label(object);
      }

      @Override
      public S fromString(
        final String string)
      {
        return null;
      }
    });

    VBox.setMargin(menu, new Insets(0.0, 0.0, 16.0, 0.0));

    final var children = this.getChildren();
    children.add(menu);
    children.addAll(panels.values());

    /*
     * Set up a listener so that every time a value is received from the device
     * (via the GWIO variable), the type menu will be set to the correct
     * corresponding value.
     */

    this.subscriptions.add(
      typeVariable.subscribe((oldValue, newValue) -> {
        Platform.runLater(() -> {
          menu.getSelectionModel().select(newValue);
        });
      })
    );

    /*
     * Set up a change listener so that every time the type menu changes in
     * response to user input:
     *
     * 1. A new value is sent to the device.
     * 2. All panels other than the selected one are hidden.
     */

    final Consumer<S> reconfigureVisibility =
      newValue -> {
        panels.values().forEach(p -> p.setVisible(false));

        if (newValue != null) {
          final var showPanel = panels.get(newValue);
          showPanel.setVisible(true);
        }
      };

    menu.getSelectionModel()
      .selectedItemProperty()
      .addListener((observable, oldValue, newValue) -> {
        reconfigureVisibility.accept(newValue);

        if (newValue != null) {
          final var service = this.gtService();
          service.executeOnDevice(ctrl -> typeVariable.set(newValue));
        }
      });

    reconfigureVisibility.accept(typeVariable.get());
  }

  private DialControlLabelled createDialForVariable(
    final GWIOVariableType<?> variable,
    final GWIOVariableInformation<?> info)
  {
    final var control =
      new DialControlLabelled(info.label());

    control.setPrefWidth(96.0);

    final var tooltip = new Tooltip();
    Tooltip.install(control, tooltip);

    final var dial =
      control.dial();

    dial.getStyleClass()
      .add("dial");
    dial.dialRadialGaugeSize()
      .setValue(Double.valueOf(6.0));

    if (Objects.equals(info.valueClass(), Integer.class)) {
      this.createIntegerDial(
        unsoundCast(variable),
        info,
        control,
        tooltip,
        dial
      );
      return control;
    }

    if (isEnumerated(info.valueClass())) {
      this.createEnumeratedDial(
        unsoundCast(variable),
        control,
        tooltip,
        dial
      );
      return control;
    }

    return control;
  }

  private void createIntegerDial(
    final GWIOVariableType<Integer> variable,
    final GWIOVariableInformation<?> info,
    final DialControlLabelled control,
    final Tooltip tooltip,
    final DialControl dial)
  {
    final var vInt =
      variable.information();
    final var min =
      vInt.valueMinimumInclusive();
    final var max =
      vInt.valueMaximumInclusive();

    dial.setValueConverter(
      new DialBoundedIntegerConverter(min.intValue(), max.intValue()));
    dial.setConvertedValue(
      vInt.valueInitial().doubleValue());

    this.subscriptions.add(
      variable.subscribe((oldValue, newValue) -> {
        Platform.runLater(() -> {
          tooltip.setText(String.format("%s: %s", info.label(), newValue));
        });
      })
    );

    this.configureDialChangeListener(
      control,
      variable,
      number -> Integer.valueOf(number.intValue()),
      number -> Double.valueOf(number.doubleValue())
    );
  }

  private static <A, B> B unsoundCast(final A x)
  {
    return (B) x;
  }

  private <U extends Enum<U>> void createEnumeratedDial(
    final GWIOVariableType<U> variable,
    final DialControlLabelled control,
    final Tooltip tooltip,
    final DialControl dial)
  {
    final var vEnum =
      variable.information();
    final var min =
      vEnum.valueMinimumInclusive();
    final var max =
      vEnum.valueMaximumInclusive();
    final var init =
      vEnum.valueInitial();

    final var enumInfo =
      GWIOEnumerationInfo.findInfo(variable.information().valueClass());

    dial.setValueConverter(
      new DialBoundedIntegerConverter(
        enumInfo.toInt(min),
        enumInfo.toInt(max)
      )
    );

    control.valueFormatter().set(number -> {
      final var value =
        enumInfo.fromInt(number.intValue());
      return enumInfo.label(value);
    });

    this.subscriptions.add(
      variable.subscribe((oldValue, newValue) -> {
        Platform.runLater(() -> {
          tooltip.setText(
            String.format(
              "%s: %s",
              vEnum.label(),
              enumInfo.label(newValue)
            )
          );
        });
      })
    );

    dial.setTickCount(enumInfo.caseCount() - 1);
    dial.setConvertedValue(enumInfo.toInt(max));
    dial.setConvertedValue(enumInfo.toInt(init));

    this.configureDialChangeListener(
      control,
      variable,
      number -> enumInfo.fromInt(number.intValue()),
      value -> Double.valueOf(enumInfo.toInt(value))
    );
  }

  protected abstract void readFromDevice();

  private <T> void configureDialChangeListener(
    final DialControlLabelled control,
    final GWIOVariableType<T> variable,
    final Function<Number, T> convertFromDial,
    final Function<T, Double> convertToDial)
  {
    final var dial = control.dial();

    /*
     * Set up a listener so that every time a value is received from the device
     * (via the GWIO variable), the dial will be set to the correct corresponding
     * value.
     */

    this.subscriptions.add(
      variable.subscribe((oldValue, newValue) -> {
        Platform.runLater(() -> {
          final var dialValue = convertToDial.apply(newValue);
          dial.setConvertedValueQuietly(dialValue.doubleValue());
        });
      })
    );

    /*
     * Set up a change listener so that every time the dial changes in response
     * to user input, a new value is sent to the device.
     */

    final ChangeListener<Number> changeListener =
      (observable, oldValue, newValue) -> {
        final var value =
          convertFromDial.apply(newValue);
        final var service =
          this.gtService();
        service.executeOnDevice(ctrl -> variable.set(value));
      };

    dial.convertedValue().addListener(changeListener);

    this.subscriptions.add(() -> {
      dial.convertedValue().removeListener(changeListener);
    });
  }
}
