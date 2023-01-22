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

import com.io7m.gatwick.codegen.internal.GWEnumerations;
import com.io7m.gatwick.codegen.internal.GWStructures;
import com.io7m.gatwick.codegen.jaxb.Enumeration;
import com.io7m.gatwick.codegen.jaxb.ParameterBase;
import com.io7m.gatwick.codegen.jaxb.Structure;
import com.io7m.gatwick.codegen.jaxb.StructureReferenceType;
import com.io7m.jaffirm.core.Preconditions;
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
import java.util.stream.Collectors;

import static com.io7m.gatwick.codegen.internal.GWHexIntegers.parseHex;
import static com.io7m.gatwick.codegen.internal.GWParameterOffsets.offsetOf;
import static com.io7m.gatwick.codegen.internal.GWParameterSizes.sizeOf;

/**
 * A definition compiler.
 */

public final class GWDefinitionCompiler
{
  private static final Logger LOG =
    LoggerFactory.getLogger(GWDefinitionCompiler.class);

  private final GWDefinitionCompilerConfiguration configuration;
  private final HashMap<String, Long> structureSizes;
  private final HashSet<Path> files;
  private Map<String, Structure> structures;
  private Map<String, Enumeration> enumerations;

  private GWDefinitionCompiler(
    final GWDefinitionCompilerConfiguration inConfiguration)
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

  public static GWDefinitionCompiler create(
    final GWDefinitionCompilerConfiguration configuration)
  {
    return new GWDefinitionCompiler(configuration);
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
    this.compileStructures();

    return Set.copyOf(this.files);
  }

  private void compileStructures()
    throws IOException
  {
    if (this.configuration.structures()) {
      final var newFiles =
        new GWStructures(this.configuration, this.structures)
          .compile();

      for (final var file : newFiles) {
        Preconditions.checkPreconditionV(
          !this.files.contains(file),
          "File %s cannot be created twice",
          file
        );
        this.files.add(file);
      }
    }
  }

  private void compileEnumerations()
    throws IOException
  {
    if (this.configuration.enumerations()) {
      final var newFiles =
        new GWEnumerations(this.configuration)
          .compileEnumerations(this.enumerations.values());

      for (final var file : newFiles) {
        Preconditions.checkPreconditionV(
          !this.files.contains(file),
          "File %s cannot be created twice",
          file
        );
        this.files.add(file);
      }
    }
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
      structure.getParameterChainOrParameterEnumeratedOrParameterFractional();

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
    final ParameterBase o)
  {
    if (o instanceof StructureReferenceType ref) {
      return this.checkStructureSize(this.structures.get(ref.getType()));
    }
    return sizeOf(o);
  }
}
