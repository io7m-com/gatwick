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
import com.io7m.gatwick.codegen.jaxb.ParameterBase;
import com.io7m.gatwick.codegen.jaxb.ParameterChainType;
import com.io7m.gatwick.codegen.jaxb.ParameterEnumeratedType;
import com.io7m.gatwick.codegen.jaxb.ParameterFractionalType;
import com.io7m.gatwick.codegen.jaxb.ParameterHighCutType;
import com.io7m.gatwick.codegen.jaxb.ParameterIntegerDirectType;
import com.io7m.gatwick.codegen.jaxb.ParameterIntegerMappedType;
import com.io7m.gatwick.codegen.jaxb.ParameterLowCutType;
import com.io7m.gatwick.codegen.jaxb.ParameterRate118AndOffType;
import com.io7m.gatwick.codegen.jaxb.ParameterRate118Type;
import com.io7m.gatwick.codegen.jaxb.ParameterRate318Type;
import com.io7m.gatwick.codegen.jaxb.ParameterStringType;
import com.io7m.gatwick.codegen.jaxb.Structure;
import com.io7m.gatwick.codegen.jaxb.StructureReferenceType;
import com.io7m.gatwick.device.api.GWDeviceException;
import com.io7m.gatwick.device.api.GWDeviceType;
import com.io7m.gatwick.iovar.GWIOAddressableType;
import com.io7m.gatwick.iovar.GWIORate118Milliseconds;
import com.io7m.gatwick.iovar.GWIORate118Note;
import com.io7m.gatwick.iovar.GWIORate118Type;
import com.io7m.gatwick.iovar.GWIORate119Note;
import com.io7m.gatwick.iovar.GWIORate119Off;
import com.io7m.gatwick.iovar.GWIORate119Type;
import com.io7m.gatwick.iovar.GWIORate318Milliseconds;
import com.io7m.gatwick.iovar.GWIORate318Note;
import com.io7m.gatwick.iovar.GWIORate318Type;
import com.io7m.gatwick.iovar.GWIOReadableType;
import com.io7m.gatwick.iovar.GWIOSerializers;
import com.io7m.gatwick.iovar.GWIOVariable;
import com.io7m.gatwick.iovar.GWIOVariableContainerType;
import com.io7m.gatwick.iovar.GWIOVariableInformation;
import com.io7m.gatwick.iovar.GWIOVariableType;
import com.io7m.jattribute.core.Attributes;
import com.io7m.jodist.ClassName;
import com.io7m.jodist.CodeBlock;
import com.io7m.jodist.FieldSpec;
import com.io7m.jodist.JavaFile;
import com.io7m.jodist.MethodSpec;
import com.io7m.jodist.ParameterizedTypeName;
import com.io7m.jodist.TypeSpec;
import com.io7m.jodist.TypeVariableName;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Functions to generate structures.
 */

public final class GWStructures
{
  private static final String HIGH_CUT = "HighCut";
  private static final String LOW_CUT = "LowCut";
  private static final Pattern UNDERSCORE_LOWERCASE =
    Pattern.compile("_([a-z])");
  private static final Pattern UNDERSCORE_NUMBER =
    Pattern.compile("_([0-9])");

  private final HashSet<Path> files;
  private final GWDefinitionCompilerConfiguration configuration;
  private final Map<String, Structure> structures;

  /**
   * Functions to generate structures.
   *
   * @param inConfiguration The compiler config
   * @param inStructures    The structures
   */

  public GWStructures(
    final GWDefinitionCompilerConfiguration inConfiguration,
    final Map<String, Structure> inStructures)
  {
    this.configuration =
      Objects.requireNonNull(inConfiguration, "configuration");
    this.structures =
      Objects.requireNonNull(inStructures, "structures");
    this.files =
      new HashSet<Path>();
  }

  /**
   * Generate classes.
   *
   * @return The generated files
   *
   * @throws IOException On errors
   */

  public Set<Path> compile()
    throws IOException
  {
    for (final var s : this.structures.values()) {
      this.compileOne(s);
    }

    return Set.copyOf(this.files);
  }

