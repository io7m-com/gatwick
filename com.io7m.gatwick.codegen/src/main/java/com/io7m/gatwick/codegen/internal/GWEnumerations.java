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

package com.io7m.gatwick.codegen.internal;

import com.io7m.gatwick.codegen.GWDefinitionCompilerConfiguration;
import com.io7m.gatwick.codegen.jaxb.Enumeration;
import com.io7m.gatwick.iovar.GWIOExtendedEnumerationType;
import com.io7m.gatwick.iovar.GWIOSerializers;
import com.io7m.gatwick.iovar.GWIOVariableDeserializeType;
import com.io7m.gatwick.iovar.GWIOVariableSerializeType;
import com.io7m.jodist.ClassName;
import com.io7m.jodist.CodeBlock;
import com.io7m.jodist.FieldSpec;
import com.io7m.jodist.JavaFile;
import com.io7m.jodist.MethodSpec;
import com.io7m.jodist.ParameterizedTypeName;
import com.io7m.jodist.TypeSpec;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * Functions to generate enums.
 */

public final class GWEnumerations
{
  private static final Pattern INVALID_START =
    Pattern.compile("^[^A-Z]+.*");

  private final HashSet<Path> files;
  private final GWDefinitionCompilerConfiguration configuration;

  /**
   * Functions to generate enums.
   *
   * @param inConfiguration The compiler configuration
   */

  public GWEnumerations(
    final GWDefinitionCompilerConfiguration inConfiguration)
  {
    this.configuration =
      Objects.requireNonNull(inConfiguration, "configuration");
    this.files =
      new HashSet<Path>();
  }

  /**
   * Generate classes.
   *
   * @param enumerations The enumerations
   *
   * @return The generated files
   *
   * @throws IOException On errors
   */

  public Set<Path> compileEnumerations(
    final Collection<Enumeration> enumerations)
    throws IOException
  {
    for (final var e : enumerations) {
      this.compileEnumeration(e);
    }
    return Set.copyOf(this.files);
  }

  private void compileEnumeration(
    final Enumeration enumeration)
    throws IOException
  {
    final var className =
      ClassName.get(
        this.configuration.enumerationPackage(),
        makeEnumerationName(enumeration.getName())
      );

    final var builder = TypeSpec.enumBuilder(className);
    builder.addSuperinterface(ParameterizedTypeName.get(
      ClassName.get(GWIOExtendedEnumerationType.class),
      className
    ));

    builder.addModifiers(PUBLIC);
    for (final var caseV : enumeration.getCase()) {
      builder.addEnumConstant(makeEnumerationConstant(caseV.getName()));
    }

    builder.addField(makeEnumerationSerializerField(className));
    builder.addField(makeEnumerationDeserializerField(className));
    builder.addMethod(makeEnumerationIntegerMethod(enumeration));
    builder.addMethod(makeEnumerationLabelMethod(enumeration));
    builder.addMethod(makeEnumerationFirstMethod(className, enumeration));
    builder.addMethod(makeEnumerationLastMethod(className, enumeration));
    builder.addMethod(makeEnumerationOfIntegerMethod(className, enumeration));
    builder.addMethod(makeEnumerationDeserializerMethod(className));
    builder.addMethod(makeEnumerationSerializerMethod(className));
    builder.addMethod(makeEnumerationSerializeSize(enumeration));
    builder.addMethod(makeEnumerationPrevious(enumeration, className));
    builder.addMethod(makeEnumerationNext(enumeration, className));

    final var enumT =
      builder.build();
    final var javaFile =
      JavaFile.builder(this.configuration.enumerationPackage(), enumT)
        .build();

    this.files.add(javaFile.writeToPath(this.configuration.outputDirectory()));
  }

  private static MethodSpec makeEnumerationPrevious(
    final Enumeration enumeration,
    final ClassName className)
  {
    return MethodSpec.methodBuilder("previous")
      .addModifiers(PUBLIC)
      .addAnnotation(Override.class)
      .returns(className)
      .addCode(
        "return $T.ofInt((this.toInt() - 1) % $L);",
        className,
        Integer.valueOf(enumeration.getCase().size()))
      .build();
  }

  private static MethodSpec makeEnumerationNext(
    final Enumeration enumeration,
    final ClassName className)
  {
    return MethodSpec.methodBuilder("next")
      .addModifiers(PUBLIC)
      .addAnnotation(Override.class)
      .returns(className)
      .addCode(
        "return $T.ofInt((this.toInt() + 1) % $L);",
        className,
        Integer.valueOf(enumeration.getCase().size()))
      .build();
  }

  private static MethodSpec makeEnumerationSerializeSize(
    final Enumeration enumeration)
  {
    var size = 1;
    final var caseCount = enumeration.getCase().size();
    if (caseCount > 127) {
      size = 2;
    }
    if (caseCount > 256) {
      size = 4;
    }

    return MethodSpec.methodBuilder("serializeSize")
      .addModifiers(PUBLIC)
      .addModifiers(STATIC)
      .returns(int.class)
      .addCode("return $L;", Integer.valueOf(size))
      .build();
  }

