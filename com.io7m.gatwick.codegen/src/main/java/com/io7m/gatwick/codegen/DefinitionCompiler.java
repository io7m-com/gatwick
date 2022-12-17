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

import com.io7m.gatwick.codegen.jaxb.Enumeration;
import com.io7m.gatwick.codegen.jaxb.Structure;
import com.io7m.gatwick.codegen.jaxb.StructureReference;
import com.io7m.jodist.ClassName;
import com.io7m.jodist.CodeBlock;
import com.io7m.jodist.JavaFile;
import com.io7m.jodist.MethodSpec;
import com.io7m.jodist.TypeSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.io7m.gatwick.codegen.internal.HexIntegers.parseHex;
import static com.io7m.gatwick.codegen.internal.ParameterOffsets.offsetOf;
import static com.io7m.gatwick.codegen.internal.ParameterSizes.sizeOf;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * A definition compiler.
 */

public final class DefinitionCompiler
{
  private static final Logger LOG =
    LoggerFactory.getLogger(DefinitionCompiler.class);

  private static final Pattern INVALID_START =
    Pattern.compile("^[^A-Z]+.*");

  private final DefinitionCompilerConfiguration configuration;
  private final HashMap<String, Long> structureSizes;
  private final HashSet<Path> files;
  private Map<String, Structure> structures;
  private Map<String, Enumeration> enumerations;

  private DefinitionCompiler(
    final DefinitionCompilerConfiguration inConfiguration)
  {
    this.configuration =
      Objects.requireNonNull(inConfiguration, "configuration");
    this.structureSizes =
      new HashMap<>();
    this.structures =
      new HashMap<>();
    this.files =
      new HashSet<Path>();
  }

  /**
   * Create a definition compiler.
   *
   * @param configuration The configuration
   *
   * @return The compiler
   */

  public static DefinitionCompiler create(
    final DefinitionCompilerConfiguration configuration)
  {
    return new DefinitionCompiler(configuration);
  }

  /**
   * Execute the compiler.
   *
   * @return The list of created files
   *
   * @throws IOException On errors
   */

  public Set<Path> execute()
    throws IOException
  {
    this.files.clear();
    this.structureSizes.clear();

    this.structures =
      this.configuration.definitions()
        .getTypes()
        .getEnumerationOrStructure()
        .stream()
        .filter(o -> o instanceof Structure)
        .map(Structure.class::cast)
        .collect(Collectors.toMap(Structure::getName, Function.identity()));

    this.enumerations =
      this.configuration.definitions()
        .getTypes()
        .getEnumerationOrStructure()
        .stream()
        .filter(o -> o instanceof Enumeration)
        .map(Enumeration.class::cast)
        .collect(Collectors.toMap(Enumeration::getName, Function.identity()));

    this.checkStructures();
    this.compileEnumerations();
    return Set.copyOf(this.files);
  }

  private void compileEnumerations()
    throws IOException
  {
    for (final var enumeration : this.enumerations.values()) {
      this.compileEnumeration(enumeration);
    }
  }

  private void compileEnumeration(
    final Enumeration enumeration)
    throws IOException
  {
    final var className =
      ClassName.get(
        this.configuration.packageName(),
        makeEnumerationName(enumeration.getName())
      );

    final var builder = TypeSpec.enumBuilder(className);
    builder.addModifiers(PUBLIC);
    for (final var caseV : enumeration.getCase()) {
      builder.addEnumConstant(makeEnumerationConstant(caseV.getName()));
    }

    builder.addMethod(makeEnumerationIntegerMethod(enumeration));
    builder.addMethod(makeEnumerationLabelMethod(enumeration));

    final var enumT =
      builder.build();
    final var javaFile =
      JavaFile.builder(this.configuration.packageName(), enumT)
        .build();

    this.files.add(javaFile.writeToPath(this.configuration.outputDirectory()));
  }

  private static MethodSpec makeEnumerationIntegerMethod(
    final Enumeration enumeration)
  {
    final var spec = MethodSpec.methodBuilder("toInt");
    spec.returns(int.class);
    spec.addModifiers(PUBLIC);

    var code = CodeBlock.builder();
    code = code.beginControlFlow("return switch (this)");

    for (final var caseV : enumeration.getCase()) {
      code = code.addStatement(
        "case $L -> $L",
        makeEnumerationConstant(caseV.getName()),
        caseV.getValue().toString()
      );
    }
    code = code.endControlFlow();
    code = code.add(";");

    spec.addCode(code.build());
    return spec.build();
  }

  private static MethodSpec makeEnumerationLabelMethod(
    final Enumeration enumeration)
  {
    final var spec = MethodSpec.methodBuilder("labelOf");
    spec.returns(String.class);
    spec.addModifiers(PUBLIC);

    var code = CodeBlock.builder();
    code = code.beginControlFlow("return switch (this)");

    for (final var caseV : enumeration.getCase()) {
      code = code.addStatement(
        "case $L -> $S",
        makeEnumerationConstant(caseV.getName()),
        caseV.getName()
      );
    }
    code = code.endControlFlow();
    code = code.add(";");

    spec.addCode(code.build());
    return spec.build();
  }

  private static String makeEnumerationConstant(
    final String name)
  {
    final var text =
      name.replace(" ", "_")
        .replace("->", "_TO_")
        .replace(".", "_")
        .replace("-", "_NEGATIVE_")
        .replace(":", "_")
        .replace("/", "_SLASH_")
        .replace("+", "_POSITIVE_")
        .toUpperCase();

    if (INVALID_START.matcher(text).matches()) {
      return "X_%s".formatted(text);
    }
    return text;
  }

  private static String makeEnumerationName(
    final String name)
  {
    return name.replace(" ", "_")
      .replace(".", "_")
      .replace("-", "_")
      .replace(":", "_");
  }

  private void checkStructures()
  {
    for (final var structure : this.structures.values()) {
      this.checkStructure(structure);
    }
  }

  private void checkStructure(
    final Structure structure)
  {
    Objects.requireNonNull(
      structure,
      "structure (%s)".formatted(structure.getName())
    );

    this.checkStructureSize(structure);
  }

  private long checkStructureSize(
    final Structure structure)
  {
    final var name = structure.getName();
    final var existing = this.structureSizes.get(name);
    if (existing != null) {
      return existing.longValue();
    }

    final var parameters =
      structure.getParameterEnumeratedOrParameterHighCutOrParameterLowCut();

    parameters.sort((o1, o2) -> {
      return Long.compareUnsigned(offsetOf(o1), offsetOf(o2));
    });

    final var last =
      parameters.get(parameters.size() - 1);
    final var size =
      offsetOf(last) + this.sizeOfGet(last);

    final var expected = structure.getExpectedSize();
    if (expected != null) {
      final var expectedSize = parseHex(expected);
      if (expectedSize != size) {
        LOG.warn(
          "{}: size 0x{} != expected size 0x{}",
          name,
          Long.toUnsignedString(size, 16),
          Long.toUnsignedString(expectedSize, 16)
        );
      }
    }

    LOG.debug("{}: size 0x{}", name, Long.toUnsignedString(size, 16));
    this.structureSizes.put(name, size);
    return size;
  }

  private long sizeOfGet(
    final Object o)
  {
    if (o instanceof StructureReference ref) {
      return this.checkStructureSize(this.structures.get(ref.getType()));
    }
    return sizeOf(o);
  }
}
