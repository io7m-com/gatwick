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

import com.io7m.gatwick.controller.api.GWOnOffValue;
import com.io7m.gatwick.controller.api.GWPatchEffectBlockNSType;
import com.io7m.gatwick.controller.api.GWPatchNSDetectValue;
import com.io7m.gatwick.controller.main.internal.generated.StructPatchNS;
import com.io7m.gatwick.device.api.GWDeviceException;
import com.io7m.gatwick.iovar.GWIOVariableType;

final class GWPatchEffectBlockNS
  extends GWPatchEffectBlock
  implements GWPatchEffectBlockNSType
{
  private final StructPatchNS ns;

  GWPatchEffectBlockNS(
    final StructPatchNS f_ns)
  {
    this.ns = f_ns;
  }

  @Override
  public GWIOVariableType<GWOnOffValue> enabled()
  {
    return this.ns.f_sw;
  }

  @Override
  public GWIOVariableType<Integer> threshold()
  {
    return this.ns.f_threshold;
  }

  @Override
  public GWIOVariableType<Integer> release()
  {
    return this.ns.f_release;
  }

  @Override
  public GWIOVariableType<GWPatchNSDetectValue> detect()
  {
    return this.ns.f_detect;
  }

  @Override
  public void readFromDevice()
    throws InterruptedException, GWDeviceException
  {
    this.ns.readFromDevice();
  }
}
