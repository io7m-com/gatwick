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

import com.io7m.gatwick.controller.api.GWControllerType;
import com.io7m.gatwick.gui.internal.GWScreenControllerType;
import com.io7m.gatwick.gui.internal.gt.GWGT1KServiceStatusType;
import com.io7m.gatwick.gui.internal.gt.GWGT1KServiceStatusType.GWGT1KServiceStatusClosedType;
import com.io7m.gatwick.gui.internal.gt.GWGT1KServiceStatusType.GWGT1KServiceStatusOpenType;
import com.io7m.gatwick.gui.internal.gt.GWGT1KServiceType;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.scene.layout.VBox;

/**
 * An abstract panel for components that need to be device-aware.
 */

public abstract class GWDeviceAwarePanel extends VBox
  implements GWScreenControllerType
{
  private final GWGT1KServiceType gt;

  /**
   * An abstract panel for components that need to be device-aware.
   *
   * @param services The service directory
   */

  public GWDeviceAwarePanel(
    final RPServiceDirectoryType services)
  {
    this.gt =
      services.requireService(GWGT1KServiceType.class);
    this.gt.status()
      .addListener((observable, oldValue, newValue) -> {
        this.onGTStateChanged(oldValue, newValue);
      });
  }

  protected final GWGT1KServiceType gtService()
  {
    return this.gt;
  }

  private void onGTStateChanged(
    final GWGT1KServiceStatusType oldStatus,
    final GWGT1KServiceStatusType newStatus)
  {
    if (oldStatus instanceof GWGT1KServiceStatusClosedType) {
      if (newStatus instanceof GWGT1KServiceStatusOpenType statusOpen) {
        this.onDeviceBecameAvailable(statusOpen.device());
        return;
      }
    }

    if (oldStatus instanceof GWGT1KServiceStatusOpenType) {
      if (newStatus instanceof GWGT1KServiceStatusClosedType) {
        this.onDeviceBecameUnavailable();
        return;
      }
    }
  }

  protected abstract void onDeviceBecameUnavailable();

  protected abstract void onDeviceBecameAvailable(GWControllerType device);
}