  private void compileOne(
    final Structure structure)
    throws IOException
  {
    final var className =
      ClassName.get(
        this.configuration.structurePackage(),
        this.structNameOf(structure)
      );

    final var spec = TypeSpec.classBuilder(className);
    spec.addSuperinterface(GWIOVariableContainerType.class);
    spec.addSuperinterface(GWIOReadableType.class);
    spec.addField(int.class, "baseAddress", PRIVATE, FINAL);
    spec.addModifiers(PUBLIC);
    spec.addModifiers(FINAL);

    if (structure.getJavaInterface() != null) {
      spec.addSuperinterface(
        ClassName.get(
          this.configuration.apiPackage(), structure.getJavaInterface()
        )
      );
    }

    final var constructor =
      MethodSpec.constructorBuilder()
        .addModifiers(PUBLIC)
        .addParameter(GWDeviceType.class, "inDevice", FINAL)
        .addParameter(Attributes.class, "inAttributes", FINAL)
        .addParameter(int.class, "inBaseAddress", FINAL)
        .addCode("this.baseAddress = inBaseAddress;\n");

    final var parameters =
      structure.getParameterChainOrParameterEnumeratedOrParameterFractional();

    for (final var p : parameters) {
      spec.addField(this.createField(p));
      constructor.addCode(this.createFieldInitializer(structure, p));
    }
    spec.addMethod(constructor.build());

    spec.addSuperinterface(GWIOAddressableType.class);
    spec.addMethod(
      MethodSpec.methodBuilder("address")
        .addModifiers(PUBLIC)
        .addAnnotation(Override.class)
        .returns(int.class)
        .addCode("return this.baseAddress;")
        .build()
    );

    for (final var p : parameters) {
      spec.addMethod(this.createGetter(structure, p));
    }

    spec.addMethod(createReadFromDeviceMethod(parameters));
    spec.addMethod(createVariablesMethod(parameters));

    final var javaFile =
      JavaFile.builder(this.configuration.structurePackage(), spec.build())
        .build();

    this.files.add(javaFile.writeToPath(this.configuration.outputDirectory()));
  }

  private MethodSpec createGetter(
    final Structure structure,
    final ParameterBase p)
  {
    try {
      final var field =
        this.createField(p);

      final var method =
        MethodSpec.methodBuilder(methodNameFor(p))
          .addModifiers(PUBLIC)
          .returns(field.type);

      method.addCode("return this.$L;", field.name);
      return method.build();
    } catch (final Exception e) {
      throw new IllegalArgumentException(
        "Encountered an error processing %s: %s: "
          .formatted(structure.getName(), p),
        e
      );
    }
  }

  private static String methodNameFor(
    final ParameterBase p)
  {
    return civilizeName(fieldNameFor(p));
  }

  private static String civilizeName(
    final String name)
  {
    var current = name;
    current =
      current.replace("f_", "");

    {
      final var matcher =
        UNDERSCORE_LOWERCASE.matcher(current);

      current = matcher.replaceAll(matchResult -> {
        return matchResult.group()
          .replace("_", "")
          .toUpperCase();
      });
    }

    {
      final var matcher =
        UNDERSCORE_NUMBER.matcher(current);

      current = matcher.replaceAll(matchResult -> {
        return matchResult.group()
          .replace("_", "");
      });
    }

    return current;
  }

  private static MethodSpec createVariablesMethod(
    final List<ParameterBase> parameters)
  {
    final var varType =
      ParameterizedTypeName.get(
        ClassName.get(GWIOVariableType.class),
        TypeVariableName.get("?")
      );

    final var listType =
      ParameterizedTypeName.get(
        ClassName.get(List.class),
        varType
      );

    final var method =
      MethodSpec.methodBuilder("variables")
        .addModifiers(PUBLIC)
        .addAnnotation(Override.class)
        .returns(listType);

    method.addCode(
      "return $T.of($L);",
      List.class,
      parameters.stream()
        .filter(p -> !(p instanceof StructureReferenceType))
        .map(GWStructures::fieldNameFor)
        .map("this.%s"::formatted)
        .collect(Collectors.joining(","))
    );

    return method.build();
  }

  private static MethodSpec createReadFromDeviceMethod(
    final List<ParameterBase> parameters)
  {
    final var method =
      MethodSpec.methodBuilder("readFromDevice")
        .addModifiers(PUBLIC)
        .addAnnotation(Override.class)
        .addException(InterruptedException.class)
        .addException(GWDeviceException.class);

    for (final var p : parameters) {
      method.addCode(
        CodeBlock.of("this.$L.readFromDevice();\n", fieldNameFor(p))
      );
    }

    return method.build();
  }

