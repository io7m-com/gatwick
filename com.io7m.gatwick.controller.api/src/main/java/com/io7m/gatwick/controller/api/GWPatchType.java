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

package com.io7m.gatwick.controller.api;

import com.io7m.gatwick.iovar.GWIOVariableType;

/**
 * The type of patches.
 */

public interface GWPatchType
{
  /**
   * @return The patch name
   */

  GWIOVariableType<String> name();

  /**
   * @return The PFX patch block
   */

  GWPatchEffectBlockPFXType pfx();

  /**
   * @return The CMP patch block
   */

  GWPatchEffectBlockCMPType cmp();

  /**
   * @return The NS1 patch block
   */

  GWPatchEffectBlockNSType ns1();

  /**
   * @return The NS2 patch block
   */

  GWPatchEffectBlockNSType ns2();

  /**
   * @return The Preamp1 patch block
   */

  GWPatchEffectBlockPreampType preamp1();

  /**
   * @return The Preamp2 patch block
   */

  GWPatchEffectBlockPreampType preamp2();

  /**
   * @return The divider 1 block
   */

  GWPatchEffectBlockDividerType divider1();

  /**
   * @return The divider 2 block
   */

  GWPatchEffectBlockDividerType divider2();

  /**
   * @return The divider 3 block
   */

  GWPatchEffectBlockDividerType divider3();

  /**
   * @return The current effects chain
   */

  GWIOVariableType<GWChain> chain();
}
