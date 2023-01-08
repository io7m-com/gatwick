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


package com.io7m.gatwick.preferences;

import com.io7m.jade.api.ApplicationDirectoriesType;
import com.io7m.jattribute.core.AttributeReadableType;
import com.io7m.jattribute.core.AttributeType;
import com.io7m.jattribute.core.Attributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * The preferences service.
 */

public final class GWPreferencesService implements GWPreferencesServiceType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(GWPreferencesService.class);

  private final ScheduledExecutorService executors;
  private final ApplicationDirectoriesType directories;
  private final AttributeType<GWPreferences> prefsAttribute;
  private final Duration checkPreferencesEvery;
  private volatile FileTime timeThen;

  private GWPreferencesService(
    final ScheduledExecutorService inExecutors,
    final ApplicationDirectoriesType inDirectories,
    final AttributeType<GWPreferences> inPrefsAttribute,
    final Duration inCheckPreferencesEvery)
  {
    this.executors =
      Objects.requireNonNull(inExecutors, "executors");
    this.directories =
      Objects.requireNonNull(inDirectories, "directories");
    this.prefsAttribute =
      Objects.requireNonNull(inPrefsAttribute, "prefsAttribute");
    this.checkPreferencesEvery =
      Objects.requireNonNull(inCheckPreferencesEvery, "checkPreferencesEvery");
  }

  /**
   * Create a new preferences service.
   *
   * @param directories           The application directories
   * @param checkPreferencesEvery Re-read the preferences file this frequently
   * @param attributes            The attribute creator
   *
   * @return A new preferences service
   */

  public static GWPreferencesServiceType create(
    final ApplicationDirectoriesType directories,
    final Duration checkPreferencesEvery,
    final Attributes attributes)
  {
    final var executors =
      Executors.newSingleThreadScheduledExecutor(r -> {
        final var thread = new Thread(r);
        thread.setDaemon(true);
        thread.setName("com.io7m.gatwick.preferences.GWPreferencesService[%d]"
                         .formatted(thread.getId()));
        return thread;
      });

    final var preferences =
      loadPreferences(directories);

    final var prefsAttribute =
      attributes.create(preferences);

    final var service =
      new GWPreferencesService(
        executors,
        directories,
        prefsAttribute,
        checkPreferencesEvery
      );

    service.start();
    return service;
  }

  private static GWPreferences loadPreferences(
    final ApplicationDirectoriesType directories)
  {
    final Path prefsFile = preferencesFileName(directories);

    var preferences = defaultPreferences();
    try (var stream = Files.newInputStream(prefsFile)) {
      preferences = GWPreferencesIO.parse(prefsFile.toUri(), stream);
    } catch (final NoSuchFileException e) {
      // Not a problem
    } catch (final IOException e) {
      LOG.debug("failed to load preferences: ", e);
    }
    return preferences;
  }

  private static GWPreferences defaultPreferences()
  {
    return new GWPreferences(
      new GWPreferencesDevice(false)
    );
  }

  private static Path preferencesFileName(
    final ApplicationDirectoriesType directories)
  {
    final var baseDirectory =
      directories.configurationDirectory();
    return baseDirectory.resolve("preferences.xml")
      .toAbsolutePath();
  }

  private void start()
  {
    final var prefsFile =
      preferencesFileName(this.directories);

    try {
      this.timeThen = Files.getLastModifiedTime(prefsFile);
    } catch (final IOException e) {
      this.timeThen = FileTime.from(Instant.now());
    }

    this.executors.schedule(
      () -> this.readPreferencesFile(prefsFile),
      this.checkPreferencesEvery.toMillis(),
      MILLISECONDS
    );
  }

  private void readPreferencesFile(
    final Path prefsFile)
  {
    try {
      final var timeNow =
        Files.getLastModifiedTime(prefsFile);

      if (timeNow.compareTo(this.timeThen) > 0) {
        this.prefsAttribute.set(loadPreferences(this.directories));
        this.timeThen = timeNow;
      }
    } catch (final NoSuchFileException e) {
      // Not a problem
    } catch (final IOException e) {
      LOG.debug("i/o error: ", e);
    }
  }

  @Override
  public String description()
  {
    return "Preferences service.";
  }

  @Override
  public Path preferencesFile()
  {
    return preferencesFileName(this.directories);
  }

  @Override
  public ApplicationDirectoriesType directories()
  {
    return this.directories;
  }

  @Override
  public AttributeReadableType<GWPreferences> preferences()
  {
    return this.prefsAttribute;
  }

  @Override
  public CompletableFuture<GWPreferences> preferencesUpdate(
    final Function<GWPreferences, GWPreferences> updater)
  {
    final var future = new CompletableFuture<GWPreferences>();
    this.executors.execute(() -> {
      try {
        final var newPreferences =
          updater.apply(this.preferences().get());
        future.complete(this.writePreferences(newPreferences));
      } catch (final Throwable e) {
        future.completeExceptionally(e);
      }
    });
    return future;
  }

  private GWPreferences writePreferences(
    final GWPreferences newPreferences)
    throws IOException
  {
    final var fileName =
      preferencesFileName(this.directories);
    final var fileNameTmp =
      fileName.resolveSibling(fileName.getFileName() + ".tmp");

    this.prefsAttribute.set(newPreferences);

    Files.createDirectories(fileName.getParent());
    try (var stream = Files.newOutputStream(
      fileNameTmp, WRITE, TRUNCATE_EXISTING, CREATE)) {
      GWPreferencesIO.write(newPreferences, stream);
      Files.move(fileNameTmp, fileName, REPLACE_EXISTING, ATOMIC_MOVE);
    }

    return newPreferences;
  }

  @Override
  public void close()
  {
    this.executors.shutdown();
  }

  @Override
  public String toString()
  {
    return String.format("[GWPreferencesService 0x%08x]", this.hashCode());
  }
}
