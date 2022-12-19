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

import com.io7m.gatwick.controller.api.GWPatchEffectBlockPFXType;
import com.io7m.gatwick.controller.api.GWPatchType;
import com.io7m.gatwick.controller.main.internal.generated.StructPatch;
import com.io7m.gatwick.device.api.GWDeviceType;
import com.io7m.gatwick.iovar.GWIOVariableType;

import java.util.Objects;

final class GWPatch implements GWPatchType
{
  private final GWDeviceType device;
  private final StructPatch patchMemory;
  private final GWPatchEffectBlockPFXType pfx;

  GWPatch(
    final GWDeviceType inDevice,
    final StructPatch inPatchMemory)
  {
    this.device =
      Objects.requireNonNull(inDevice, "device");
    this.patchMemory =
      Objects.requireNonNull(inPatchMemory, "f_patch");
    this.pfx =
      new GWPatchEffectPFXBlock(this.patchMemory.f_pedalfx);
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
}
