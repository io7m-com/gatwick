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

package com.io7m.gatwick.controller.main.internal;

import com.io7m.gatwick.controller.api.GWChain;
import com.io7m.gatwick.controller.api.GWPatchEffectBlockCMPType;
import com.io7m.gatwick.controller.api.GWPatchEffectBlockDistortionType;
import com.io7m.gatwick.controller.api.GWPatchEffectBlockDividerType;
import com.io7m.gatwick.controller.api.GWPatchEffectBlockFXType;
import com.io7m.gatwick.controller.api.GWPatchEffectBlockNSType;
import com.io7m.gatwick.controller.api.GWPatchEffectBlockPFXType;
import com.io7m.gatwick.controller.api.GWPatchEffectBlockPreampType;
import com.io7m.gatwick.controller.api.GWPatchType;
import com.io7m.gatwick.controller.main.internal.generated.StructPatch;
import com.io7m.gatwick.controller.main.internal.generated.StructPatch2;
import com.io7m.gatwick.controller.main.internal.generated.StructPatch3;
import com.io7m.gatwick.device.api.GWDeviceType;
import com.io7m.gatwick.iovar.GWIOVariable;
import com.io7m.gatwick.iovar.GWIOVariableInformation;
import com.io7m.gatwick.iovar.GWIOVariableType;
import com.io7m.jattribute.core.Attributes;

import java.nio.ByteBuffer;
import java.util.Objects;

final class GWPatch implements GWPatchType
{
  private final GWDeviceType device;
  private final GWIOVariableType<ByteBuffer> chainBase;
  private final GWIOVariableType<GWChain> chain;
  private final GWPatchEffectBlockCMP cmp;
  private final GWPatchEffectBlockDistortion ds1;
  private final GWPatchEffectBlockDistortion ds2;
  private final GWPatchEffectBlockDivider div1;
  private final GWPatchEffectBlockDivider div2;
  private final GWPatchEffectBlockDivider div3;
  private final GWPatchEffectBlockFX fx1;
  private final GWPatchEffectBlockFX fx2;
  private final GWPatchEffectBlockFX fx3;
  private final GWPatchEffectBlockNS ns1;
  private final GWPatchEffectBlockNS ns2;
  private final GWPatchEffectBlockPFXType pfx;
  private final GWPatchEffectBlockPreamp preamp1;
  private final GWPatchEffectBlockPreamp preamp2;
  private final StructPatch patchMemory;
  private final StructPatch2 patchMemory2;
  private final StructPatch3 patchMemory3;

