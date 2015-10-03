/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.finance.formula;

import static org.testng.AssertJUnit.assertEquals;

import java.util.function.Function;

import marc.henrard.book.algorithmicdifferentiation.finance.formula.SabrVolatilityFormula;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeAad;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeEntryAad;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeUtils;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleAad;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleDerivatives;
import marc.henrard.book.algorithmicdifferentiation.type.OperationTypeAad;
import marc.henrard.book.algorithmicdifferentiation.utils.FiniteDifferenceFirstOrder;
import marc.henrard.book.algorithmicdifferentiation.utils.FiniteDifferenceSchemes;

import org.testng.annotations.Test;
import org.testng.internal.junit.ArrayAsserts;

/**
 * Analyzes the Black formula and its Algorithmic Differentiation implementations.
 */
public class SabrVolatilityFormulaTest {
  
  // forward, alpha, beta, rho, nu, strike, expiry
  static private final double[][] DATA = { 
    {0.02, 0.05, 0.50, -0.25, 0.50, 0.03, 1.00},  
    {0.05, 0.10, 0.40, 0.00, 0.40, 0.05, 2.00},  
    {0.10, 0.02, 0.60, 0.25, 0.30, 0.20, 0.10},  
    {0.05, 0.05, 0.30, -0.25, 0.60, 0.03, 5.50} };
  static private final int NB_TESTS = DATA.length;
  static private final double EPSILON = 1.0E-6;
  static private final double TOLERANCE_PRICE = 1.0E-10;
  static private final double TOLERANCE_DELTA = 1.0E-5;
  
  /** Tests the implementation of the AD by comparison to finite difference. */
  @Test
  public void derivativesCorrectness() {
    for (int looptest = 0; looptest < NB_TESTS; looptest++) {
      double[] d4 = FiniteDifferenceFirstOrder.differentiate(new SabrVol(),
          DATA[looptest], EPSILON, FiniteDifferenceSchemes.FOURTH_ORDER);
      DoubleDerivatives priceAad = SabrVolatilityFormula.volatility_Aad(DATA[looptest][0], DATA[looptest][1],
          DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], DATA[looptest][5], DATA[looptest][6]);
      ArrayAsserts.assertArrayEquals("SabrVolatilityFormula AAD " + looptest, 
          d4, priceAad.derivatives(), TOLERANCE_DELTA);
    }
  }
  
  @Test
  public void derivativesCorrectnessTape() {
    for (int looptest = 0; looptest < NB_TESTS; looptest++) {
      TapeAad tape = new TapeAad();
      DoubleAad[] dataAad = new DoubleAad[7];
      for (int loopi = 0; loopi < 7; loopi++) {
        int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.INPUT, DATA[looptest][loopi]));
        dataAad[loopi] = new DoubleAad(DATA[looptest][loopi], index);
      }
      DoubleDerivatives priceAad = SabrVolatilityFormula.volatility_Aad(DATA[looptest][0], DATA[looptest][1],
          DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], DATA[looptest][5], DATA[looptest][6]);
      DoubleAad priceAadAutomatic = SabrVolatilityFormula.volatility_Aad_Automatic(
          dataAad[0], dataAad[1], dataAad[2], dataAad[3], dataAad[4], dataAad[5], dataAad[6], tape);
      assertEquals("SabrVolatilityFormula AAD " + looptest, 
          priceAad.value(), priceAadAutomatic.value(), TOLERANCE_PRICE);
      TapeUtils.interpret(tape);
      double[] dAadAutomatic = TapeUtils.extractDerivatives(tape);
      ArrayAsserts.assertArrayEquals("SabrVolatilityFormula AAD " + looptest,
          priceAad.derivatives(), dAadAutomatic, TOLERANCE_DELTA);
    }
  }
  
  @Test
  public void derivativesCorrectnessTape2() {
    for (int looptest = 0; looptest < NB_TESTS; looptest++) {
      TapeAad tape = new TapeAad();
      DoubleAad[] dataAad = new DoubleAad[7];
      for (int loopi = 0; loopi < 7; loopi++) {
        int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.INPUT, DATA[looptest][loopi]));
        dataAad[loopi] = new DoubleAad(DATA[looptest][loopi], index);
      }
      DoubleDerivatives priceAad = SabrVolatilityFormula.volatility_Aad(DATA[looptest][0], DATA[looptest][1],
          DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], DATA[looptest][5], DATA[looptest][6]);
      DoubleAad priceAadAutomatic = SabrVolatilityFormula.volatility_Aad_Automatic2(
          dataAad[0], dataAad[1], dataAad[2], dataAad[3], dataAad[4], dataAad[5], dataAad[6], tape);
      assertEquals("SabrVolatilityFormula AAD " + looptest, 
          priceAad.value(), priceAadAutomatic.value(), TOLERANCE_PRICE);
      TapeUtils.interpret(tape);
      double[] dAadAutomatic = TapeUtils.extractDerivatives(tape);
      ArrayAsserts.assertArrayEquals("SabrVolatilityFormula AAD " + looptest,
          priceAad.derivatives(), dAadAutomatic, TOLERANCE_DELTA);
    }
  }
  
}

/** Inner class to write SABR volatility as a Function1D. */
class SabrVol implements Function<double[], Double> {

  @Override
  public Double apply(double[] x) {
    return SabrVolatilityFormula.volatility(x[0], x[1], x[2], x[3], x[4], x[5], x[6]);
  }
  
}