  private static String fieldNameFor(
    final ParameterBase parameter)
  {
    final var javaName = parameter.getJavaNameOverride();
    if (javaName != null) {
      return javaName;
    }
    return transformFieldNameFromBasicName(parameter.getName());
  }

  private CodeBlock createFieldInitializer(
    final Structure structure,
    final ParameterBase parameter)
  {
    if (parameter instanceof ParameterStringType p) {
      return createFieldStringInitializer(structure, p);
    } else if (parameter instanceof ParameterIntegerMappedType p) {
      return createFieldIntegerMappedInitializer(structure, p);
    } else if (parameter instanceof ParameterIntegerDirectType p) {
      return createFieldIntegerDirectInitializer(structure, p);
    } else if (parameter instanceof ParameterFractionalType p) {
      return createFieldFractionalInitializer(structure, p);
    } else if (parameter instanceof StructureReferenceType p) {
      return this.createFieldStructureReferenceInitializer(structure, p);
    } else if (parameter instanceof ParameterRate318Type p) {
      return createFieldRate318Initializer(structure, p);
    } else if (parameter instanceof ParameterRate118Type p) {
      return createFieldRate118Initializer(structure, p);
    } else if (parameter instanceof ParameterRate118AndOffType p) {
      return createFieldRate118AndOffInitializer(structure, p);
    } else if (parameter instanceof ParameterLowCutType p) {
      return this.createFieldLowCutInitializer(structure, p);
    } else if (parameter instanceof ParameterHighCutType p) {
      return this.createFieldHighCutInitializer(structure, p);
    } else if (parameter instanceof ParameterEnumeratedType p) {
      return this.createFieldEnumeratedInitializer(structure, p);
    } else if (parameter instanceof ParameterChainType p) {
      return createFieldChainInitializer(structure, p);
    }

    throw new IllegalStateException(
      "Unrecognized parameter type: %s".formatted(parameter)
    );
  }

  private FieldSpec createField(
    final ParameterBase parameter)
  {
    if (parameter instanceof ParameterStringType p) {
      return createFieldString(p);
    } else if (parameter instanceof ParameterIntegerMappedType p) {
      return createFieldIntegerMapped(p);
    } else if (parameter instanceof ParameterIntegerDirectType p) {
      return createFieldIntegerDirect(p);
    } else if (parameter instanceof ParameterFractionalType p) {
      return createFieldFractional(p);
    } else if (parameter instanceof StructureReferenceType p) {
      return this.createFieldStructureReference(p);
    } else if (parameter instanceof ParameterRate318Type p) {
      return createFieldRate318(p);
    } else if (parameter instanceof ParameterRate118Type p) {
      return createFieldRate118(p);
    } else if (parameter instanceof ParameterRate118AndOffType p) {
      return createFieldRate118AndOff(p);
    } else if (parameter instanceof ParameterLowCutType p) {
      return this.createFieldLowCut(p);
    } else if (parameter instanceof ParameterHighCutType p) {
      return this.createFieldHighCut(p);
    } else if (parameter instanceof ParameterEnumeratedType p) {
      return this.createFieldEnumerated(p);
    } else if (parameter instanceof ParameterChainType p) {
      return createFieldChain(p);
    }

    throw new IllegalStateException(
      "Unrecognized parameter type: %s".formatted(parameter)
    );
  }

  private static FieldSpec createFieldChain(
    final ParameterChainType p)
  {
    final var varType =
      ParameterizedTypeName.get(
        ClassName.get(GWIOVariableType.class),
        ClassName.get(ByteBuffer.class)
      );

    return FieldSpec.builder(varType, fieldNameFor(p))
      .addModifiers(PUBLIC)
      .addModifiers(FINAL)
      .build();
  }

  private FieldSpec createFieldEnumerated(
    final ParameterEnumeratedType p)
  {
    final var enumType =
      GWEnumerations.makeEnumerationClassName(this.configuration, p.getType());

    final var varType =
      ParameterizedTypeName.get(
        ClassName.get(GWIOVariableType.class),
        enumType
      );

    return FieldSpec.builder(varType, fieldNameFor(p))
      .addModifiers(PUBLIC)
      .addModifiers(FINAL)
      .build();
  }

