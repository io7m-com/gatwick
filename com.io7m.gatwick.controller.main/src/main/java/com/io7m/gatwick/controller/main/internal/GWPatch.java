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
import com.io7m.gatwick.controller.api.GWPatchEffectBlockDividerType;
import com.io7m.gatwick.controller.api.GWPatchEffectBlockNSType;
import com.io7m.gatwick.controller.api.GWPatchEffectBlockPFXType;
import com.io7m.gatwick.controller.api.GWPatchEffectBlockPreampType;
import com.io7m.gatwick.controller.api.GWPatchType;
import com.io7m.gatwick.controller.main.internal.generated.StructPatch;
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
  private final StructPatch patchMemory;
  private final GWPatchEffectBlockPFXType pfx;
  private final GWPatchEffectBlockCMP cmp;
  private final GWPatchEffectBlockNS ns1;
  private final GWPatchEffectBlockNS ns2;
  private final GWPatchEffectBlockPreamp preamp1;
  private final GWPatchEffectBlockPreamp preamp2;
  private final GWPatchEffectBlockDivider div1;
  private final GWPatchEffectBlockDivider div2;
  private final GWPatchEffectBlockDivider div3;
  private final GWIOVariableType<ByteBuffer> chainBase;
  private final GWIOVariableType<GWChain> chain;

  GWPatch(
    final GWDeviceType inDevice,
    final Attributes attributes,
    final StructPatch inPatchMemory)
  {
    this.device =
      Objects.requireNonNull(inDevice, "device");
    this.patchMemory =
      Objects.requireNonNull(inPatchMemory, "f_patch");
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
}
