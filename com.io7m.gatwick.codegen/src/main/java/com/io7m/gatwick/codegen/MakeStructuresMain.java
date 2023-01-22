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

package com.io7m.gatwick.codegen;

import java.nio.file.Paths;

/**
 * Main compiler entry point.
 */

public final class MakeStructuresMain
{
  private MakeStructuresMain()
  {

  }

  /**
   * Main compiler entry point.
   *
   * @param args Command-line arguments
   *
   * @throws Exception On errors
   */

  public static void main(
    final String[] args)
    throws Exception
  {
    final var outputDirectory = Paths.get(args[0]);
    final var structurePackageName = args[1];
    final var enumerationPackageName = args[2];

    final var definitions =
      GWDefinitionParser.parse();

    final var compiler =
      GWDefinitionCompiler.create(new GWDefinitionCompilerConfiguration(
        outputDirectory,
        true,
        structurePackageName,
        false,
        enumerationPackageName,
        "com.io7m.gatwick.controller.api",
        definitions
      ));

    compiler.execute();
  }
}
