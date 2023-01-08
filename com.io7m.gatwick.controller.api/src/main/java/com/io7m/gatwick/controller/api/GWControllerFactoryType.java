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


package com.io7m.gatwick.controller.api;

import com.io7m.gatwick.device.api.GWDeviceFactoryType;
import com.io7m.gatwick.device.api.GWDeviceMIDIDescription;
import com.io7m.taskrecorder.core.TRTask;

import java.util.List;
import java.util.function.Predicate;

/**
 * A controller factory.
 */

public interface GWControllerFactoryType
{
  /**
   * Open a controller.
   *
   * @param configuration The controller configuration
   *
   * @return A controller
   *
   * @throws GWControllerException On errors
   */

  GWControllerType openController(
    GWControllerConfiguration configuration)
    throws GWControllerException;

  /**
   * Open a controller using the given device factory.
   *
   * @param devices       The device factory
   * @param configuration The controller configuration
   *
   * @return A controller
   *
   * @throws GWControllerException On errors
   */

  GWControllerType openControllerWith(
    GWDeviceFactoryType devices,
    GWControllerConfiguration configuration)
    throws GWControllerException;

  /**
   * Detect devices that appear to be GT-1000 devices.
   *
   * @param devices The devices
   *
   * @return A list of probable GT-1000 devices
   */

  TRTask<List<GWDeviceMIDIDescription>> detectDevicesWith(
    GWDeviceFactoryType devices);

  /**
   * Detect devices that appear to be GT-1000 devices.
   *
   * @param deviceFactoryFilter The predicate used to filter device factories
   *
   * @return A list of probable GT-1000 devices
   */

  TRTask<List<GWDeviceMIDIDescription>> detectDevices(
    Predicate<GWDeviceFactoryType> deviceFactoryFilter);
}
