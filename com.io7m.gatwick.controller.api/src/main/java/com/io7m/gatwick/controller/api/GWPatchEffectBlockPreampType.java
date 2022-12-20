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
 * The patch preamp effect block.
 */

public interface GWPatchEffectBlockPreampType
  extends GWPatchEffectBlockType
{
  /**
   * @return The on/off value
   */

  GWIOVariableType<GWOnOffValue> enabled();

  /**
   * @return The preamp type
   */

  GWIOVariableType<GWPatchPreampTypeValue> type();

  /**
   * @return The amount of preamp gain
   */

  GWIOVariableType<Integer> gain();

  /**
   * @return The amount by which compression changes in response to the power
   * amp.
   */

  GWIOVariableType<Integer> sag();

  /**
   * @return The amount by which dynamics is affected by the interaction between
   * the power amp and the speaker transformer.
   */

  GWIOVariableType<Integer> resonance();

  /**
   * @return The preamp level
   */

  GWIOVariableType<Integer> level();

  /**
   * @return The bass
   */

  GWIOVariableType<Integer> bass();

  /**
   * @return The middle
   */

  GWIOVariableType<Integer> middle();

  /**
   * @return The treble
   */

  GWIOVariableType<Integer> treble();

  /**
   * @return The presence
   */

  GWIOVariableType<Integer> presence();

  /**
   * @return The bright switch
   */

  GWIOVariableType<GWOnOffValue> bright();

  /**
   * @return The gain switch
   */

  GWIOVariableType<GWPatchPreampGainValue> gainSwitch();

  /**
   * @return The solo switch
   */

  GWIOVariableType<GWOnOffValue> solo();

  /**
   * @return The solo level
   */

  GWIOVariableType<Integer> soloLevel();
}