  GWPatch(
    final GWDeviceType inDevice,
    final Attributes attributes,
    final StructPatch inPatchMemory,
    final StructPatch2 inPatchMemory2,
    final StructPatch3 inPatchMemory3)
  {
    this.device =
      Objects.requireNonNull(inDevice, "device");
    this.patchMemory =
      Objects.requireNonNull(inPatchMemory, "patchMemory");
    this.patchMemory2 =
      Objects.requireNonNull(inPatchMemory2, "patchMemory2");
    this.patchMemory3 =
      Objects.requireNonNull(inPatchMemory3, "patchMemory3");

    this.pfx =
      new GWPatchEffectBlockPFX(this.patchMemory.f_pedalfx);
    this.cmp =
      new GWPatchEffectBlockCMP(this.patchMemory.f_comp);
    this.ns1 =
      new GWPatchEffectBlockNS(this.patchMemory.f_ns1);
    this.ns2 =
      new GWPatchEffectBlockNS(this.patchMemory.f_ns2);
    this.preamp1 =
      new GWPatchEffectBlockPreamp(this.patchMemory.f_preampa);
    this.preamp2 =
      new GWPatchEffectBlockPreamp(this.patchMemory.f_preampb);
    this.div1 =
      new GWPatchEffectBlockDivider(this.patchMemory.f_efct, 1);
    this.div2 =
      new GWPatchEffectBlockDivider(this.patchMemory.f_efct, 2);
    this.div3 =
      new GWPatchEffectBlockDivider(this.patchMemory.f_efct, 3);
    this.ds1 =
      new GWPatchEffectBlockDistortion(this.patchMemory.f_dist1);
    this.ds2 =
      new GWPatchEffectBlockDistortion(this.patchMemory.f_dist2);

    this.fx1 =
      new GWPatchEffectBlockFX(
        this.patchMemory.f_fx1,
        this.patchMemory.f_fx1agsim,
        this.patchMemory.f_fx1acreso,
        this.patchMemory.f_fx1awah,
        this.patchMemory.f_fx1chorus,
        this.patchMemory.f_fx1cvibe,
        this.patchMemory.f_fx1comp,
        this.patchMemory.f_fx1defretter,
        this.patchMemory.f_fx1feedbacker,
        this.patchMemory.f_fx1flanger,
        this.patchMemory.f_fx1harmonizer,
        this.patchMemory.f_fx1humanizer,
        this.patchMemory.f_fx1octave,
        this.patchMemory.f_fx1overtone,
        this.patchMemory.f_fx1pan,
        this.patchMemory.f_fx1phaser,
        this.patchMemory.f_fx1pitchshift,
        this.patchMemory.f_fx1ringmod,
        this.patchMemory.f_fx1rotary,
        this.patchMemory.f_fx1sitarsim,
        this.patchMemory.f_fx1slicer,
        this.patchMemory.f_fx1slowgear,
        this.patchMemory.f_fx1soundhold,
        this.patchMemory.f_fx1sbend,
        this.patchMemory.f_fx1twah,
        this.patchMemory.f_fx1tremolo,
        this.patchMemory.f_fx1vibrato,
        this.patchMemory2.f_fx1chorusbass,
        this.patchMemory2.f_fx1flangerbass,
        this.patchMemory3.f_fx1dist,
        this.patchMemory2.f_fx1slowgearbass,
        this.patchMemory2.f_fx1octavebass,
        this.patchMemory2.f_fx1defretterbass,
        this.patchMemory2.f_fx1touchwahbass
      );

    this.fx2 =
      new GWPatchEffectBlockFX(
        this.patchMemory.f_fx2,
        this.patchMemory.f_fx2agsim,
        this.patchMemory.f_fx2acreso,
        this.patchMemory.f_fx2awah,
        this.patchMemory.f_fx2chorus,
        this.patchMemory.f_fx2cvibe,
        this.patchMemory.f_fx2comp,
        this.patchMemory.f_fx2defretter,
        this.patchMemory.f_fx2feedbacker,
        this.patchMemory.f_fx2flanger,
        this.patchMemory.f_fx2harmonizer,
        this.patchMemory.f_fx2humanizer,
        this.patchMemory.f_fx2octave,
        this.patchMemory.f_fx2overtone,
        this.patchMemory.f_fx2pan,
        this.patchMemory.f_fx2phaser,
        this.patchMemory.f_fx2pitchshift,
        this.patchMemory.f_fx2ringmod,
        this.patchMemory.f_fx2rotary,
        this.patchMemory.f_fx2sitarsim,
        this.patchMemory.f_fx2slicer,
        this.patchMemory.f_fx2slowgear,
        this.patchMemory.f_fx2soundhold,
        this.patchMemory.f_fx2sbend,
        this.patchMemory.f_fx2twah,
        this.patchMemory.f_fx2tremolo,
        this.patchMemory.f_fx2vibrato,
        this.patchMemory2.f_fx2chorusbass,
        this.patchMemory2.f_fx2flangerbass,
        this.patchMemory3.f_fx2dist,
        this.patchMemory2.f_fx2slowgearbass,
        this.patchMemory2.f_fx2octavebass,
        this.patchMemory2.f_fx2defretterbass,
        this.patchMemory2.f_fx2touchwahbass
      );

    this.fx3 =
      new GWPatchEffectBlockFX(
        this.patchMemory.f_fx3,
        this.patchMemory.f_fx3agsim,
        this.patchMemory.f_fx3acreso,
        this.patchMemory.f_fx3awah,
        this.patchMemory.f_fx3chorus,
        this.patchMemory.f_fx3cvibe,
        this.patchMemory.f_fx3comp,
        this.patchMemory.f_fx3defretter,
        this.patchMemory.f_fx3feedbacker,
        this.patchMemory.f_fx3flanger,
        this.patchMemory.f_fx3harmonizer,
        this.patchMemory.f_fx3humanizer,
        this.patchMemory.f_fx3octave,
        this.patchMemory.f_fx3overtone,
        this.patchMemory.f_fx3pan,
        this.patchMemory.f_fx3phaser,
        this.patchMemory.f_fx3pitchshift,
        this.patchMemory.f_fx3ringmod,
        this.patchMemory.f_fx3rotary,
        this.patchMemory.f_fx3sitarsim,
        this.patchMemory.f_fx3slicer,
        this.patchMemory.f_fx3slowgear,
        this.patchMemory.f_fx3soundhold,
        this.patchMemory.f_fx3sbend,
        this.patchMemory.f_fx3twah,
        this.patchMemory.f_fx3tremolo,
        this.patchMemory.f_fx3vibrato,
        this.patchMemory2.f_fx3chorusbass,
        this.patchMemory2.f_fx3flangerbass,
        this.patchMemory3.f_fx3dist,
        this.patchMemory2.f_fx3slowgearbass,
        this.patchMemory2.f_fx3octavebass,
        this.patchMemory2.f_fx3defretterbass,
        this.patchMemory2.f_fx3touchwahbass
      );

    this.chainBase =
      this.patchMemory.f_efct.f_chain;

    this.chain =
      GWIOVariable.create(
        this.device,
        attributes,
        GWChainSerializers.serializer(),
        GWChainSerializers.deserializer(),
        49,
        new GWIOVariableInformation<>(
          "CHAIN",
          GWChain.class,
          GWChain.defaultChain(),
          GWChain.defaultChain(),
          GWChain.defaultChain()
        ),
        this.chainBase.address()
      );
  }

