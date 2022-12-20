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
 * A patch divider effect block.
 */

public interface GWPatchEffectBlockDividerType
  extends GWPatchEffectBlockType
{
  /**
   * @return The divider mode
   */

  GWIOVariableType<GWPatchEfctDividerModeValue> mode();

  /**
   * @return The divider channel selection
   */

  GWIOVariableType<GWPatchEfctDividerChannelSelectValue> channel();

  /**
   * @return The dynamic setting for channel A
   */

  GWIOVariableType<GWPatchEfctDividerDynamicValue> dynamicA();

  /**
   * @return The dynamic sensitivity for channel A
   */

  GWIOVariableType<Integer> dynamicSensitivityA();

  /**
   * @return The filter value for channel A
   */

  GWIOVariableType<GWPatchEfctDividerFilterValue> filterA();

  /**
   * @return The filter cutoff for channel A
   */

  GWIOVariableType<GWPatchEfctDividerCutoffFreqValue> cutoffA();

  /**
   * @return The dynamic setting for channel B
   */

  GWIOVariableType<GWPatchEfctDividerDynamicValue> dynamicB();

  /**
   * @return The dynamic sensitivity for channel B
   */

  GWIOVariableType<Integer> dynamicSensitivityB();

  /**
   * @return The filter value for channel B
   */

  GWIOVariableType<GWPatchEfctDividerFilterValue> filterB();

  /**
   * @return The filter cutoff for channel B
   */

  GWIOVariableType<GWPatchEfctDividerCutoffFreqValue> cutoffB();
}