  private static MethodSpec makeEnumerationSerializerMethod(
    final ClassName className)
  {
    final var type =
      ParameterizedTypeName.get(
        ClassName.get(GWIOVariableSerializeType.class),
        className
      );

    return MethodSpec.methodBuilder("serializer")
      .addModifiers(PUBLIC)
      .addModifiers(STATIC)
      .returns(type)
      .addCode("return $L;", "$SERIALIZER")
      .build();
  }

  private static MethodSpec makeEnumerationDeserializerMethod(
    final ClassName className)
  {
    final var type =
      ParameterizedTypeName.get(
        ClassName.get(GWIOVariableDeserializeType.class),
        className
      );

    return MethodSpec.methodBuilder("deserializer")
      .addModifiers(PUBLIC)
      .addModifiers(STATIC)
      .returns(type)
      .addCode("return $L;", "$DESERIALIZER")
      .build();
  }

  private static FieldSpec makeEnumerationSerializerField(
    final ClassName className)
  {
    final var type =
      ParameterizedTypeName.get(
        ClassName.get(GWIOVariableSerializeType.class),
        className
      );

    final var field =
      FieldSpec.builder(type, "$SERIALIZER", STATIC, PRIVATE, FINAL);

    final var code = CodeBlock.builder();
    code.add(
      "\n  (b, x) -> { $T.uint8Serializer().serializeTo(b, x.toInt()); }\n",
      GWIOSerializers.class
    );

    field.initializer(code.build());
    return field.build();
  }

  private static FieldSpec makeEnumerationDeserializerField(
    final ClassName className)
  {
    final var type =
      ParameterizedTypeName.get(
        ClassName.get(GWIOVariableDeserializeType.class),
        className
      );

    final var field =
      FieldSpec.builder(type, "$DESERIALIZER", STATIC, PRIVATE, FINAL);

    final var code = CodeBlock.builder();
    code.add(
      "\n  b -> $T.ofInt($T.uint8Deserializer().deserializeFrom(b));",
      className,
      GWIOSerializers.class
    );

    field.initializer(code.build());
    return field.build();
  }

  private static MethodSpec makeEnumerationFirstMethod(
    final ClassName className,
    final Enumeration enumeration)
  {
    final var spec = MethodSpec.methodBuilder("first");
    spec.returns(className);
    spec.addModifiers(PUBLIC);
    spec.addModifiers(STATIC);

    final var case0 =
      enumeration.getCase().get(0);
    final var code =
      CodeBlock.of("return $L;", makeEnumerationConstant(case0.getName()));

    spec.addCode(code);
    return spec.build();
  }

  private static MethodSpec makeEnumerationLastMethod(
    final ClassName className,
    final Enumeration enumeration)
  {
    final var spec = MethodSpec.methodBuilder("last");
    spec.returns(className);
    spec.addModifiers(PUBLIC);
    spec.addModifiers(STATIC);

    final var cases = enumeration.getCase();
    final var case0 = cases.get(cases.size() - 1);
    final var code =
      CodeBlock.of("return $L;", makeEnumerationConstant(case0.getName()));

    spec.addCode(code);
    return spec.build();
  }

  private static MethodSpec makeEnumerationIntegerMethod(
    final Enumeration enumeration)
  {
    final var spec = MethodSpec.methodBuilder("toInt");
    spec.returns(int.class);
    spec.addAnnotation(Override.class);
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

  private static MethodSpec makeEnumerationOfIntegerMethod(
    final ClassName className,
    final Enumeration enumeration)
  {
    final var spec = MethodSpec.methodBuilder("ofInt");
    spec.returns(className);
    spec.addModifiers(PUBLIC);
    spec.addModifiers(STATIC);
    spec.addParameter(int.class, "x");

    var code = CodeBlock.builder();
    code = code.beginControlFlow("return switch (x)");

    for (final var caseV : enumeration.getCase()) {
      code = code.addStatement(
        "case $L -> $L",
        Integer.valueOf(caseV.getValue().intValueExact()),
        makeEnumerationConstant(caseV.getName())
      );
    }

    code = code.addStatement(
      "default -> throw new $T($T.format($S, x, $T.class))",
      IllegalArgumentException.class,
      String.class,
      "Cannot convert integer %d to a value of %s",
      className
    );

    code = code.endControlFlow();
    code = code.add(";");

    spec.addCode(code.build());
    return spec.build();
  }

  private static MethodSpec makeEnumerationLabelMethod(
    final Enumeration enumeration)
  {
    final var spec = MethodSpec.methodBuilder("label");
    spec.returns(String.class);
    spec.addAnnotation(Override.class);
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
    return "GW" + name.replace(" ", "")
      .replace(".", "")
      .replace("-", "")
      .replace(":", "")
      + "Value";
  }

  static ClassName makeEnumerationClassName(
    final GWDefinitionCompilerConfiguration configuration,
    final String name)
  {
    return ClassName.get(
      configuration.enumerationPackage(),
      makeEnumerationName(name)
    );
  }
}
