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


package com.io7m.gatwick.tests.preferences;

import com.io7m.gatwick.preferences.GWPreferences;
import com.io7m.gatwick.preferences.GWPreferencesDebug;
import com.io7m.gatwick.preferences.GWPreferencesDevice;
import com.io7m.gatwick.preferences.GWPreferencesIO;
import com.io7m.gatwick.preferences.GWPreferencesService;
import com.io7m.gatwick.tests.GWTestDirectories;
import com.io7m.jade.api.ApplicationDirectoriesType;
import com.io7m.jattribute.core.Attributes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class GWPreferencesTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(GWPreferencesTest.class);

  private Path directory;
  private ApplicationDirectoriesType directories;
  private Attributes attributes;

  @BeforeEach
  public void setup()
    throws IOException
  {
    this.directory =
      GWTestDirectories.createTempDirectory();
    this.directories =
      new ApplicationDirectoriesType()
      {
        @Override
        public Path configurationDirectory()
        {
          return GWPreferencesTest.this.directory.resolve("config");
        }

        @Override
        public Path dataDirectory()
        {
          return GWPreferencesTest.this.directory.resolve("data");
        }

        @Override
        public Path cacheDirectory()
        {
          return GWPreferencesTest.this.directory.resolve("cache");
        }
      };

    this.attributes =
      Attributes.create(throwable -> {
        LOG.error("error: ", throwable);
      });
  }

  @AfterEach
  public void tearDown()
    throws IOException
  {
    GWTestDirectories.deleteDirectory(this.directory);
  }

  /**
   * A missing configuration file is not an error.
   *
   * @throws Exception On errors
   */

  @Test
  public void testPreferencesNoFile()
    throws Exception
  {
    try (var service =
           GWPreferencesService.create(
             this.directories,
             Duration.ofSeconds(30L),
             this.attributes)) {

    }
  }

  /**
   * Reloading configuration files works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testPreferencesFileReloading()
    throws Exception
  {
    final var file =
      this.directory.resolve("config")
        .resolve("preferences.xml")
        .toAbsolutePath();

    Files.createDirectories(file.getParent());
    Files.writeString(file, """
      <?xml version="1.0" encoding="UTF-8"?>
      <Preferences xmlns="urn:com.io7m.gatwick:preferences:1">
        <Device ShowFakeDevices="false"/>
        <Debug EnableDebugServer="false" DebugServerPort="30000"/>
      </Preferences>
      """);

    final var prefs =
      new LinkedList<GWPreferences>();

    try (var service =
           GWPreferencesService.create(
             this.directories,
             Duration.ofSeconds(1L),
             this.attributes)) {

      final var prefs0 =
        new GWPreferences(
          new GWPreferencesDevice(false),
          new GWPreferencesDebug(false, 30000)
        );

      service.preferences()
        .subscribe((oldValue, newValue) -> {
          LOG.debug("received: {}", newValue);
          prefs.add(newValue);
        });

      Files.writeString(file, """
        <?xml version="1.0" encoding="UTF-8"?>
        <Preferences xmlns="urn:com.io7m.gatwick:preferences:1">
          <Device ShowFakeDevices="true"/>
          <Debug EnableDebugServer="true" DebugServerPort="30001"/>
        </Preferences>
        """);

      while (prefs.size() < 2) {
        Thread.sleep(100L);
      }

      {
        final var p = prefs.poll();
        assertEquals(prefs0, p);
      }

      final var prefs1 =
        new GWPreferences(
          new GWPreferencesDevice(true),
          new GWPreferencesDebug(true, 30001)
        );

      {
        final var p = prefs.poll();
        assertEquals(prefs1, p);
      }
    }
  }

  /**
   * Updating configuration files works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testPreferencesUpdates()
    throws Exception
  {
    final var file =
      this.directory.resolve("config")
        .resolve("preferences.xml")
        .toAbsolutePath();

    final var prefs0 =
      new GWPreferences(
        new GWPreferencesDevice(true),
        new GWPreferencesDebug(true, 30000)
      );

    final var prefs1 =
      new GWPreferences(
        new GWPreferencesDevice(false),
        new GWPreferencesDebug(false, 30001)
      );

    try (var service =
           GWPreferencesService.create(
             this.directories,
             Duration.ofSeconds(1L),
             this.attributes)) {

      service.preferencesUpdate(preferences -> prefs0).get();
      service.preferencesUpdate(preferences -> prefs1).get();
      service.preferencesUpdate(preferences -> prefs0).get();
      service.preferencesUpdate(preferences -> prefs1).get();

      assertEquals(prefs1, service.preferences().get());
    }

    assertEquals(prefs1, GWPreferencesIO.parse(file.toUri(), Files.newInputStream(file)));
  }
}
