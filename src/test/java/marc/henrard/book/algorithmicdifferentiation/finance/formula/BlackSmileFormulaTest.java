package marc.henrard.book.algorithmicdifferentiation.finance.formula;

import static org.testng.AssertJUnit.assertEquals;

import java.util.function.Function;

import marc.henrard.book.algorithmicdifferentiation.data.Interpolation;
import marc.henrard.book.algorithmicdifferentiation.data.InterpolationDataDouble;
import marc.henrard.book.algorithmicdifferentiation.data.InterpolationDataDoubleAad;
import marc.henrard.book.algorithmicdifferentiation.data.InterpolationLinear;
import marc.henrard.book.algorithmicdifferentiation.finance.data.BlackSmileDescription;
import marc.henrard.book.algorithmicdifferentiation.finance.data.BlackSmileDescriptionAad;
import marc.henrard.book.algorithmicdifferentiation.finance.data.BlackSmileStrike;
import marc.henrard.book.algorithmicdifferentiation.finance.data.BlackSmileStrikeAad;
import marc.henrard.book.algorithmicdifferentiation.finance.formula.BlackFormula;
import marc.henrard.book.algorithmicdifferentiation.finance.formula.BlackSmileFormula;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeAad;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeUtils;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleAad;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleDerivatives;
import marc.henrard.book.algorithmicdifferentiation.utils.FiniteDifferenceFirstOrder;
import marc.henrard.book.algorithmicdifferentiation.utils.FiniteDifferenceSchemes;

import org.testng.annotations.Test;
import org.testng.internal.junit.ArrayAsserts;

/**
 * Tests {@link BlackSmileFormula}.
 */
public class BlackSmileFormulaTest {

  /* The interpolator (linear) */
  private static final Interpolation INTERPOLATION = new InterpolationLinear();
  private static final BlackSmileFormula FORMULA = BlackSmileFormula.DEFAULT;

  static private final double[] DATA_NODES_STRIKE = {0.0050, 0.01, 0.02, 0.03, 0.04};
  static private final double[] DATA_VALUES = {0.30, 0.24, 0.20, 0.22, 0.26};
  static private final InterpolationDataDouble DATA_SMILE = new InterpolationDataDouble(DATA_NODES_STRIKE, DATA_VALUES);
  static private final BlackSmileDescription SMILE_DES_STRIKE =
      new BlackSmileStrike(DATA_SMILE);
  // Forward /  Numeraire / Strike / Expiry
  static private final double[][] DATA_OTHER = { {0.01, 1.0, 0.0125, 1.0},
      {0.0125, 1.0, 0.0125, 1.0}, {0.0150, 1.0, 0.0125, 1.0}, {0.025, 0.90, 0.0275, 10.0}, {0.025, 0.90, 0.0225, 10.0}};
  static private final int NB_TESTS = DATA_OTHER.length;

  static private final double EPSILON = 1.0E-6;
  static private final double TOLERANCE_PRICE = 1.0E-10;
  static private final double TOLERANCE_DELTA = 1.0E-6;

  @Test
  public void stickyStrike() {
    boolean[] callPut = {true, false};
    for (int looptest = 0; looptest < NB_TESTS; looptest++) {
      double volatility = INTERPOLATION.interpolate(DATA_OTHER[looptest][2], DATA_SMILE);
      for (int looppc = 0; looppc < 2; looppc++) {
        DoubleDerivatives priceComputed = FORMULA.price_Aad_StickyStrike(DATA_OTHER[looptest][0], SMILE_DES_STRIKE,
            DATA_OTHER[looptest][1], DATA_OTHER[looptest][2], DATA_OTHER[looptest][3], callPut[looppc]);
        DoubleDerivatives priceExpected = BlackFormula.price_Aad(DATA_OTHER[looptest][0], volatility,
            DATA_OTHER[looptest][1], DATA_OTHER[looptest][2], DATA_OTHER[looptest][3], callPut[looppc]);
        assertEquals("Black Smile Formula - sticky strike",
            priceExpected.value(), priceComputed.value(), TOLERANCE_PRICE);
        ArrayAsserts.assertArrayEquals("Black Smile Formula - sticky strike",
            priceExpected.derivatives(), priceComputed.derivatives(), TOLERANCE_DELTA);
      }
    }
  }

