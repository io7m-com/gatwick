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
import com.io7m.gatwick.iovar.GWIOExtendedEnumerationType;
import com.io7m.gatwick.iovar.GWIOVariableType;
import com.io7m.jmulticlose.core.CloseableCollection;
import com.io7m.jmulticlose.core.CloseableCollectionType;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.beans.value.ChangeListener;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * An abstract panel of controls.
 */

public abstract class GWEffectBlockPanel extends GWDeviceAwarePanel
{
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
    return GWIOExtendedEnumerationType.class.isAssignableFrom(valueClass);
  }

  protected abstract List<GWIOVariableType<?>> variablesForDials(
    GWControllerType device);

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

    final var variables =
      this.variablesForDials(device);

    final var children = this.getChildren();
    for (final var variable : variables) {
      final var info =
        variable.information();

      if (Objects.equals(info.valueClass(), String.class)) {
        continue;
      }

      final var control =
        new DialControlLabelled(info.label());

      control.setPrefWidth(96.0);

      final var dial =
        control.dial();

      dial.getStyleClass()
          .add("dial");
      dial.dialRadialGaugeSize()
        .setValue(Double.valueOf(6.0));

      if (Objects.equals(info.valueClass(), Integer.class)) {
        final var varInt =
          (GWIOVariableType<Integer>) variable;
        final var vInt =
          varInt.information();

        final var min =
          vInt.valueMinimumInclusive();
        final var max =
          vInt.valueMaximumInclusive();

        dial.setValueConverter(
          new DialBoundedIntegerConverter(min.intValue(), max.intValue()));
        dial.setConvertedValue(
          vInt.valueInitial().doubleValue());

        this.configureDialChangeListener(
          dial,
          varInt,
          number -> Integer.valueOf(number.intValue()),
          number -> Double.valueOf(number.doubleValue())
        );
      }

      if (isEnumerated(info.valueClass())) {
        final var varEnum =
          (GWIOVariableType<GWIOExtendedEnumerationType<?>>) variable;

        final var vEnum =
          varEnum.information();
        final var min =
          vEnum.valueMinimumInclusive();
        final var max =
          vEnum.valueMaximumInclusive();
        final var init =
          vEnum.valueInitial();

        dial.setValueConverter(
          new DialBoundedIntegerConverter(min.toInt(), max.toInt()));
        control.valueFormatter().set(number -> {
          final var value =
            min.fromInt(number.intValue());
          return value.label();
        });

        dial.setTickCount(min.caseCount() - 1);
        dial.setConvertedValue(max.toInt());
        dial.setConvertedValue(init.toInt());

        this.configureDialChangeListener(
          dial,
          varEnum,
          number -> init.fromInt(number.intValue()),
          value -> Double.valueOf(value.toInt())
        );
      }

      children.add(control);
    }
  }

  protected abstract void readFromDevice();

  private <T> void configureDialChangeListener(
    final DialControl dial,
    final GWIOVariableType<T> variable,
    final Function<Number, T> convertFromDial,
    final Function<T, Double> convertToDial)
  {
    /*
     * Set up a listener so that every time a value is received from the device
     * (via the GWIO variable), the dial will be set to the correct corresponding
     * value.
     */

    this.subscriptions.add(
      variable.subscribe((oldValue, newValue) -> {
        final var dialValue = convertToDial.apply(newValue);
        dial.setConvertedValueQuietly(dialValue.doubleValue());
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
