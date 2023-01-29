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


package com.io7m.gatwick.gui.internal.icons;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * The icon set service.
 */

public final class GWIconSetService
  implements GWIconSetServiceType
{
  private static final None<?> NONE = new None<>();

  private final Map<Class<?>, GWIconEnumerationSetType<?>> sets;

  private GWIconSetService(
    final Map<Class<?>, GWIconEnumerationSetType<?>> inSets)
  {
    this.sets = Objects.requireNonNull(inSets, "sets");
  }

  /**
   * Create an icon set service.
   *
   * @return The service
   */

  public static GWIconSetServiceType create()
  {
    final var loader =
      ServiceLoader.load(GWIconEnumerationSetType.class);

    final var sets =
      new HashMap<Class<?>, GWIconEnumerationSetType<?>>();
    final var iterator =
      loader.iterator();

    while (iterator.hasNext()) {
      final var set = iterator.next();
      sets.put(set.enumerationClass(), set);
    }

    return new GWIconSetService(
      Map.copyOf(sets)
    );
  }

  @Override
  public String description()
  {
    return "Icon set service.";
  }

  @Override
  public String toString()
  {
    return String.format("[GWIconSetService 0x%08x]", this.hashCode());
  }

  @Override
  public <S extends Enum<S>> GWIconEnumerationSetType<S> iconSetFor(
    final Class<S> clazz)
  {
    final var set = this.sets.get(clazz);
    if (set == null) {
      return empty();
    }

    return (GWIconEnumerationSetType<S>) set;
  }

  private static <S extends Enum<S>> GWIconEnumerationSetType<S> empty()
  {
    return (GWIconEnumerationSetType<S>) (Object) NONE;
  }

  private static final class None<S extends Enum<S>> implements
    GWIconEnumerationSetType<S>
  {
    private None()
    {

    }

    @Override
    public Class<S> enumerationClass()
    {
      throw new UnsupportedOperationException("No enumeration class.");
    }

    @Override
    public Optional<Image> iconFor(
      final S value)
    {
      return Optional.empty();
    }
  }
}
