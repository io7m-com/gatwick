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

import com.io7m.gatwick.gui.internal.gt.GWGT1KDeviceSelectionController;
import com.io7m.gatwick.gui.internal.main.GWMainController;
import com.io7m.gatwick.gui.internal.preset.GWPresetController;
import com.io7m.gatwick.gui.internal.splash.GWSplashController;
import com.io7m.repetoir.core.RPServiceDirectoryWritableType;
import com.io7m.repetoir.core.RPServiceType;
import javafx.util.Callback;

import java.util.Objects;

/**
 * The factory of screen controllers.
 */

public final class GWScreenControllerFactory
  implements Callback<Class<?>, Object>, RPServiceType
{
  private final RPServiceDirectoryWritableType services;

  /**
   * The factory of screen controllers.
   *
   * @param inServices The service directory
   */

  public GWScreenControllerFactory(
    final RPServiceDirectoryWritableType inServices)
  {
    this.services =
      Objects.requireNonNull(inServices, "services");
  }

  @Override
  public Object call(
    final Class<?> param)
  {
    if (Objects.equals(param, GWMainController.class)) {
      return new GWMainController(this.services);
    }
    if (Objects.equals(param, GWSplashController.class)) {
      return new GWSplashController(this.services);
    }
    if (Objects.equals(param, GWGT1KDeviceSelectionController.class)) {
      return new GWGT1KDeviceSelectionController(this.services);
    }
    if (Objects.equals(param, GWPresetController.class)) {
      return new GWPresetController(this.services);
    }

    throw new IllegalStateException(
      "Unrecognized screen controller: %s".formatted(param)
    );
  }

  @Override
  public String toString()
  {
    return String.format("[GWScreenControllerFactory 0x%08x]", this.hashCode());
  }

  @Override
  public String description()
  {
    return "Screen controller factory service.";
  }
}