  @Test
  public void stickySimpleMoneyness() {
    boolean[] callPut = {true, false};
    for (int looptest = 0; looptest < NB_TESTS; looptest++) {
      double volatility = INTERPOLATION.interpolate(DATA_OTHER[looptest][2], DATA_SMILE);
      for (int looppc = 0; looppc < 2; looppc++) {
        DoubleDerivatives priceComputed = FORMULA.price_Aad_StickySimpleMoney(DATA_OTHER[looptest][0], SMILE_DES_STRIKE,
            DATA_OTHER[looptest][1], DATA_OTHER[looptest][2], DATA_OTHER[looptest][3], callPut[looppc]);
        DoubleDerivatives priceExpected = BlackFormula.price_Aad(DATA_OTHER[looptest][0], volatility,
            DATA_OTHER[looptest][1], DATA_OTHER[looptest][2], DATA_OTHER[looptest][3], callPut[looppc]);
        assertEquals("Black Smile Formula - sticky simple moneyness",
            priceExpected.value(), priceComputed.value(), TOLERANCE_PRICE);
        for (int loopr = 1; loopr <= 4; loopr++) {
          assertEquals("Black Smile Formula - sticky simple moneyness",
              priceExpected.derivatives()[loopr], priceComputed.derivatives()[loopr], TOLERANCE_DELTA);
        }
        BlackSM blackSimpleMoney = new BlackSM(DATA_OTHER[looptest][0], DATA_OTHER[looptest][1],
            DATA_OTHER[looptest][2], DATA_OTHER[looptest][3], DATA_SMILE, callPut[looppc]);
        double[] deltaSSM = FiniteDifferenceFirstOrder.differentiate(blackSimpleMoney,
            new double[] {DATA_OTHER[looptest][0]}, EPSILON, FiniteDifferenceSchemes.FOURTH_ORDER);
        assertEquals("Black Smile Formula - sticky simple moneyness - " + looptest + ", " + looppc,
            deltaSSM[0], priceComputed.derivatives()[0], TOLERANCE_PRICE);
      }
    }
  }

  @Test
  public void automaticAad() {
    boolean[] callPut = {true, false};
    for (int looptest = 0; looptest < NB_TESTS; looptest++) {
      for (int looppc = 0; looppc < 2; looppc++) {
        TapeAad tape = new TapeAad();
        DoubleAad forward = DoubleAad.input(DATA_OTHER[looptest][0], tape);
        DoubleAad numeraire = DoubleAad.input(DATA_OTHER[looptest][1], tape);
        DoubleAad strike = DoubleAad.input(DATA_OTHER[looptest][2], tape);
        DoubleAad expiry = DoubleAad.input(DATA_OTHER[looptest][3], tape);
        DoubleAad[] dataValuesAad = new DoubleAad[DATA_VALUES.length];
        for (int i = 0; i < DATA_VALUES.length; i++) {
          dataValuesAad[i] = DoubleAad.input(DATA_VALUES[i], tape);
        }
        InterpolationDataDoubleAad smileDataAad = new InterpolationDataDoubleAad(DATA_NODES_STRIKE, dataValuesAad);
        BlackSmileDescriptionAad smileAad = new BlackSmileStrikeAad(smileDataAad);
        DoubleAad priceAad = FORMULA.price_Aad_Automatic(forward, smileAad, numeraire, strike, expiry, callPut[looppc], tape);
        Black2 fn = new Black2(SMILE_DES_STRIKE, callPut[looppc]);
        double[] dExpected = FiniteDifferenceFirstOrder
            .differentiate(fn, DATA_OTHER[looptest], EPSILON, FiniteDifferenceSchemes.SYMMETRICAL);
        DoubleDerivatives price = FORMULA.price_Aad_StickyStrike(DATA_OTHER[looptest][0], SMILE_DES_STRIKE, 
            DATA_OTHER[looptest][1], DATA_OTHER[looptest][2], DATA_OTHER[looptest][3], callPut[looppc]);
        assertEquals("BlackSmileFormula: price_Aad_Automatic", price.value(), priceAad.value(), TOLERANCE_PRICE);
        double[] d = TapeUtils.interpret(tape);
        for (int i = 0; i < 4; i++) { // Fwd/Num/Strike/Exp
          assertEquals("BlackSmileFormula: price_Aad_Automatic - fwd", dExpected[i], d[i], TOLERANCE_DELTA);
        }
        DoubleDerivatives weights = SMILE_DES_STRIKE.volatilityParameterSensitivity(DATA_OTHER[looptest][2],
            DATA_OTHER[looptest][0]);
        for (int i = 0; i < weights.derivatives().length; i++) { // Vega
          assertEquals("BlackSmileFormula: price_Aad_Automatic-vega",
              weights.derivatives()[i] * price.derivatives()[1], d[4 + i], TOLERANCE_DELTA);
        }
      }
    }
  }

/* Inner class to write Black price as a Function. */
  class BlackSM implements Function<double[], Double> {

    private final double forward;
    private final double numeraire;
    private final double strike;
    private final double expiry;
    private final InterpolationDataDouble dataSmile;
    private final boolean isCall;

    public BlackSM(double forward, double numeraire, double strike, double expiry,
        InterpolationDataDouble dataSmile, boolean isCall) {
      this.forward = forward;
      this.numeraire = numeraire;
      this.strike = strike;
      this.expiry = expiry;
      this.dataSmile = dataSmile;
      this.isCall = isCall;
    }

    @Override
    public Double apply(double[] x) {
      double forwardMod = x[0];
      double moneynessMod = strike - forwardMod;
      double strikeMod = moneynessMod + forward;
      double volatilityMod = INTERPOLATION.interpolate(strikeMod, dataSmile);
      return BlackFormula.price(forwardMod, volatilityMod, numeraire, strike,
          expiry, isCall);
    }
  }

/* Inner class to write Black price as a Function. */
  class Black2 implements Function<double[], Double> {

    private final BlackSmileDescription smile;
    private final boolean isCall;

    public Black2(BlackSmileDescription dataSmile, boolean isCall) {
      this.smile = dataSmile;
      this.isCall = isCall;
    }

    @Override
    public Double apply(double[] x) {
      return FORMULA.price(x[0], smile, x[1], x[2], x[3], isCall);
    }
  }

}
