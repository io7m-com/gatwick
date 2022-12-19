/*
 * Copyright © 2020 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.gatwick.tests.codegen;

import com.io7m.gatwick.codegen.GWDefinitionCompiler;
import com.io7m.gatwick.codegen.GWDefinitionCompilerConfiguration;
import com.io7m.gatwick.codegen.jaxb.Definitions;
import com.io7m.gatwick.tests.GWZip;
import com.sun.source.util.JavacTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.lang.module.ModuleFinder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Locale.ROOT;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class Compilation
{
  private static final Logger LOG =
    LoggerFactory.getLogger(Compilation.class);

  private final Path directory;
  private final Path moduleDirectory;
  private ClassLoader classLoader;

  private Compilation(
    final Path inOutputDirectory,
    final Path inModuleDirectory)
  {
    this.directory =
      Objects.requireNonNull(inOutputDirectory, "outputDirectory");
    this.moduleDirectory =
      Objects.requireNonNull(inModuleDirectory, "moduleDirectory");
  }

  public static ClassLoader compile(
    final Definitions definitions,
    final Path outputDirectory,
    final Path moduleDirectory)
    throws Exception
  {
    final var compilation =
      new Compilation(outputDirectory, moduleDirectory);

    final var compiler =
      GWDefinitionCompiler.create(new GWDefinitionCompilerConfiguration(
        outputDirectory,
        true,
        "com.io7m.gatwick.generated.structs",
        true,
        "com.io7m.gatwick.generated.enums",
        definitions
      ));

    final var files =
      compiler.execute();

    compilation.compileJava(files.stream().toList());
    return compilation.classLoader();
  }

  private void compileJava(
    final List<Path> createdFiles)
    throws IOException
  {
    final var listener =
      new Diagnostics();
    final var tool =
      ToolProvider.getSystemJavaCompiler();

    try (var fileManager = tool.getStandardFileManager(listener, ROOT, UTF_8)) {
      final var compileFiles =
        new ArrayList<SimpleJavaFileObject>(createdFiles.size());
      for (final var created : createdFiles) {
        LOG.info("compile {}", created.toUri());
        compileFiles.add(new SourceFile(created));
      }

      final var compileArguments =
        List.of(
          "-g",
          "-Werror",
          "-Xdiags:verbose",
          "-Xlint:unchecked",
          "-d",
          this.directory.toAbsolutePath().toString()
        );

      final var task =
        (JavacTask) tool.getTask(
          null,
          fileManager,
          listener,
          compileArguments,
          null,
          compileFiles
        );

      final var result =
        task.call();

      assertTrue(
        result.booleanValue(),
        "Compilation of all files must succeed"
      );
    }

    this.createModule();
    this.classLoader = this.loadClasses();
  }

  private void createModule()
    throws IOException
  {
    final var moduleFile = this.moduleDirectory.resolve("module.jar");
    LOG.debug("creating module {}", moduleFile);

    final var metaInf = this.directory.resolve("META-INF");
    Files.createDirectories(metaInf);
    final var manifest = metaInf.resolve("MANIFEST.MF");

    try (var out = Files.newBufferedWriter(manifest)) {
      out.append("Manifest-Version: 1.0");
      out.newLine();
      out.append("Automatic-Module-Name: com.io7m.gatwick.tests.generated");
      out.newLine();
    }

    GWZip.create(moduleFile, this.directory);
  }

  private ClassLoader loadClasses()
  {
    final var finder =
      ModuleFinder.of(this.moduleDirectory);
    final var parent =
      ModuleLayer.boot();

    final var configuration =
      parent.configuration()
        .resolve(
          finder,
          ModuleFinder.of(),
          Set.of("com.io7m.gatwick.tests.generated")
        );

    final var systemClassLoader =
      ClassLoader.getSystemClassLoader();
    final var layer =
      parent.defineModulesWithOneLoader(configuration, systemClassLoader);
    return layer.findLoader("com.io7m.gatwick.tests.generated");
  }

  public ClassLoader classLoader()
  {
    return this.classLoader;
  }
}
