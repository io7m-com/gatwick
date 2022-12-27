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
import com.io7m.gatwick.codegen.jaxb.ParameterChain;
import com.io7m.gatwick.codegen.jaxb.ParameterEnumerated;
import com.io7m.gatwick.codegen.jaxb.ParameterFractional;
import com.io7m.gatwick.codegen.jaxb.ParameterHighCut;
import com.io7m.gatwick.codegen.jaxb.ParameterIntegerDirect;
import com.io7m.gatwick.codegen.jaxb.ParameterIntegerMapped;
import com.io7m.gatwick.codegen.jaxb.ParameterLowCut;
import com.io7m.gatwick.codegen.jaxb.ParameterRate118;
import com.io7m.gatwick.codegen.jaxb.ParameterRate118AndOff;
import com.io7m.gatwick.codegen.jaxb.ParameterRate318;
import com.io7m.gatwick.codegen.jaxb.ParameterString;
import com.io7m.gatwick.codegen.jaxb.Structure;
import com.io7m.gatwick.codegen.jaxb.StructureReference;
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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
        this.structNameOf(structure));

    final var spec =
      TypeSpec.classBuilder(className);

    spec.addSuperinterface(GWIOReadableType.class);
    spec.addField(int.class, "baseAddress", PRIVATE, FINAL);
    spec.addModifiers(PUBLIC);
    spec.addModifiers(FINAL);

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

    spec.addMethod(createReadFromDeviceMethod(parameters));

    final var javaFile =
      JavaFile.builder(this.configuration.structurePackage(), spec.build())
        .build();

    this.files.add(javaFile.writeToPath(this.configuration.outputDirectory()));
  }

  private static MethodSpec createReadFromDeviceMethod(
    final List<Object> parameters)
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
    final ParameterString parameter)
  {
    return fieldName(parameter.getName());
  }

  private static String fieldNameFor(
    final ParameterIntegerDirect parameter)
  {
    return fieldName(parameter.getName());
  }

  private static String fieldNameFor(
    final ParameterIntegerMapped parameter)
  {
    return fieldName(parameter.getName());
  }

  private static String fieldNameFor(
    final ParameterFractional parameter)
  {
    return fieldName(parameter.getName());
  }

  private static String fieldNameFor(
    final StructureReference parameter)
  {
    return fieldName(parameter.getName());
  }

  private static String fieldNameFor(
    final ParameterRate318 parameter)
  {
    return fieldName(parameter.getName());
  }

  private static String fieldNameFor(
    final ParameterRate118 parameter)
  {
    return fieldName(parameter.getName());
  }

  private static String fieldNameFor(
    final ParameterRate118AndOff parameter)
  {
    return fieldName(parameter.getName());
  }

  private static String fieldNameFor(
    final ParameterLowCut parameter)
  {
    return fieldName(parameter.getName());
  }

  private static String fieldNameFor(
    final ParameterHighCut parameter)
  {
    return fieldName(parameter.getName());
  }

  private static String fieldNameFor(
    final ParameterEnumerated parameter)
  {
    return fieldName(parameter.getName());
  }

  private static String fieldNameFor(
    final ParameterChain parameter)
  {
    return fieldName(parameter.getName());
  }

  private static String fieldNameFor(
    final Object parameter)
  {
    if (parameter instanceof ParameterString p) {
      return fieldNameFor(p);
    } else if (parameter instanceof ParameterIntegerMapped p) {
      return fieldNameFor(p);
    } else if (parameter instanceof ParameterIntegerDirect p) {
      return fieldNameFor(p);
    } else if (parameter instanceof ParameterFractional p) {
      return fieldNameFor(p);
    } else if (parameter instanceof StructureReference p) {
      return fieldNameFor(p);
    } else if (parameter instanceof ParameterRate318 p) {
      return fieldNameFor(p);
    } else if (parameter instanceof ParameterRate118 p) {
      return fieldNameFor(p);
    } else if (parameter instanceof ParameterRate118AndOff p) {
      return fieldNameFor(p);
    } else if (parameter instanceof ParameterLowCut p) {
      return fieldNameFor(p);
    } else if (parameter instanceof ParameterHighCut p) {
      return fieldNameFor(p);
    } else if (parameter instanceof ParameterEnumerated p) {
      return fieldNameFor(p);
    } else if (parameter instanceof ParameterChain p) {
      return fieldNameFor(p);
    }

    throw new IllegalStateException(
      "Unrecognized parameter type: %s".formatted(parameter)
    );
  }

  private CodeBlock createFieldInitializer(
    final Structure structure,
    final Object parameter)
  {
    if (parameter instanceof ParameterString p) {
      return createFieldStringInitializer(structure, p);
    } else if (parameter instanceof ParameterIntegerMapped p) {
      return createFieldIntegerMappedInitializer(structure, p);
    } else if (parameter instanceof ParameterIntegerDirect p) {
      return createFieldIntegerDirectInitializer(structure, p);
    } else if (parameter instanceof ParameterFractional p) {
      return createFieldFractionalInitializer(structure, p);
    } else if (parameter instanceof StructureReference p) {
      return this.createFieldStructureReferenceInitializer(structure, p);
    } else if (parameter instanceof ParameterRate318 p) {
      return createFieldRate318Initializer(structure, p);
    } else if (parameter instanceof ParameterRate118 p) {
      return createFieldRate118Initializer(structure, p);
    } else if (parameter instanceof ParameterRate118AndOff p) {
      return createFieldRate118AndOffInitializer(structure, p);
    } else if (parameter instanceof ParameterLowCut p) {
      return this.createFieldLowCutInitializer(structure, p);
    } else if (parameter instanceof ParameterHighCut p) {
      return this.createFieldHighCutInitializer(structure, p);
    } else if (parameter instanceof ParameterEnumerated p) {
      return this.createFieldEnumeratedInitializer(structure, p);
    } else if (parameter instanceof ParameterChain p) {
      return createFieldChainInitializer(structure, p);
    }

    throw new IllegalStateException(
      "Unrecognized parameter type: %s".formatted(parameter)
    );
  }

  private FieldSpec createField(
    final Object parameter)
  {
    if (parameter instanceof ParameterString p) {
      return createFieldString(p);
    } else if (parameter instanceof ParameterIntegerMapped p) {
      return createFieldIntegerMapped(p);
    } else if (parameter instanceof ParameterIntegerDirect p) {
      return createFieldIntegerDirect(p);
    } else if (parameter instanceof ParameterFractional p) {
      return createFieldFractional(p);
    } else if (parameter instanceof StructureReference p) {
      return this.createFieldStructureReference(p);
    } else if (parameter instanceof ParameterRate318 p) {
      return createFieldRate318(p);
    } else if (parameter instanceof ParameterRate118 p) {
      return createFieldRate118(p);
    } else if (parameter instanceof ParameterRate118AndOff p) {
      return createFieldRate118AndOff(p);
    } else if (parameter instanceof ParameterLowCut p) {
      return this.createFieldLowCut(p);
    } else if (parameter instanceof ParameterHighCut p) {
      return this.createFieldHighCut(p);
    } else if (parameter instanceof ParameterEnumerated p) {
      return this.createFieldEnumerated(p);
    } else if (parameter instanceof ParameterChain p) {
      return createFieldChain(p);
    }

    throw new IllegalStateException(
      "Unrecognized parameter type: %s".formatted(parameter)
    );
  }

  private static FieldSpec createFieldChain(
    final ParameterChain p)
  {
    final var varType =
      ParameterizedTypeName.get(
        ClassName.get(GWIOVariableType.class),
        ClassName.get(ByteBuffer.class)
      );

    return FieldSpec.builder(varType, fieldName(p.getName()))
      .addModifiers(PUBLIC)
      .addModifiers(FINAL)
      .build();
  }

  private FieldSpec createFieldEnumerated(
    final ParameterEnumerated p)
  {
    final var enumType =
      GWEnumerations.makeEnumerationClassName(this.configuration, p.getType());

    final var varType =
      ParameterizedTypeName.get(
        ClassName.get(GWIOVariableType.class),
        enumType
      );

    return FieldSpec.builder(varType, fieldName(p.getName()))
      .addModifiers(PUBLIC)
      .addModifiers(FINAL)
      .build();
  }

  private FieldSpec createFieldHighCut(
    final ParameterHighCut p)
  {
    final var enumType =
      GWEnumerations.makeEnumerationClassName(
        this.configuration, HIGH_CUT);

    final var varType =
      ParameterizedTypeName.get(
        ClassName.get(GWIOVariableType.class),
        enumType
      );

    return FieldSpec.builder(varType, fieldName(p.getName()))
      .addModifiers(PUBLIC)
      .addModifiers(FINAL)
      .build();
  }

  private FieldSpec createFieldLowCut(
    final ParameterLowCut p)
  {
    final var enumType =
      GWEnumerations.makeEnumerationClassName(
        this.configuration, LOW_CUT);

    final var varType =
      ParameterizedTypeName.get(
        ClassName.get(GWIOVariableType.class),
        enumType
      );

    return FieldSpec.builder(varType, fieldName(p.getName()))
      .addModifiers(PUBLIC)
      .addModifiers(FINAL)
      .build();
  }

  private static FieldSpec createFieldRate118AndOff(
    final ParameterRate118AndOff p)
  {
    final var varType =
      ParameterizedTypeName.get(
        ClassName.get(GWIOVariableType.class),
        ClassName.get(GWIORate119Type.class)
      );

    return FieldSpec.builder(varType, fieldName(p.getName()))
      .addModifiers(PUBLIC)
      .addModifiers(FINAL)
      .build();
  }

  private static FieldSpec createFieldRate118(
    final ParameterRate118 p)
  {
    final var varType =
      ParameterizedTypeName.get(
        ClassName.get(GWIOVariableType.class),
        ClassName.get(GWIORate118Type.class)
      );

    return FieldSpec.builder(varType, fieldName(p.getName()))
      .addModifiers(PUBLIC)
      .addModifiers(FINAL)
      .build();
  }

  private static FieldSpec createFieldRate318(
    final ParameterRate318 p)
  {
    final var varType =
      ParameterizedTypeName.get(
        ClassName.get(GWIOVariableType.class),
        ClassName.get(GWIORate318Type.class)
      );

    return FieldSpec.builder(varType, fieldName(p.getName()))
      .addModifiers(PUBLIC)
      .addModifiers(FINAL)
      .build();
  }

  private FieldSpec createFieldStructureReference(
    final StructureReference p)
  {
    final var targetStruct =
      this.structures.get(p.getType());

    final var typeName =
      ClassName.get(
        this.configuration.structurePackage(),
        this.structNameOf(targetStruct)
      );

    return FieldSpec.builder(typeName, fieldName(p.getName()))
      .addModifiers(PUBLIC)
      .addModifiers(FINAL)
      .build();
  }

  private static FieldSpec createFieldIntegerDirect(
    final ParameterIntegerDirect p)
  {
    final var varType =
      ParameterizedTypeName.get(
        ClassName.get(GWIOVariableType.class),
        ClassName.get(Integer.class)
      );

    return FieldSpec.builder(varType, fieldName(p.getName()))
      .addModifiers(PUBLIC)
      .addModifiers(FINAL)
      .build();
  }

  private static FieldSpec createFieldIntegerMapped(
    final ParameterIntegerMapped p)
  {
    final var varType =
      ParameterizedTypeName.get(
        ClassName.get(GWIOVariableType.class),
        ClassName.get(Integer.class)
      );

    return FieldSpec.builder(varType, fieldName(p.getName()))
      .addModifiers(PUBLIC)
      .addModifiers(FINAL)
      .build();
  }

  private static FieldSpec createFieldFractional(
    final ParameterFractional p)
  {
    final var varType =
      ParameterizedTypeName.get(
        ClassName.get(GWIOVariableType.class),
        ClassName.get(Double.class)
      );

    return FieldSpec.builder(varType, fieldName(p.getName()))
      .addModifiers(PUBLIC)
      .addModifiers(FINAL)
      .build();
  }

  private static FieldSpec createFieldString(
    final ParameterString p)
  {
    final var varType =
      ParameterizedTypeName.get(
        ClassName.get(GWIOVariableType.class),
        ClassName.get(String.class)
      );

    return FieldSpec.builder(varType, fieldName(p.getName()))
      .addModifiers(PUBLIC)
      .addModifiers(FINAL)
      .build();
  }

  private CodeBlock createFieldEnumeratedInitializer(
    final Structure structure,
    final ParameterEnumerated p)
  {
    final var enumType =
      GWEnumerations.makeEnumerationClassName(this.configuration, p.getType());

    final var offset =
      Long.toUnsignedString(GWHexIntegers.parseHex(p.getOffset()), 16);

    final var code = CodeBlock.builder();
    code.add("// $L.$L\n", structure.getName(), p.getName());
    code.add(
      "this.$L = $T.create(\n", fieldName(p.getName()), GWIOVariable.class);
    code.add("  inDevice,\n");
    code.add("  inAttributes,\n");
    code.add("  $T.serializer(),\n", enumType);
    code.add("  $T.deserializer(),\n", enumType);
    code.add("  $T.serializeSize(),\n", enumType);

    code.add(
      "  new $T<>($S, $T.class, $T.first(), $T.first(), $T.last()),\n",
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
    final ParameterHighCut p)
  {
    final var enumType =
      GWEnumerations.makeEnumerationClassName(this.configuration, HIGH_CUT);

    final var offset =
      Long.toUnsignedString(GWHexIntegers.parseHex(p.getOffset()), 16);

    final var code = CodeBlock.builder();
    code.add("// $L.$L\n", structure.getName(), p.getName());
    code.add(
      "this.$L = $T.create(\n", fieldName(p.getName()), GWIOVariable.class);
    code.add("  inDevice,\n");
    code.add("  inAttributes,\n");
    code.add("  $T.serializer(),\n", enumType);
    code.add("  $T.deserializer(),\n", enumType);
    code.add("  $T.serializeSize(),\n", enumType);

    code.add(
      "  new $T<>($S, $T.class, $T.first(), $T.first(), $T.last()),\n",
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
    final ParameterLowCut p)
  {
    final var enumType =
      GWEnumerations.makeEnumerationClassName(this.configuration, LOW_CUT);

    final var offset =
      Long.toUnsignedString(GWHexIntegers.parseHex(p.getOffset()), 16);

    final var code = CodeBlock.builder();
    code.add("// $L.$L\n", structure.getName(), p.getName());
    code.add(
      "this.$L = $T.create(\n", fieldName(p.getName()), GWIOVariable.class);
    code.add("  inDevice,\n");
    code.add("  inAttributes,\n");
    code.add("  $T.serializer(),\n", enumType);
    code.add("  $T.deserializer(),\n", enumType);
    code.add("  $T.serializeSize(),\n", enumType);

    code.add(
      "  new $T<>($S, $T.class, $T.first(), $T.first(), $T.last()),\n",
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
    final ParameterRate118AndOff p)
  {
    final var serializers =
      ClassName.get(GWIOSerializers.class);
    final var offset =
      Long.toUnsignedString(GWHexIntegers.parseHex(p.getOffset()), 16);

    final var code = CodeBlock.builder();
    code.add("// $L.$L\n", structure.getName(), p.getName());
    code.add(
      "this.$L = $T.create(\n", fieldName(p.getName()), GWIOVariable.class);
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
    final ParameterRate118 p)
  {
    final var serializers =
      ClassName.get(GWIOSerializers.class);
    final var offset =
      Long.toUnsignedString(GWHexIntegers.parseHex(p.getOffset()), 16);

    final var code = CodeBlock.builder();
    code.add("// $L.$L\n", structure.getName(), p.getName());
    code.add(
      "this.$L = $T.create(\n", fieldName(p.getName()), GWIOVariable.class);
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
    final ParameterRate318 p)
  {
    final var serializers =
      ClassName.get(GWIOSerializers.class);
    final var offset =
      Long.toUnsignedString(GWHexIntegers.parseHex(p.getOffset()), 16);

    final var code = CodeBlock.builder();
    code.add("// $L.$L\n", structure.getName(), p.getName());
    code.add(
      "this.$L = $T.create(\n", fieldName(p.getName()), GWIOVariable.class);
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
    final ParameterChain p)
  {
    final var serializers =
      ClassName.get(GWIOSerializers.class);
    final var offset =
      Long.toUnsignedString(GWHexIntegers.parseHex(p.getOffset()), 16);

    final var code = CodeBlock.builder();
    code.add("// $L.$L\n", structure.getName(), p.getName());
    code.add(
      "this.$L = $T.create(\n", fieldName(p.getName()), GWIOVariable.class);
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
    final StructureReference p)
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
      fieldName(p.getName()),
      typeName,
      offset
    );
    return code.build();
  }

  private record GWSerializers(
    String serializeMethod,
    String deserializeMethod,
    int size) {

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
    final ParameterIntegerDirect p)
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
      "this.$L = $T.create(\n", fieldName(p.getName()), GWIOVariable.class);
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
    final ParameterFractional p)
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
      "this.$L = $T.create(\n", fieldName(p.getName()), GWIOVariable.class);
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
    final ParameterIntegerMapped p)
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
      "this.$L = $T.create(\n", fieldName(p.getName()), GWIOVariable.class);
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
    final ParameterString p)
  {
    final var offset =
      Long.toUnsignedString(GWHexIntegers.parseHex(p.getOffset()), 16);

    final var code = CodeBlock.builder();
    code.add("// $L.$L\n", structure.getName(), p.getName());
    code.add(
      "this.$L = $T.create(\n", fieldName(p.getName()), GWIOVariable.class);
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

  private static String fieldName(
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
