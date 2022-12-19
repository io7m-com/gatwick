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
 * The patch PFX effect block.
 */

public interface GWPatchEffectBlockPFXType
  extends GWPatchEffectBlockType
{
  /**
   * @return The effect type
   */

  GWIOVariableType<GWPatchPedalFXTypeValue> type();

  /**
   * @return The wah type
   */

  GWIOVariableType<GWPatchPedalFXWahTypeValue> wahType();

  /**
   * @return The wah pedal position
   */

  GWIOVariableType<Integer> wahPosition();

  /**
   * @return The pedal minimum position
   */

  GWIOVariableType<Integer> pedalMinimum();

  /**
   * @return The pedal maximum position
   */

  GWIOVariableType<Integer> pedalMaximum();

  /**
   * @return The pitch bend pedal position
   */

  GWIOVariableType<Integer> bendPosition();

  /**
   * @return The pitch bend minimum pitch
   */

  GWIOVariableType<Integer> bendPitchMinimum();

  /**
   * @return The pitch bend maximum pitch
   */

  GWIOVariableType<Integer> bendPitchMaximum();
}
