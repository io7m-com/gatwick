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

import com.io7m.repetoir.core.RPServiceDirectoryType;

import java.util.List;
import java.util.Objects;

abstract class GWDCAbstract
  implements GWDebugCommandType
{
  private final RPServiceDirectoryType services;
  private final String name;
  private final List<String> arguments;

  protected GWDCAbstract(
    final RPServiceDirectoryType inServices,
    final String inName,
    final List<String> inArguments)
  {
    this.services =
      Objects.requireNonNull(inServices, "services");
    this.name =
      Objects.requireNonNull(inName, "name");
    this.arguments =
      Objects.requireNonNull(inArguments, "arguments");
  }

  protected final RPServiceDirectoryType services()
  {
    return this.services;
  }

  protected final String name()
  {
    return this.name;
  }

  protected final List<String> arguments()
  {
    return this.arguments;
  }
}