  private FieldSpec createFieldHighCut(
    final ParameterHighCutType p)
  {
    final var enumType =
      GWEnumerations.makeEnumerationClassName(
        this.configuration, HIGH_CUT);

    final var varType =
      ParameterizedTypeName.get(
        ClassName.get(GWIOVariableType.class),
        enumType
      );

    return FieldSpec.builder(varType, fieldNameFor(p))
      .addModifiers(PUBLIC)
      .addModifiers(FINAL)
      .build();
  }

  private FieldSpec createFieldLowCut(
    final ParameterLowCutType p)
  {
    final var enumType =
      GWEnumerations.makeEnumerationClassName(
        this.configuration, LOW_CUT);

    final var varType =
      ParameterizedTypeName.get(
        ClassName.get(GWIOVariableType.class),
        enumType
      );

    return FieldSpec.builder(varType, fieldNameFor(p))
      .addModifiers(PUBLIC)
      .addModifiers(FINAL)
      .build();
  }

  private static FieldSpec createFieldRate118AndOff(
    final ParameterRate118AndOffType p)
  {
    final var varType =
      ParameterizedTypeName.get(
        ClassName.get(GWIOVariableType.class),
        ClassName.get(GWIORate119Type.class)
      );

    return FieldSpec.builder(varType, fieldNameFor(p))
      .addModifiers(PUBLIC)
      .addModifiers(FINAL)
      .build();
  }

  private static FieldSpec createFieldRate118(
    final ParameterRate118Type p)
  {
    final var varType =
      ParameterizedTypeName.get(
        ClassName.get(GWIOVariableType.class),
        ClassName.get(GWIORate118Type.class)
      );

    return FieldSpec.builder(varType, fieldNameFor(p))
      .addModifiers(PUBLIC)
      .addModifiers(FINAL)
      .build();
  }

  private static FieldSpec createFieldRate318(
    final ParameterRate318Type p)
  {
    final var varType =
      ParameterizedTypeName.get(
        ClassName.get(GWIOVariableType.class),
        ClassName.get(GWIORate318Type.class)
      );

    return FieldSpec.builder(varType, fieldNameFor(p))
      .addModifiers(PUBLIC)
      .addModifiers(FINAL)
      .build();
  }

  private FieldSpec createFieldStructureReference(
    final StructureReferenceType p)
  {
    final var targetStruct =
      this.structures.get(p.getType());

    final var typeName =
      ClassName.get(
        this.configuration.structurePackage(),
        this.structNameOf(targetStruct)
      );

    return FieldSpec.builder(typeName, fieldNameFor(p))
      .addModifiers(PUBLIC)
      .addModifiers(FINAL)
      .build();
  }

  private static FieldSpec createFieldIntegerDirect(
    final ParameterIntegerDirectType p)
  {
    final var varType =
      ParameterizedTypeName.get(
        ClassName.get(GWIOVariableType.class),
        ClassName.get(Integer.class)
      );

    return FieldSpec.builder(varType, fieldNameFor(p))
      .addModifiers(PUBLIC)
      .addModifiers(FINAL)
      .build();
  }

  private static FieldSpec createFieldIntegerMapped(
    final ParameterIntegerMappedType p)
  {
    final var varType =
      ParameterizedTypeName.get(
        ClassName.get(GWIOVariableType.class),
        ClassName.get(Integer.class)
      );

    return FieldSpec.builder(varType, fieldNameFor(p))
      .addModifiers(PUBLIC)
      .addModifiers(FINAL)
      .build();
  }

  private static FieldSpec createFieldFractional(
    final ParameterFractionalType p)
  {
    final var varType =
      ParameterizedTypeName.get(
        ClassName.get(GWIOVariableType.class),
        ClassName.get(Double.class)
      );

    return FieldSpec.builder(varType, fieldNameFor(p))
      .addModifiers(PUBLIC)
      .addModifiers(FINAL)
      .build();
  }

  private static FieldSpec createFieldString(
    final ParameterStringType p)
  {
    final var varType =
      ParameterizedTypeName.get(
        ClassName.get(GWIOVariableType.class),
        ClassName.get(String.class)
      );

    return FieldSpec.builder(varType, fieldNameFor(p))
      .addModifiers(PUBLIC)
      .addModifiers(FINAL)
      .build();
  }

