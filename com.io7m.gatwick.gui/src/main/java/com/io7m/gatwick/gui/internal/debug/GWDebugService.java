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

package com.io7m.gatwick.gui.internal.debug;

import com.io7m.gatwick.preferences.GWPreferences;
import com.io7m.gatwick.preferences.GWPreferencesServiceType;
import com.io7m.jmulticlose.core.CloseableCollection;
import com.io7m.jmulticlose.core.CloseableCollectionType;
import com.io7m.jmulticlose.core.ClosingResourceFailedException;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import com.io7m.repetoir.core.RPServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * The debugging service.
 */

public final class GWDebugService implements RPServiceType, AutoCloseable
{
  private static final Logger LOG =
    LoggerFactory.getLogger(GWDebugService.class);

  private final RPServiceDirectoryType services;
  private final GWPreferencesServiceType preferences;
  private final CloseableCollectionType<ClosingResourceFailedException> resources;
  private GWDebugServer server;

  /**
   * The debugging service.
   *
   * @param inServices The service directory
   */

  private GWDebugService(
    final RPServiceDirectoryType inServices)
  {
    this.services =
      Objects.requireNonNull(inServices, "services");
    this.preferences =
      this.services.requireService(GWPreferencesServiceType.class);
    this.resources =
      CloseableCollection.create();
  }

  /**
   * Create a new debugging service.
   *
   * @param inServices The service directory
   *
   * @return A debugging service
   */

  public static GWDebugService create(
    final RPServiceDirectoryType inServices)
  {
    final var server = new GWDebugService(inServices);
    server.start();
    return server;
  }

  private void start()
  {
    final var preferencesNow =
      this.preferences.preferences()
        .get();

    if (preferencesNow.debug().enableDebugServer()) {
      this.debugServerStart(preferencesNow);
    }

    this.resources.add(
      this.preferences.preferences()
        .subscribe(this::onUpdatePreferences)
    );
  }

  private void onUpdatePreferences(
    final GWPreferences oldPreferences,
    final GWPreferences newPreferences)
  {
    final var oldEnable =
      oldPreferences.debug().enableDebugServer();
    final var newEnable =
      newPreferences.debug().enableDebugServer();

    final var wantStartup = !oldEnable && newEnable;
    if (wantStartup) {
      this.debugServerShutdown();
      this.debugServerStart(newPreferences);
    }

    final var wantShutdown = oldEnable && (!newEnable);
    if (wantShutdown) {
      this.debugServerShutdown();
    }
  }

  private void debugServerShutdown()
  {
    LOG.debug("debug server shutdown requested");

    try {
      this.server.close();
    } catch (final Exception e) {
      // Nothing we can do about this.
    }
  }

  private void debugServerStart(
    final GWPreferences newPreferences)
  {
    LOG.debug("debug server startup requested");
    this.server = GWDebugServer.create(this.services, newPreferences.debug());
  }

  @Override
  public String toString()
  {
    return String.format("[GWDebugService 0x%08x]", this.hashCode());
  }

  @Override
  public String description()
  {
    return "Debugging service.";
  }

  @Override
  public void close()
    throws Exception
  {
    this.resources.close();
  }
}
