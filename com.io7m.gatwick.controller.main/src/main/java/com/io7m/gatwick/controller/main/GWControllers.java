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

package com.io7m.gatwick.controller.main;

import com.io7m.gatwick.controller.api.GWControllerConfiguration;
import com.io7m.gatwick.controller.api.GWControllerException;
import com.io7m.gatwick.controller.api.GWControllerFactoryType;
import com.io7m.gatwick.controller.api.GWControllerType;
import com.io7m.gatwick.controller.main.internal.GWController;
import com.io7m.gatwick.device.api.GWDeviceFactoryType;

import java.util.Objects;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

/**
 * A factory of controllers.
 */

public final class GWControllers implements GWControllerFactoryType
{
  private final GWDeviceFactoryType devices;

  /**
   * Create a factory of controllers. Dependencies are loaded from ServiceLoader.
   */

  public GWControllers()
  {
    this(
      ServiceLoader.load(GWDeviceFactoryType.class)
        .findFirst()
        .orElseThrow(() -> {
          return new ServiceConfigurationError(
            "No services available of type %s"
              .formatted(GWDeviceFactoryType.class)
          );
        })
    );
  }

  /**
   * Create a factory of controllers.
   *
   * @param inDevices The device factory
   */

  public GWControllers(
    final GWDeviceFactoryType inDevices)
  {
    this.devices =
      Objects.requireNonNull(inDevices, "devices");
  }

  @Override
  public GWDeviceFactoryType devices()
  {
    return this.devices;
  }

  @Override
  public GWControllerType openController(
    final GWControllerConfiguration configuration)
    throws GWControllerException
  {
    return GWController.open(this.devices, configuration);
  }
}
