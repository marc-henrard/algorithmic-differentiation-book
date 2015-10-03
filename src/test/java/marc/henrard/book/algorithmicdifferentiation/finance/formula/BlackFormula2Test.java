/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.finance.formula;

import static org.testng.AssertJUnit.assertEquals;

import java.util.function.Function;

import marc.henrard.book.algorithmicdifferentiation.finance.formula.BlackFormula2;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeAad;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeEntryAad;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeUtils;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleAad;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleDerivatives;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleSad;
import marc.henrard.book.algorithmicdifferentiation.type.OperationTypeAad;
import marc.henrard.book.algorithmicdifferentiation.utils.FiniteDifferenceFirstOrder;
import marc.henrard.book.algorithmicdifferentiation.utils.FiniteDifferenceSchemes;

import org.testng.annotations.Test;
import org.testng.internal.junit.ArrayAsserts;

/**
 * Tests {@link BlackFormula2} and its Algorithmic Differentiation implementations.
 * <p>
 * The mathematical library underlying is 
 * <a href="http://commons.apache.org/proper/commons-math/">Apache Commons Mathematics Library</a>
 */
public class BlackFormula2Test {
  
  // Forward / volatility / Numeraire / Strike / Expiry
  static private final double[][] DATA = { {1.0, 0.50, 1.0, 1.0, 1.0}, {0.05, 0.20, 0.90, 0.04, 2.0}, 
    {100.0, 0.15, 0.75, 111.1, 5.0}, {0.0010, 0.99, 0.99, 0.0015, 1.25}, {1234.0, 0.25, 0.99, 1432.0, 10.0} };
  static private final int NB_TESTS = DATA.length;
  static private final double EPSILON = 1.0E-6;
  static private final double TOLERANCE_PRICE = 1.0E-10;
  static private final double TOLERANCE_DELTA = 1.0E-6;
  
  /** Tests the implementation of the AD by comparison to finite difference. 
   * @throws MathException */
  @Test
  public void derivativesCorrectness() {
    boolean[] callPut = {true, false };
    for (int looptest = 0; looptest < NB_TESTS; looptest++) {
      for (int looppc = 0; looppc < 2; looppc++) {
        double[] d3Call = FiniteDifferenceFirstOrder.differentiate(callPut[looppc] ? new blackCall() : new blackPut(), 
            DATA[looptest], EPSILON, FiniteDifferenceSchemes.FOURTH_ORDER);
        DoubleDerivatives callPriceSad = BlackFormula2.price_Sad(DATA[looptest][0], DATA[looptest][1],
            DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], callPut[looppc]);
        ArrayAsserts.assertArrayEquals("BlackFormula SAD " + looptest, d3Call, callPriceSad.derivatives(), TOLERANCE_DELTA);
        DoubleSad callPriceSadA = BlackFormula2.price_Sad_Automatic(DATA[looptest][0], DATA[looptest][1],
            DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], callPut[looppc]);
        ArrayAsserts.assertArrayEquals("BlackFormula SAD " + looptest, d3Call, callPriceSadA.derivatives(), TOLERANCE_DELTA);
        DoubleDerivatives callPriceAad = BlackFormula2.price_Aad(DATA[looptest][0], DATA[looptest][1],
            DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], callPut[looppc]);
        ArrayAsserts.assertArrayEquals("BlackFormula AAD " + looptest, d3Call, callPriceAad.derivatives(), TOLERANCE_DELTA);
        DoubleDerivatives callPriceAadOpt = BlackFormula2.price_Aad_Optimized(DATA[looptest][0], DATA[looptest][1],
            DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], callPut[looppc]);
        ArrayAsserts.assertArrayEquals("BlackFormula AAD " + looptest, d3Call, callPriceAadOpt.derivatives(), TOLERANCE_DELTA);
      }
    }
  }
  
  static private final DoubleAad[][][] DATA_AAD = new DoubleAad[NB_TESTS][5][2];
  static private final TapeAad[][] TAPE = new TapeAad[NB_TESTS][2];
  static {
    for(int loopt=0; loopt<NB_TESTS; loopt++) {
      TAPE[loopt][0] = new TapeAad();
      TAPE[loopt][1] = new TapeAad();
      for(int loopi=0; loopi<5; loopi++) {
        int va0 = TAPE[loopt][0].addEntry(new TapeEntryAad(OperationTypeAad.INPUT, DATA[loopt][loopi]));
        DATA_AAD[loopt][loopi][0] = new DoubleAad(DATA[loopt][loopi], va0);
        int va1 = TAPE[loopt][1].addEntry(new TapeEntryAad(OperationTypeAad.INPUT, DATA[loopt][loopi]));
        DATA_AAD[loopt][loopi][1] = new DoubleAad(DATA[loopt][loopi], va1);
      }
    }
  }
  
  @Test
  public void derivativesCorrectnessTape() {
    boolean[] callPut = {true, false };
    for (int looptest = 0; looptest < NB_TESTS; looptest++) {
      for (int looppc = 0; looppc < 2; looppc++) {
        DoubleDerivatives callPriceAad_Optimized = BlackFormula2.price_Aad_Optimized(DATA[looptest][0], 
            DATA[looptest][1], DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], callPut[looppc]);
        DoubleAad callPriceAad_Automatic = BlackFormula2.price_Aad_Automatic(DATA_AAD[looptest][0][looppc], 
            DATA_AAD[looptest][1][looppc], DATA_AAD[looptest][2][looppc], DATA_AAD[looptest][3][looppc], 
            DATA_AAD[looptest][4][looppc], callPut[looppc], TAPE[looptest][looppc]);
        assertEquals("BlackFormula AAD " + looptest, 
            callPriceAad_Optimized.value(), callPriceAad_Automatic.value(), TOLERANCE_DELTA);
        TapeUtils.interpret(TAPE[looptest][looppc]);
        double[] d = TapeUtils.extractDerivatives(TAPE[looptest][looppc]);
        ArrayAsserts.assertArrayEquals("BlackFormula AAD " + looptest, callPriceAad_Optimized.derivatives(), d, TOLERANCE_PRICE);
      }
    }
  }
  
}

/** Inner class to write Black call price as a Function. */
class blackCall implements Function<double[], Double> {

  @Override
  public Double apply(double[] x) {
      return BlackFormula2.price(x[0], x[1], x[2], x[3], x[4], true);
  }
  
}

/** Inner class to write Black put price as a Function. */
class blackPut implements Function<double[], Double> {

  @Override
  public Double apply(double[] x) {
      return BlackFormula2.price(x[0], x[1], x[2], x[3], x[4], false);
  }
  
}