  private CodeBlock createFieldEnumeratedInitializer(
    final Structure structure,
    final ParameterEnumeratedType p)
  {
    final var enumType =
      GWEnumerations.makeEnumerationClassName(this.configuration, p.getType());

    final var offset =
      Long.toUnsignedString(GWHexIntegers.parseHex(p.getOffset()), 16);

    final var code = CodeBlock.builder();
    code.add("// $L.$L\n", structure.getName(), p.getName());
    code.add(
      "this.$L = $T.create(\n", fieldNameFor(p), GWIOVariable.class);
    code.add("  inDevice,\n");
    code.add("  inAttributes,\n");
    code.add("  $T.info().serializer(),\n", enumType);
    code.add("  $T.info().deserializer(),\n", enumType);
    code.add("  $T.info().serializeSize(),\n", enumType);

    code.add(
      "  new $T<>($S, $T.class, $T.info().first(), $T.info().first(), $T.info().last()),\n",
      GWIOVariableInformation.class,
      p.getName(),
      enumType,
      enumType,
      enumType,
      enumType
    );

    code.add("  baseAddress + 0x$L\n", offset);
    code.add(");\n");
    return code.build();
  }

  private CodeBlock createFieldHighCutInitializer(
    final Structure structure,
    final ParameterHighCutType p)
  {
    final var enumType =
      GWEnumerations.makeEnumerationClassName(this.configuration, HIGH_CUT);

    final var offset =
      Long.toUnsignedString(GWHexIntegers.parseHex(p.getOffset()), 16);

    final var code = CodeBlock.builder();
    code.add("// $L.$L\n", structure.getName(), p.getName());
    code.add(
      "this.$L = $T.create(\n", fieldNameFor(p), GWIOVariable.class);
    code.add("  inDevice,\n");
    code.add("  inAttributes,\n");
    code.add("  $T.info().serializer(),\n", enumType);
    code.add("  $T.info().deserializer(),\n", enumType);
    code.add("  $T.info().serializeSize(),\n", enumType);

    code.add(
      "  new $T<>($S, $T.class, $T.info().first(), $T.info().first(), $T.info().last()),\n",
      GWIOVariableInformation.class,
      p.getName(),
      enumType,
      enumType,
      enumType,
      enumType
    );

    code.add("  baseAddress + 0x$L\n", offset);
    code.add(");\n");
    return code.build();
  }

  private CodeBlock createFieldLowCutInitializer(
    final Structure structure,
    final ParameterLowCutType p)
  {
    final var enumType =
      GWEnumerations.makeEnumerationClassName(this.configuration, LOW_CUT);

    final var offset =
      Long.toUnsignedString(GWHexIntegers.parseHex(p.getOffset()), 16);

    final var code = CodeBlock.builder();
    code.add("// $L.$L\n", structure.getName(), p.getName());
    code.add(
      "this.$L = $T.create(\n", fieldNameFor(p), GWIOVariable.class);
    code.add("  inDevice,\n");
    code.add("  inAttributes,\n");
    code.add("  $T.info().serializer(),\n", enumType);
    code.add("  $T.info().deserializer(),\n", enumType);
    code.add("  $T.info().serializeSize(),\n", enumType);

    code.add(
      "  new $T<>($S, $T.class, $T.info().first(), $T.info().first(), $T.info().last()),\n",
      GWIOVariableInformation.class,
      p.getName(),
      enumType,
      enumType,
      enumType,
      enumType
    );

    code.add("  baseAddress + 0x$L\n", offset);
    code.add(");\n");
    return code.build();
  }

  private static CodeBlock createFieldRate118AndOffInitializer(
    final Structure structure,
    final ParameterRate118AndOffType p)
  {
    final var serializers =
      ClassName.get(GWIOSerializers.class);
    final var offset =
      Long.toUnsignedString(GWHexIntegers.parseHex(p.getOffset()), 16);

    final var code = CodeBlock.builder();
    code.add("// $L.$L\n", structure.getName(), p.getName());
    code.add(
      "this.$L = $T.create(\n", fieldNameFor(p), GWIOVariable.class);
    code.add("  inDevice,\n");
    code.add("  inAttributes,\n");
    code.add("  $T.rate119Serializer(),\n", serializers);
    code.add("  $T.rate119Deserializer(),\n", serializers);
    code.add("  1,\n", serializers);

    code.add(
      "  new $T<>($S, $T.class, $T.$L, $T.$L, $T.last()),\n",
      GWIOVariableInformation.class,
      p.getName(),
      GWIORate119Type.class,
      GWIORate119Off.class,
      GWIORate119Off.OFF,
      GWIORate119Off.class,
      GWIORate119Off.OFF,
      GWIORate119Note.class
    );

    code.add("  baseAddress + 0x$L\n", offset);
    code.add(");\n");
    return code.build();
  }