  @Override
  public GWIOVariableType<String> name()
  {
    return this.patchMemory.f_common.f_patch_name;
  }

  @Override
  public GWPatchEffectBlockPFXType pfx()
  {
    return this.pfx;
  }

  @Override
  public GWPatchEffectBlockCMPType cmp()
  {
    return this.cmp;
  }

  @Override
  public GWPatchEffectBlockNSType ns1()
  {
    return this.ns1;
  }

  @Override
  public GWPatchEffectBlockNSType ns2()
  {
    return this.ns2;
  }

  @Override
  public GWPatchEffectBlockPreampType preamp1()
  {
    return this.preamp1;
  }

  @Override
  public GWPatchEffectBlockPreampType preamp2()
  {
    return this.preamp2;
  }

  @Override
  public GWPatchEffectBlockDividerType divider1()
  {
    return this.div1;
  }

  @Override
  public GWPatchEffectBlockDividerType divider2()
  {
    return this.div2;
  }

  @Override
  public GWPatchEffectBlockDividerType divider3()
  {
    return this.div3;
  }

  @Override
  public GWIOVariableType<GWChain> chain()
  {
    return this.chain;
  }

  @Override
  public GWPatchEffectBlockDistortionType dist1()
  {
    return this.ds1;
  }

  @Override
  public GWPatchEffectBlockDistortionType dist2()
  {
    return this.ds2;
  }

  @Override
  public GWPatchEffectBlockFXType fx1()
  {
    return this.fx1;
  }

  @Override
  public GWPatchEffectBlockFXType fx2()
  {
    return this.fx2;
  }

  @Override
  public GWPatchEffectBlockFXType fx3()
  {
    return this.fx3;
  }
}
