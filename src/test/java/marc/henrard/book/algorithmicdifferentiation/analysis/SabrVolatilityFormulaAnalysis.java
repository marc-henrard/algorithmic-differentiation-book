/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.analysis;

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

/**
 * Analyzes the Black formula and its Algorithmic Differentiation implementations.
 */
public class SabrVolatilityFormulaAnalysis {
  
  // forward, alpha, beta, rho, nu, strike, expiry
  static private final double[][] DATA = { 
    {0.02, 0.05, 0.50, -0.25, 0.50, 0.03, 1.00},  
    {0.05, 0.10, 0.40, 0.00, 0.40, 0.05, 2.00},  
    {0.10, 0.02, 0.60, 0.25, 0.30, 0.20, 0.10},  
    {0.05, 0.05, 0.30, -0.25, 0.60, 0.03, 5.50},  
    {0.06, 0.05, 0.30, -0.25, 0.60, 0.03, 10.00} };
  static private final int NB_TESTS = DATA.length;
  static private final double EPSILON = 1.0E-6;

  /** Compare the performance between different algorithmic differentiation implementations. */
  @SuppressWarnings("unused")
  @Test(enabled = true)
  public void derivativesPerformance() {

    long startTime, endTime;
    int nbTest = 100_000;
    int nbRep = 5;
    long hotspot = 0;

    for (int looprep = 0; looprep < nbRep; looprep++) { // Start repetitions
      System.out.println("SABR volatility formula - performance review : run " + looprep);

      startTime = System.currentTimeMillis();
      hotspot = 0;
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          double volatility = SabrVolatilityFormula.volatility(DATA[looptest][0], DATA[looptest][1],
              DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], DATA[looptest][5], DATA[looptest][6]);
          hotspot += (int) (volatility * 10);
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + (5 * nbTest) + " SABR volatility: " + (endTime - startTime) + " ms ... " + hotspot);
      // Performance note: volatility: 24-Dec-2014: On Mac Book Pro 2.6 GHz Intel Core i7: 35 ms for 5x100,000 functions.

      startTime = System.currentTimeMillis();
      hotspot = 0;
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          double[] d = FiniteDifferenceFirstOrder.differentiate(new SabrVol(),
              DATA[looptest], EPSILON, FiniteDifferenceSchemes.FORWARD);
          hotspot += d.length;
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + 5 * nbTest + " SABR volatility (FD Fwd): " + (endTime - startTime) + " ms ..." + hotspot);
      // Performance note: volatility FD fwd: 24-Dec-2014: On Mac Book Pro 2.6 GHz Intel Core i7: 630 ms for 5x100,000 functions.

      startTime = System.currentTimeMillis();
      hotspot = 0;
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          DoubleDerivatives volatility = SabrVolatilityFormula.volatility_Aad(DATA[looptest][0], DATA[looptest][1],
              DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], DATA[looptest][5], DATA[looptest][6]);
          hotspot += volatility.derivatives().length;
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + 5 * nbTest + " SABR volatility (AAD): " + (endTime - startTime) + " ms ..." + hotspot);
      // Performance note: volatility AAD: 24-Dec-2014: On Mac Book Pro 2.6 GHz Intel Core i7: 130 ms for 5x100,000 functions.

      startTime = System.currentTimeMillis();
      hotspot = 0;
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          TapeAad tape = new TapeAad();
          DoubleAad[] dataAad = new DoubleAad[7];
          for (int loopi = 0; loopi < 7; loopi++) {
            int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.INPUT, DATA[looptest][loopi]));
            dataAad[loopi] = new DoubleAad(DATA[looptest][loopi], index);
          }
          DoubleAad volatility = SabrVolatilityFormula.volatility_Aad_Automatic(dataAad[0], dataAad[1],
              dataAad[2], dataAad[3], dataAad[4], dataAad[5], dataAad[6], tape);
          TapeUtils.interpret(tape);
          double[] d = TapeUtils.extractDerivatives(tape);
          hotspot += d.length;
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + 5 * nbTest + " SABR volatility (AAD Automatic): " + (endTime - startTime) + " ms ..." + hotspot);
      // Performance note: volatility AAD Automatic (no interpret/interpret): 
      //   24-Dec-2014: On Mac Book Pro 2.6 GHz Intel Core i7: xxx/615 ms for 5x100,000 functions.

      startTime = System.currentTimeMillis();
      hotspot = 0;
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          TapeAad tape = new TapeAad();
          DoubleAad[] dataAad = new DoubleAad[7];
          for (int loopi = 0; loopi < 7; loopi++) {
            int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.INPUT, DATA[looptest][loopi]));
            dataAad[loopi] = new DoubleAad(DATA[looptest][loopi], index);
          }
          DoubleAad volatility = SabrVolatilityFormula.volatility_Aad_Automatic2(dataAad[0], dataAad[1],
              dataAad[2], dataAad[3], dataAad[4], dataAad[5], dataAad[6], tape);
          TapeUtils.interpret(tape);
          double[] d = TapeUtils.extractDerivatives(tape);
          hotspot += d.length;
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + 5 * nbTest + " SABR volatility (AAD Automatic2): " + (endTime - startTime) + " ms ..." + hotspot);
      // Performance note: volatility AAD Automatic (no interpret/interpret): 
      //   24-Dec-2014: On Mac Book Pro 2.6 GHz Intel Core i7: XXX/XXX ms for 5x200,000 functions.
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