  private static CodeBlock createFieldRate118Initializer(
    final Structure structure,
    final ParameterRate118Type p)
  {
    final var serializers =
      ClassName.get(GWIOSerializers.class);
    final var offset =
      Long.toUnsignedString(GWHexIntegers.parseHex(p.getOffset()), 16);

    final var code = CodeBlock.builder();
    code.add("// $L.$L\n", structure.getName(), p.getName());
    code.add(
      "this.$L = $T.create(\n", fieldNameFor(p), GWIOVariable.class);
    code.add("  inDevice,\n");
    code.add("  inAttributes,\n");
    code.add("  $T.rate118Serializer(),\n", serializers);
    code.add("  $T.rate118Deserializer(),\n", serializers);
    code.add("  1,\n", serializers);

    code.add(
      "  new $T<>($S, $T.class, $T.$L, new $T(0), $T.last()),\n",
      GWIOVariableInformation.class,
      p.getName(),
      GWIORate118Type.class,
      GWIORate118Note.class,
      GWIORate118Note.RATE_8TH_NOTE,
      GWIORate118Milliseconds.class,
      GWIORate118Note.class
    );

    code.add("  baseAddress + 0x$L\n", offset);
    code.add(");\n");
    return code.build();
  }

  private static CodeBlock createFieldRate318Initializer(
    final Structure structure,
    final ParameterRate318Type p)
  {
    final var serializers =
      ClassName.get(GWIOSerializers.class);
    final var offset =
      Long.toUnsignedString(GWHexIntegers.parseHex(p.getOffset()), 16);

    final var code = CodeBlock.builder();
    code.add("// $L.$L\n", structure.getName(), p.getName());
    code.add(
      "this.$L = $T.create(\n", fieldNameFor(p), GWIOVariable.class);
    code.add("  inDevice,\n");
    code.add("  inAttributes,\n");
    code.add("  $T.rate318Serializer(),\n", serializers);
    code.add("  $T.rate318Deserializer(),\n", serializers);
    code.add("  2,\n", serializers);

    code.add(
      "  new $T<>($S, $T.class, $T.$L, new $T(0), $T.last()),\n",
      GWIOVariableInformation.class,
      p.getName(),
      GWIORate318Type.class,
      GWIORate318Note.class,
      GWIORate318Note.RATE_8TH_NOTE,
      GWIORate318Milliseconds.class,
      GWIORate318Note.class
    );

    code.add("  baseAddress + 0x$L\n", offset);
    code.add(");\n");
    return code.build();
  }

  private static CodeBlock createFieldChainInitializer(
    final Structure structure,
    final ParameterChainType p)
  {
    final var serializers =
      ClassName.get(GWIOSerializers.class);
    final var offset =
      Long.toUnsignedString(GWHexIntegers.parseHex(p.getOffset()), 16);

    final var code = CodeBlock.builder();
    code.add("// $L.$L\n", structure.getName(), p.getName());
    code.add(
      "this.$L = $T.create(\n", fieldNameFor(p), GWIOVariable.class);
    code.add("  inDevice,\n");
    code.add("  inAttributes,\n");
    code.add("  $T.rawSerializer(),\n", serializers);
    code.add("  $T.rawDeserializer(),\n", serializers);

    final var size = GWParameterSizes.sizeOf(p);
    code.add("  $L,\n", size);

    code.add(
      "  new $T<>($S, $T.class, $T.allocate($L), $T.allocate($L), $T.allocate($L)),\n",
      GWIOVariableInformation.class,
      p.getName(),
      ByteBuffer.class,
      ByteBuffer.class,
      size,
      ByteBuffer.class,
      size,
      ByteBuffer.class,
      size
    );

    code.add("  baseAddress + 0x$L\n", offset);
    code.add(");\n");
    return code.build();
  }

  private CodeBlock createFieldStructureReferenceInitializer(
    final Structure structure,
    final StructureReferenceType p)
  {
    final var targetStruct =
      this.structures.get(p.getType());

    final var typeName =
      ClassName.get(
        this.configuration.structurePackage(),
        this.structNameOf(targetStruct)
      );

    final var offset =
      Long.toUnsignedString(
        GWHexIntegers.parseHex(p.getOffset()),
        16);

    final var code = CodeBlock.builder();
    code.add("// $L.$L\n", structure.getName(), p.getName());
    code.add(
      "this.$L = new $T(inDevice, inAttributes, inBaseAddress + 0x$L);\n",
      fieldNameFor(p),
      typeName,
      offset
    );
    return code.build();
  }

