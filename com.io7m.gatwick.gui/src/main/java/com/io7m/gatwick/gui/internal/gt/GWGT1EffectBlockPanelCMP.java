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

import com.io7m.digal.core.DialBoundedIntegerConverter;
import com.io7m.digal.core.DialControlLabelled;
import com.io7m.gatwick.controller.api.GWControllerType;
import com.io7m.gatwick.iovar.GWIOExtendedEnumerationType;
import com.io7m.gatwick.iovar.GWIOVariableInformation;
import com.io7m.jmulticlose.core.CloseableCollection;
import com.io7m.jmulticlose.core.CloseableCollectionType;
import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * A panel for the CMP block.
 */

public final class GWGT1EffectBlockPanelCMP
  extends GWGT1DeviceAwarePanel
{
  private CloseableCollectionType<RuntimeException> subscriptions;

  /**
   * A panel for the CMP block.
   *
   * @param services The service directory
   */

  public GWGT1EffectBlockPanelCMP(
    final RPServiceDirectoryType services)
  {
    super(services);

    this.subscriptions =
      CloseableCollection.create(() -> {
        return new RuntimeException("Closing device subscriptions failed.");
      });
  }

  @Override
  protected void onDeviceBecameUnavailable()
  {
    this.getChildren().clear();
    this.subscriptions.close();
  }

  @Override
  protected void onDeviceBecameAvailable(
    final GWControllerType device)
  {
    this.subscriptions =
      CloseableCollection.create(() -> {
        return new RuntimeException("Closing device subscriptions failed.");
      });

    final var variables =
      device.patchCurrent()
        .cmp()
        .variables();

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

      dial.dialRadialGaugeSize()
        .setValue(Double.valueOf(6.0));

      if (Objects.equals(info.valueClass(), Integer.class)) {
        final var vInt =
          (GWIOVariableInformation<Integer>) info;

        final var min = vInt.valueMinimumInclusive();
        final var max = vInt.valueMaximumInclusive();
        dial.setValueConverter(
          new DialBoundedIntegerConverter(min.intValue(), max.intValue()));
        dial.setConvertedValue(vInt.valueInitial().doubleValue());
      }

      if (isEnumerated(info.valueClass())) {
        final var vEnum =
          (GWIOVariableInformation<GWIOExtendedEnumerationType<?>>) info;

        final GWIOExtendedEnumerationType<?> min =
          vEnum.valueMinimumInclusive();
        final GWIOExtendedEnumerationType<?> max =
          vEnum.valueMaximumInclusive();
        final GWIOExtendedEnumerationType<?> init =
          vEnum.valueInitial();

        dial.setValueConverter(
          new DialBoundedIntegerConverter(min.toInt(), max.toInt()));
        control.valueFormatter().set(number -> {
          final GWIOExtendedEnumerationType<?> value =
            min.fromInt(number.intValue());
          return value.label();
        });

        dial.setTickCount(min.caseCount() - 1);
        dial.setConvertedValue(max.toInt());
        dial.setConvertedValue(init.toInt());
      }

      this.subscriptions.add(
        variable.subscribe((oldValue, newValue) -> {

        })
      );

      children.add(control);
    }
  }

  private static boolean isEnumerated(
    final Class<?> valueClass)
  {
    return GWIOExtendedEnumerationType.class.isAssignableFrom(valueClass);
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {

  }
}
