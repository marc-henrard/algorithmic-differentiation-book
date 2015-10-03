/**
 * Copyright (C) 2015 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.finance.data;

import static org.testng.AssertJUnit.assertEquals;

import java.util.function.Function;

import marc.henrard.book.algorithmicdifferentiation.data.InterpolationDataDouble;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleDerivatives;
import marc.henrard.book.algorithmicdifferentiation.utils.FiniteDifferenceFirstOrder;
import marc.henrard.book.algorithmicdifferentiation.utils.FiniteDifferenceSchemes;

import org.testng.annotations.Test;
import org.testng.internal.junit.ArrayAsserts;

/**
 * Tests {@link BlackSmileStrike}.
 */
public class BlackSmileStrikeTest {

  static private final double[][] DATA = { {0.01, 0.01}, {0.05, 0.04},
      {0.0075, 0.0125}, {0.055, 0.054}, {0.01, 0.09}};
  static private final int NB_TESTS = DATA.length;
  static private final double EPSILON = 1.0E-6;
  static private final double TOLERANCE_VOL = 1.0E-10;
  static private final double TOLERANCE_DELTA = 1.0E-6;

  static private final double[] DATA_NODES_STRIKE = {0.0050, 0.01, 0.02, 0.03, 0.04, 0.10};
  static private final double[] DATA_VALUES = {0.30, 0.24, 0.20, 0.22, 0.26, 0.27};
  static private final InterpolationDataDouble DATA_SMILE = new InterpolationDataDouble(DATA_NODES_STRIKE, DATA_VALUES);
  static private final BlackSmileDescription SMILE_DES_STRIKE =
      new BlackSmileStrike(DATA_SMILE);

  /** Tests that all implementations return the same value. */
  @Test
  public void valuesCorrectness() {
    for (int looptest = 0; looptest < NB_TESTS; looptest++) {
      double vol = SMILE_DES_STRIKE.volatility(DATA[looptest][0], DATA[looptest][1]);
      DoubleDerivatives volAad = SMILE_DES_STRIKE.volatilityParameterSensitivity(DATA[looptest][0], DATA[looptest][1]);
      assertEquals("BlackSmileStrike: vol", vol, volAad.value(), TOLERANCE_VOL);
    }
  }

  /** Tests that AAD implementation returns a similar value as FD. */
  @Test
  public void derivatives() {
    for (int looptest = 0; looptest < NB_TESTS; looptest++) {
      SmileStrikeFn fn = new SmileStrikeFn(DATA_NODES_STRIKE, DATA[looptest][0], DATA[looptest][1]);
      double[] d =
          FiniteDifferenceFirstOrder.differentiate(fn, DATA_VALUES, EPSILON, FiniteDifferenceSchemes.SYMMETRICAL);
      DoubleDerivatives volAad = SMILE_DES_STRIKE.volatilityParameterSensitivity(DATA[looptest][0], DATA[looptest][1]);
      ArrayAsserts.assertArrayEquals("BlackSmileStrike: derivatives", d, volAad.derivatives(), TOLERANCE_DELTA);
    }
  }

  /** Inner class to write volatility as a Function of interpolated values. */
  class SmileStrikeFn implements Function<double[], Double> {

    private final double nodes[];
    private final double strike;
    private final double forward;

    public SmileStrikeFn(double[] nodes, double strike, double forward) {
      this.nodes = nodes;
      this.strike = strike;
      this.forward = forward;
    }

    @Override
    public Double apply(double[] x) {
      InterpolationDataDouble dataSmile = new InterpolationDataDouble(nodes, x);
      BlackSmileDescription smile = new BlackSmileStrike(dataSmile);
      return smile.volatility(strike, forward);
    }

  }

}