  private record GWSerializers(
    String serializeMethod,
    String deserializeMethod,
    int size)
  {

  }

  private static GWSerializers baseSerializersForMaxValue(
    final int maxValue)
  {
    String serializerMethod = "uint8Serializer";
    String deserializerMethod = "uint8Deserializer";
    int serializeSize = 1;

    if (maxValue >= 127) {
      serializerMethod = "uint8As16Serializer";
      deserializerMethod = "uint8As16Deserializer";
      serializeSize = 2;
    }
    if (maxValue >= 255) {
      serializerMethod = "uint16As32Serializer";
      deserializerMethod = "uint16As32Deserializer";
      serializeSize = 4;
    }

    return new GWSerializers(
      serializerMethod,
      deserializerMethod,
      serializeSize
    );
  }

  private static CodeBlock createFieldIntegerDirectInitializer(
    final Structure structure,
    final ParameterIntegerDirectType p)
  {
    final var offset =
      Long.toUnsignedString(GWHexIntegers.parseHex(p.getOffset()), 16);
    final var min =
      p.getMinInclusive();
    final var max =
      p.getMaxInclusive();
    final var def =
      p.getDefault();

    final var code = CodeBlock.builder();
    code.add("// $L.$L\n", structure.getName(), p.getName());
    code.add(
      "this.$L = $T.create(\n", fieldNameFor(p), GWIOVariable.class);
    code.add("  inDevice,\n");
    code.add("  inAttributes,\n");

    final GWSerializers serializers =
      baseSerializersForMaxValue(max.intValueExact());

    code.add(
      "  $T.$L(),\n",
      GWIOSerializers.class,
      serializers.serializeMethod
    );
    code.add(
      "  $T.$L(),\n",
      GWIOSerializers.class,
      serializers.deserializeMethod
    );
    code.add("  $L,\n", Integer.valueOf(serializers.size));
    code.add(
      "  new $T<>($S, $T.class, $L, $L, $L),\n",
      GWIOVariableInformation.class,
      p.getName(),
      Integer.class,
      Integer.valueOf(def.intValueExact()),
      Integer.valueOf(min.intValueExact()),
      Integer.valueOf(max.intValueExact())
    );

    code.add("  inBaseAddress + 0x$L\n", offset);
    code.add(");\n");
    return code.build();
  }

  private static CodeBlock createFieldFractionalInitializer(
    final Structure structure,
    final ParameterFractionalType p)
  {
    final var offset =
      Long.toUnsignedString(GWHexIntegers.parseHex(p.getOffset()), 16);
    final var min =
      p.getMinInclusive();
    final var max =
      p.getMaxInclusive();
    final var physMin =
      p.getPhysicalMinInclusive();
    final var physMax =
      p.getPhysicalMaxInclusive();
    final var def =
      p.getDefault();

    final var code = CodeBlock.builder();
    code.add("// $L.$L\n", structure.getName(), p.getName());
    code.add(
      "this.$L = $T.create(\n", fieldNameFor(p), GWIOVariable.class);
    code.add("  inDevice,\n");
    code.add("  inAttributes,\n");

    final var baseSerializers =
      baseSerializersForMaxValue(physMax.intValueExact());

    code.add(
      "  $T.$L($T.$L(), $L, $L, $L, $L),\n",
      GWIOSerializers.class,
      "fractionalSerializer",
      GWIOSerializers.class,
      baseSerializers.serializeMethod,
      Double.valueOf(min),
      Double.valueOf(max),
      Integer.valueOf(physMin.intValueExact()),
      Integer.valueOf(physMax.intValueExact())
    );
    code.add(
      "  $T.$L($T.$L(), $L, $L, $L, $L),\n",
      GWIOSerializers.class,
      "fractionalDeserializer",
      GWIOSerializers.class,
      baseSerializers.deserializeMethod,
      Double.valueOf(min),
      Double.valueOf(max),
      Integer.valueOf(physMin.intValueExact()),
      Integer.valueOf(physMax.intValueExact())
    );
    code.add("  $L,\n", Integer.valueOf(baseSerializers.size));

    code.add(
      "  new $T<>($S, $T.class, $L, $L, $L),\n",
      GWIOVariableInformation.class,
      p.getName(),
      Double.class,
      Double.valueOf(def),
      Double.valueOf(min),
      Double.valueOf(max)
    );

    code.add("  inBaseAddress + 0x$L\n", offset);
    code.add(");\n");
    return code.build();
  }

