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


package com.io7m.gatwick.tests.controller;

import com.io7m.gatwick.controller.api.GWAmpCtlValue;
import com.io7m.gatwick.controller.api.GWBankChangeModeValue;
import com.io7m.gatwick.controller.api.GWChainElementValue;
import com.io7m.gatwick.controller.api.GWHighCutValue;
import com.io7m.gatwick.controller.api.GWLEDLuminanceValue;
import com.io7m.gatwick.controller.api.GWLowCutValue;
import com.io7m.gatwick.controller.api.GWMetronomeBeatValue;
import com.io7m.gatwick.controller.api.GWOnOffValue;
import com.io7m.gatwick.controller.api.GWPatchAmpCtlBPM1Value;
import com.io7m.gatwick.controller.api.GWPatchAmpCtlBPM2Value;
import com.io7m.gatwick.controller.api.GWPatchBankDownFunctionValue;
import com.io7m.gatwick.controller.api.GWPatchBankUpFunctionValue;
import com.io7m.gatwick.controller.api.GWPatchCNumFunctionValue;
import com.io7m.gatwick.controller.api.GWPatchChorusHighCutValue;
import com.io7m.gatwick.controller.api.GWPatchChorusLowCutValue;
import com.io7m.gatwick.controller.api.GWPatchChorusOutputModeValue;
import com.io7m.gatwick.controller.api.GWPatchChorusTypeValue;
import com.io7m.gatwick.controller.api.GWPatchChorusWaveformValue;
import com.io7m.gatwick.controller.api.GWPatchCommonInputSettingValue;
import com.io7m.gatwick.controller.api.GWPatchCompRatioValue;
import com.io7m.gatwick.controller.api.GWPatchCompTypeValue;
import com.io7m.gatwick.controller.api.GWPatchCtl1FunctionValue;
import com.io7m.gatwick.controller.api.GWPatchCtl2FunctionValue;
import com.io7m.gatwick.controller.api.GWPatchCtl3FunctionValue;
import com.io7m.gatwick.controller.api.GWPatchCtl4FunctionValue;
import com.io7m.gatwick.controller.api.GWPatchCtl5FunctionValue;
import com.io7m.gatwick.controller.api.GWPatchCtl6FunctionValue;
import com.io7m.gatwick.controller.api.GWPatchCtl7FunctionValue;
import com.io7m.gatwick.controller.api.GWPatchDelayHighCutValue;
import com.io7m.gatwick.controller.api.GWPatchDistTypeValue;
import com.io7m.gatwick.controller.api.GWPatchEQHighCutValue;
import com.io7m.gatwick.controller.api.GWPatchEQHighMidFreqValue;
import com.io7m.gatwick.controller.api.GWPatchEQLowCutValue;
import com.io7m.gatwick.controller.api.GWPatchEQLowMidFreqValue;
import com.io7m.gatwick.controller.api.GWPatchEQQValue;
import com.io7m.gatwick.controller.api.GWPatchEQTypeValue;
import com.io7m.gatwick.controller.api.GWPatchEfctDividerChannelSelectValue;
import com.io7m.gatwick.controller.api.GWPatchEfctDividerCutoffFreqValue;
import com.io7m.gatwick.controller.api.GWPatchEfctDividerDynamicValue;
import com.io7m.gatwick.controller.api.GWPatchEfctDividerFilterValue;
import com.io7m.gatwick.controller.api.GWPatchEfctDividerModeValue;
import com.io7m.gatwick.controller.api.GWPatchEfctFootVolumeCurveValue;
import com.io7m.gatwick.controller.api.GWPatchEfctKeyValue;
import com.io7m.gatwick.controller.api.GWPatchEfctMicDistanceValue;
import com.io7m.gatwick.controller.api.GWPatchEfctMicTypeValue;
import com.io7m.gatwick.controller.api.GWPatchEfctMixModeValue;
import com.io7m.gatwick.controller.api.GWPatchEfctSendReturnModeValue;
import com.io7m.gatwick.controller.api.GWPatchEfctSpeakerTypeValue;
import com.io7m.gatwick.controller.api.GWPatchExp1FunctionValue;
import com.io7m.gatwick.controller.api.GWPatchExp1SwFunctionValue;
import com.io7m.gatwick.controller.api.GWPatchExp2FunctionValue;
import com.io7m.gatwick.controller.api.GWPatchExp3FunctionValue;
import com.io7m.gatwick.controller.api.GWPatchFXAWahFilterModeValue;
import com.io7m.gatwick.controller.api.GWPatchFXAWahWaveformValue;
import com.io7m.gatwick.controller.api.GWPatchFXAcResoTypeValue;
import com.io7m.gatwick.controller.api.GWPatchFXCVibeTypeValue;
import com.io7m.gatwick.controller.api.GWPatchFXChorusBassTypeValue;
import com.io7m.gatwick.controller.api.GWPatchFXChorusHighCutValue;
import com.io7m.gatwick.controller.api.GWPatchFXChorusLowCutValue;
import com.io7m.gatwick.controller.api.GWPatchFXChorusOutputModeValue;
import com.io7m.gatwick.controller.api.GWPatchFXChorusTypeValue;
import com.io7m.gatwick.controller.api.GWPatchFXChorusWaveformValue;
import com.io7m.gatwick.controller.api.GWPatchFXFeedbackerModeValue;
import com.io7m.gatwick.controller.api.GWPatchFXFlangerHighCutValue;
import com.io7m.gatwick.controller.api.GWPatchFXFlangerLowCutValue;
import com.io7m.gatwick.controller.api.GWPatchFXFlangerSeparationValue;
import com.io7m.gatwick.controller.api.GWPatchFXFlangerWaveformValue;
import com.io7m.gatwick.controller.api.GWPatchFXHarmonizerHarmonyValue;
import com.io7m.gatwick.controller.api.GWPatchFXHarmonizerVoiceValue;
import com.io7m.gatwick.controller.api.GWPatchFXHumanizerModeValue;
import com.io7m.gatwick.controller.api.GWPatchFXHumanizerVowelValue;
import com.io7m.gatwick.controller.api.GWPatchFXOctaveTypeValue;
import com.io7m.gatwick.controller.api.GWPatchFXOvertoneOutputModeValue;
import com.io7m.gatwick.controller.api.GWPatchFXPhaserHighCutValue;
import com.io7m.gatwick.controller.api.GWPatchFXPhaserLowCutValue;
import com.io7m.gatwick.controller.api.GWPatchFXPhaserStageValue;
import com.io7m.gatwick.controller.api.GWPatchFXPhaserTypeValue;
import com.io7m.gatwick.controller.api.GWPatchFXPhaserWaveformValue;
import com.io7m.gatwick.controller.api.GWPatchFXPitchShiftModeValue;
import com.io7m.gatwick.controller.api.GWPatchFXPitchShiftVoiceValue;
import com.io7m.gatwick.controller.api.GWPatchFXRotarySpeedValue;
import com.io7m.gatwick.controller.api.GWPatchFXSBendPitchValue;
import com.io7m.gatwick.controller.api.GWPatchFXTWahFilterValue;
import com.io7m.gatwick.controller.api.GWPatchFXTWahPolarityValue;
import com.io7m.gatwick.controller.api.GWPatchFXTypeValue;
import com.io7m.gatwick.controller.api.GWPatchFunctionModeValue;
import com.io7m.gatwick.controller.api.GWPatchLedType1Value;
import com.io7m.gatwick.controller.api.GWPatchLedType2Value;
import com.io7m.gatwick.controller.api.GWPatchMstDelayDTypeValue;
import com.io7m.gatwick.controller.api.GWPatchMstDelayDrumEchoHeadValue;
import com.io7m.gatwick.controller.api.GWPatchMstDelayDualModeValue;
import com.io7m.gatwick.controller.api.GWPatchMstDelayHeadValue;
import com.io7m.gatwick.controller.api.GWPatchMstDelayHighCutValue;
import com.io7m.gatwick.controller.api.GWPatchMstDelaySelectorValue;
import com.io7m.gatwick.controller.api.GWPatchMstDelayStageValue;
import com.io7m.gatwick.controller.api.GWPatchMstDelayTwistModeValue;
import com.io7m.gatwick.controller.api.GWPatchMstDelayTypeValue;
import com.io7m.gatwick.controller.api.GWPatchNSDetectValue;
import com.io7m.gatwick.controller.api.GWPatchNum1FunctionValue;
import com.io7m.gatwick.controller.api.GWPatchNum2FunctionValue;
import com.io7m.gatwick.controller.api.GWPatchNum3FunctionValue;
import com.io7m.gatwick.controller.api.GWPatchNum4FunctionValue;
import com.io7m.gatwick.controller.api.GWPatchNum5FunctionValue;
import com.io7m.gatwick.controller.api.GWPatchPedalFXTypeValue;
import com.io7m.gatwick.controller.api.GWPatchPedalFXWahTypeValue;
import com.io7m.gatwick.controller.api.GWPatchPreampGainValue;
import com.io7m.gatwick.controller.api.GWPatchPreampTypeValue;
import com.io7m.gatwick.controller.api.GWPatchReverbModeValue;
import com.io7m.gatwick.controller.api.GWPatchReverbTTypeValue;
import com.io7m.gatwick.controller.api.GWPatchReverbTypeValue;
import com.io7m.gatwick.controller.api.GWTunerModeValue;
import com.io7m.gatwick.controller.api.GWTunerTypeValue;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.HashSet;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class GWEnumerationsTest
{
  private static final Class[] ENUMERATIONS = {
    GWAmpCtlValue.class,
    GWBankChangeModeValue.class,
    GWChainElementValue.class,
    GWHighCutValue.class,
    GWLEDLuminanceValue.class,
    GWLowCutValue.class,
    GWMetronomeBeatValue.class,
    GWOnOffValue.class,
    GWPatchAmpCtlBPM1Value.class,
    GWPatchAmpCtlBPM2Value.class,
    GWPatchBankDownFunctionValue.class,
    GWPatchBankUpFunctionValue.class,
    GWPatchChorusHighCutValue.class,
    GWPatchChorusLowCutValue.class,
    GWPatchChorusOutputModeValue.class,
    GWPatchChorusTypeValue.class,
    GWPatchChorusWaveformValue.class,
    GWPatchCNumFunctionValue.class,
    GWPatchCommonInputSettingValue.class,
    GWPatchCompRatioValue.class,
    GWPatchCompTypeValue.class,
    GWPatchCtl1FunctionValue.class,
    GWPatchCtl2FunctionValue.class,
    GWPatchCtl3FunctionValue.class,
    GWPatchCtl4FunctionValue.class,
    GWPatchCtl5FunctionValue.class,
    GWPatchCtl6FunctionValue.class,
    GWPatchCtl7FunctionValue.class,
    GWPatchDelayHighCutValue.class,
    GWPatchDistTypeValue.class,
    GWPatchEfctDividerChannelSelectValue.class,
    GWPatchEfctDividerCutoffFreqValue.class,
    GWPatchEfctDividerDynamicValue.class,
    GWPatchEfctDividerFilterValue.class,
    GWPatchEfctDividerModeValue.class,
    GWPatchEfctFootVolumeCurveValue.class,
    GWPatchEfctKeyValue.class,
    GWPatchEfctMicDistanceValue.class,
    GWPatchEfctMicTypeValue.class,
    GWPatchEfctMixModeValue.class,
    GWPatchEfctSendReturnModeValue.class,
    GWPatchEfctSpeakerTypeValue.class,
    GWPatchEQHighCutValue.class,
    GWPatchEQHighMidFreqValue.class,
    GWPatchEQLowCutValue.class,
    GWPatchEQLowMidFreqValue.class,
    GWPatchEQQValue.class,
    GWPatchEQTypeValue.class,
    GWPatchExp1FunctionValue.class,
    GWPatchExp1SwFunctionValue.class,
    GWPatchExp2FunctionValue.class,
    GWPatchExp3FunctionValue.class,
    GWPatchFunctionModeValue.class,
    GWPatchFXAcResoTypeValue.class,
    GWPatchFXAWahFilterModeValue.class,
    GWPatchFXAWahWaveformValue.class,
    GWPatchFXChorusBassTypeValue.class,
    GWPatchFXChorusHighCutValue.class,
    GWPatchFXChorusLowCutValue.class,
    GWPatchFXChorusOutputModeValue.class,
    GWPatchFXChorusTypeValue.class,
    GWPatchFXChorusWaveformValue.class,
    GWPatchFXCVibeTypeValue.class,
    GWPatchFXFeedbackerModeValue.class,
    GWPatchFXFlangerHighCutValue.class,
    GWPatchFXFlangerLowCutValue.class,
    GWPatchFXFlangerSeparationValue.class,
    GWPatchFXFlangerWaveformValue.class,
    GWPatchFXHarmonizerHarmonyValue.class,
    GWPatchFXHarmonizerVoiceValue.class,
    GWPatchFXHumanizerModeValue.class,
    GWPatchFXHumanizerVowelValue.class,
    GWPatchFXOctaveTypeValue.class,
    GWPatchFXOvertoneOutputModeValue.class,
    GWPatchFXPhaserHighCutValue.class,
    GWPatchFXPhaserLowCutValue.class,
    GWPatchFXPhaserStageValue.class,
    GWPatchFXPhaserTypeValue.class,
    GWPatchFXPhaserWaveformValue.class,
    GWPatchFXPitchShiftModeValue.class,
    GWPatchFXPitchShiftVoiceValue.class,
    GWPatchFXRotarySpeedValue.class,
    GWPatchFXSBendPitchValue.class,
    GWPatchFXTWahFilterValue.class,
    GWPatchFXTWahPolarityValue.class,
    GWPatchFXTypeValue.class,
    GWPatchLedType1Value.class,
    GWPatchLedType2Value.class,
    GWPatchMstDelayDrumEchoHeadValue.class,
    GWPatchMstDelayDTypeValue.class,
    GWPatchMstDelayDualModeValue.class,
    GWPatchMstDelayHeadValue.class,
    GWPatchMstDelayHighCutValue.class,
    GWPatchMstDelaySelectorValue.class,
    GWPatchMstDelayStageValue.class,
    GWPatchMstDelayTwistModeValue.class,
    GWPatchMstDelayTypeValue.class,
    GWPatchNSDetectValue.class,
    GWPatchNum1FunctionValue.class,
    GWPatchNum2FunctionValue.class,
    GWPatchNum3FunctionValue.class,
    GWPatchNum4FunctionValue.class,
    GWPatchNum5FunctionValue.class,
    GWPatchPedalFXTypeValue.class,
    GWPatchPedalFXWahTypeValue.class,
    GWPatchPreampGainValue.class,
    GWPatchPreampTypeValue.class,
    GWPatchReverbModeValue.class,
    GWPatchReverbTTypeValue.class,
    GWPatchReverbTypeValue.class,
    GWTunerModeValue.class,
    GWTunerTypeValue.class
  };

  @TestFactory
  public Stream<DynamicTest> testToFromIntIdentity()
  {
    return Stream.of(ENUMERATIONS)
      .map(GWEnumerationsTest::toFromIntIdentity);
  }

  private static DynamicTest toFromIntIdentity(
    final Class<?> enumClass)
  {
    return DynamicTest.dynamicTest(
      "testToFromIntIdentity_" + enumClass.getName(),
      () -> {
        final var values =
          enumClass.getMethod("values");
        final var ofInt =
          enumClass.getMethod("ofInt", int.class);
        final var toInt =
          enumClass.getMethod("toInt");

        final Object[] valuesResult =
          (Object[]) values.invoke(enumClass);

        for (final var v : valuesResult) {
          final int i =
            (int) toInt.invoke(v);
          final var r =
            ofInt.invoke(enumClass, i);

          assertEquals(v, r);
        }
      });
  }

  @TestFactory
  public Stream<DynamicTest> testLabelsUnique()
  {
    return Stream.of(ENUMERATIONS)
      .map(GWEnumerationsTest::labelUnique);
  }

  private static DynamicTest labelUnique(
    final Class<?> enumClass)
  {
    return DynamicTest.dynamicTest(
      "testLabelUnique_" + enumClass.getName(),
      () -> {
        final var values =
          enumClass.getMethod("values");
        final var label =
          enumClass.getMethod("label");

        final Object[] valuesResult =
          (Object[]) values.invoke(enumClass);

        final var labels = new HashSet<String>();
        for (final var v : valuesResult) {
          labels.add((String) label.invoke(v));
        }

        assertEquals(
          valuesResult.length,
          labels.size()
        );
      });
  }
}
