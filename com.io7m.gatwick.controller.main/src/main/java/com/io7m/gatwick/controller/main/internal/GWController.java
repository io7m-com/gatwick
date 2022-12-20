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


package com.io7m.gatwick.controller.main.internal;

import com.io7m.gatwick.controller.api.GWControllerConfiguration;
import com.io7m.gatwick.controller.api.GWControllerException;
import com.io7m.gatwick.controller.api.GWControllerType;
import com.io7m.gatwick.controller.api.GWPatchType;
import com.io7m.gatwick.controller.main.internal.generated.StructGT_1000;
import com.io7m.gatwick.device.api.GWDeviceException;
import com.io7m.gatwick.device.api.GWDeviceFactoryType;
import com.io7m.gatwick.device.api.GWDeviceType;
import com.io7m.jattribute.core.Attributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static com.io7m.gatwick.controller.api.GWControllerStandardErrorCodes.DEVICE_ERROR;

/**
 * The controller implementation.
 */

public final class GWController implements GWControllerType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(GWController.class);

  private final GWDeviceType device;
  private final Attributes attributes;
  private final GWPatch patchCurrent;
  private final StructGT_1000 memoryMap;

  private GWController(
    final GWDeviceType inDevice)
  {
    this.device =
      Objects.requireNonNull(inDevice, "device");

    this.attributes =
      Attributes.create(throwable -> {
        LOG.error("error assigning attribute value: ", throwable);
      });

    this.memoryMap =
      new StructGT_1000(this.device, this.attributes, 0);
    this.patchCurrent =
      new GWPatch(this.device, this.attributes, this.memoryMap.f_patch);
  }

  /**
   * Open a controller.
   *
   * @param devices       The device factory
   * @param configuration The controller configuration
   *
   * @return A controller
   *
   * @throws GWControllerException On errors
   */

  public static GWControllerType open(
    final GWDeviceFactoryType devices,
    final GWControllerConfiguration configuration)
    throws GWControllerException
  {
    try {
      return new GWController(
        devices.openDevice(configuration.deviceConfiguration())
      );
    } catch (final GWDeviceException e) {
      throw new GWControllerException(DEVICE_ERROR, e.getMessage(), e);
    }
  }

  @Override
  public GWPatchType patchCurrent()
  {
    return this.patchCurrent;
  }

  @Override
  public GWDeviceType device()
  {
    return this.device;
  }

  @Override
  public void close()
    throws GWControllerException
  {
    try {
      this.device.close();
    } catch (final GWDeviceException e) {
      throw new GWControllerException(DEVICE_ERROR, e.getMessage(), e);
    }
  }
}
