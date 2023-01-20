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

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A convenient abstract icon set implementation.
 *
 * @param <S> The enum type
 */

public abstract class GWIconEnumerationSetAbstract<S extends Enum<S>>
  implements GWIconEnumerationSetType<S>
{
  private final Map<S, String> icons;
  private final Class<S> clazz;
  private final String fallback;

  protected GWIconEnumerationSetAbstract(
    final Class<S> inClazz,
    final Map<S, String> inNames,
    final String inFallback)
  {
    this.clazz =
      Objects.requireNonNull(inClazz, "clazz");
    this.icons =
      Objects.requireNonNull(inNames, "icons");
    this.fallback =
      Objects.requireNonNull(inFallback, "inFallback");
  }

  @Override
  public final Optional<Image> iconFor(
    final S value)
  {
    final var name = this.icons.get(value);
    return Optional.of(
      iconByName(Objects.requireNonNullElse(name, this.fallback))
    );
  }

  @Override
  public final Class<S> enumerationClass()
  {
    return this.clazz;
  }

  private static Image iconByName(
    final String name)
  {
    return new Image(
      GWIconService.class.getResource(
          "/com/io7m/gatwick/gui/internal/%s".formatted(name))
        .toString(),
      true
    );
  }
}