  private static CodeBlock createFieldIntegerMappedInitializer(
    final Structure structure,
    final ParameterIntegerMappedType p)
  {
    final var offset =
      Long.toUnsignedString(GWHexIntegers.parseHex(p.getOffset()), 16);
    final var min =
      p.getMinInclusive();
    final var max =
      p.getMaxInclusive();
    final var physMin =
      p.getPhysicalMinInclusive();
    final var physMax =
      p.getPhysicalMaxInclusive();
    final var def =
      p.getDefault();

    final var code = CodeBlock.builder();
    code.add("// $L.$L\n", structure.getName(), p.getName());
    code.add(
      "this.$L = $T.create(\n", fieldNameFor(p), GWIOVariable.class);
    code.add("  inDevice,\n");
    code.add("  inAttributes,\n");

    final var baseSerializers =
      baseSerializersForMaxValue(physMax.intValueExact());

    code.add(
      "  $T.$L($T.$L(), $L, $L, $L, $L),\n",
      GWIOSerializers.class,
      "integerMappedSerializer",
      GWIOSerializers.class,
      baseSerializers.serializeMethod,
      Integer.valueOf(min.intValueExact()),
      Integer.valueOf(max.intValueExact()),
      Integer.valueOf(physMin.intValueExact()),
      Integer.valueOf(physMax.intValueExact())
    );
    code.add(
      "  $T.$L($T.$L(), $L, $L, $L, $L),\n",
      GWIOSerializers.class,
      "integerMappedDeserializer",
      GWIOSerializers.class,
      baseSerializers.deserializeMethod,
      Integer.valueOf(min.intValueExact()),
      Integer.valueOf(max.intValueExact()),
      Integer.valueOf(physMin.intValueExact()),
      Integer.valueOf(physMax.intValueExact())
    );
    code.add("  $L,\n", Integer.valueOf(baseSerializers.size));

    code.add(
      "  new $T<>($S, $T.class, $L, $L, $L),\n",
      GWIOVariableInformation.class,
      p.getName(),
      Integer.class,
      Integer.valueOf(def.intValueExact()),
      Integer.valueOf(min.intValueExact()),
      Integer.valueOf(max.intValueExact())
    );

    code.add("  inBaseAddress + 0x$L\n", offset);
    code.add(");\n");
    return code.build();
  }

  private static CodeBlock createFieldStringInitializer(
    final Structure structure,
    final ParameterStringType p)
  {
    final var offset =
      Long.toUnsignedString(GWHexIntegers.parseHex(p.getOffset()), 16);

    final var code = CodeBlock.builder();
    code.add("// $L.$L\n", structure.getName(), p.getName());
    code.add(
      "this.$L = $T.create(\n", fieldNameFor(p), GWIOVariable.class);
    code.add("  inDevice,\n");
    code.add("  inAttributes,\n");
    code.add("  $T.stringSerializer(),\n", GWIOSerializers.class);
    code.add("  $T.stringDeserializer(),\n", GWIOSerializers.class);
    code.add("  $L,\n", Long.valueOf(p.getLength()));

    code.add(
      "  new $T<>($S, $T.class, $S, $S, $S),\n",
      GWIOVariableInformation.class,
      p.getName(),
      String.class,
      "",
      "",
      "~".repeat(Math.toIntExact(p.getLength()))
    );

    code.add("  inBaseAddress + 0x$L\n", offset);
    code.add(");\n");
    return code.build();
  }

  private String structNameOf(
    final Structure structure)
  {
    return "Struct" + structure.getName()
      .replace("-", "_");
  }

  private static String transformFieldNameFromBasicName(
    final String name)
  {
    return "f_" + name.replace(" ", "_")
      .replace("#", "_SHARP")
      .replace("&", "_")
      .replace("(", "_")
      .replace(")", "_")
      .replace("/", "_")
      .replace(".", "$")
      .replace("-", "_")
      .replace(":", "_")
      .toLowerCase();
  }
}
