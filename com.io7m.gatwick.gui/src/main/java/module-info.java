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

import com.io7m.gatwick.controller.api.GWControllerFactoryType;
import com.io7m.gatwick.device.api.GWDeviceFactoryType;

/**
 * GT-1000 controller (GUI)
 */

open module com.io7m.gatwick.gui
{
  requires com.io7m.gatwick.device.api;
  requires com.io7m.gatwick.controller.api;

  requires org.slf4j;
  requires com.io7m.jade.api;
  requires com.io7m.jmulticlose.core;
  requires com.io7m.junreachable.core;
  requires com.io7m.jxtrand.api;
  requires com.io7m.repetoir.core;
  requires com.io7m.taskrecorder.core;
  requires javafx.base;
  requires javafx.controls;
  requires javafx.fxml;

  uses GWControllerFactoryType;
  uses GWDeviceFactoryType;

  exports com.io7m.gatwick.gui;
}
