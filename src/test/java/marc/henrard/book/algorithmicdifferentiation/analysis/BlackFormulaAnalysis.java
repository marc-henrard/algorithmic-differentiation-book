/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.analysis;

import java.util.function.Function;

import marc.henrard.book.algorithmicdifferentiation.finance.formula.BlackFormula;
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

/**
 * Analyzes the Black formula and its Algorithmic Differentiation implementations.
 * The results of this analysis are described in the book in Sections 3.1 and Section XXX.
 * <p>
 * The mathematical library underlying is <a href="https://dst.lbl.gov/ACSSoftware/colt/index.html">Colt</a>
 */
public class BlackFormulaAnalysis {
  
  // Forward / volatility / Numeraire / Strike / Expiry
  static private final double[][] DATA = { 
    {1.0, 0.50, 1.0, 1.0, 1.0}, 
    {0.05, 0.20, 0.90, 0.04, 2.0}, 
    {100.0, 0.15, 0.75, 111.1, 5.0}, 
    {0.0010, 0.99, 0.99, 0.0015, 1.25}, 
    {1234.0, 0.25, 0.99, 1432.0, 10.0} };
  static private final int NB_TESTS = DATA.length;
  
  static private final double EPSILON = 1.0E-6;

  /** Compare the performance between algorithmic differentiation and finite difference. 
   * @throws MathException */
  @SuppressWarnings("unused")
  @Test(enabled = true)
  public void derivativesPerformance() {

    long startTime, endTime;
    final int nbTest = 100_000;
    int nbRep = 5;
    long hotspot = 0; // To ensure that no code is hotspot away

    for (int looprep = 0; looprep < nbRep; looprep++) { // Start repetitions
      System.out.println("Black formula - performance review (Colt mathematical library): run " + looprep);

      startTime = System.currentTimeMillis();
      hotspot = 0;
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          double call = BlackFormula.price(DATA[looptest][0], DATA[looptest][1],
              DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], true);
          double put = BlackFormula.price(DATA[looptest][0], DATA[looptest][1],
              DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], false);
          hotspot += (int) (call + put);
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " function: " + (endTime - startTime) + " ms ... " + hotspot);
      // Performance note: price: 04-Aug-2015: On Mac Book Pro 2.6 GHz Intel Core i7: 70 ms for 5x2x100,000 functions.

      startTime = System.currentTimeMillis();
      hotspot = 0;
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          double[] call = FiniteDifferenceFirstOrder.differentiate(new blackCall(), 
              DATA[looptest], EPSILON, FiniteDifferenceSchemes.FORWARD);
          double[] put = FiniteDifferenceFirstOrder.differentiate(new blackPut(), 
              DATA[looptest], EPSILON, FiniteDifferenceSchemes.FORWARD);
          hotspot += call.length + put.length;
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " function + FD fwd: " + (endTime - startTime) + " ms ... " + hotspot);
      // Performance note: price: 04-Aug-2015: On Mac Book Pro 2.6 GHz Intel Core i7: 525 ms for 5x2x100,000 functions.

      startTime = System.currentTimeMillis();
      hotspot = 0;
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          DoubleDerivatives callPriceAd = BlackFormula.price_Sad(DATA[looptest][0], DATA[looptest][1],
              DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], true);
          DoubleDerivatives putPriceAd = BlackFormula.price_Sad(DATA[looptest][0], DATA[looptest][1],
              DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], false);
          hotspot += callPriceAd.derivatives().length + putPriceAd.derivatives().length;
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " function + SAD: " + (endTime - startTime) + " ms ... " + hotspot);
      // Performance note: price SAD: 04-Aug-2015: On Mac Book Pro 2.6 GHz Intel Core i7: 235 ms for 5x2x100,000 derivatives.

      startTime = System.currentTimeMillis();
      hotspot = 0;
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          DoubleSad callPriceAd = BlackFormula.price_Sad_Automatic(DATA[looptest][0], DATA[looptest][1],
              DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], true);
          DoubleSad putPriceAd = BlackFormula.price_Sad_Automatic(DATA[looptest][0], DATA[looptest][1],
              DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], false);
          hotspot += callPriceAd.getNbDerivatives() + putPriceAd.getNbDerivatives();
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " function + SAD Automatic: " + (endTime - startTime) + " ms ... " + hotspot);
      // Performance note: price SAD: 04-Aug-2015: On Mac Book Pro 2.6 GHz Intel Core i7: 575 ms for 5x2x100,000 derivatives.

      startTime = System.currentTimeMillis();
      hotspot = 0;
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          DoubleDerivatives callPriceAd = BlackFormula.price_Aad(DATA[looptest][0], DATA[looptest][1],
              DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], true);
          DoubleDerivatives putPriceAd = BlackFormula.price_Aad(DATA[looptest][0], DATA[looptest][1],
              DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], false);
          hotspot += callPriceAd.derivatives().length + putPriceAd.derivatives().length;
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " function + AAD: " + (endTime - startTime) + " ms ... " + hotspot);
      // Performance note: price AD: 04-Aug-2015: On Mac Book Pro 2.6 GHz Intel Core i7: 185 ms for 5x2x100,000 derivatives.

      startTime = System.currentTimeMillis();
      hotspot = 0;
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          DoubleDerivatives callPriceAd = BlackFormula.price_Aad_Optimized(DATA[looptest][0], DATA[looptest][1],
              DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], true);
          DoubleDerivatives putPriceAd = BlackFormula.price_Aad_Optimized(DATA[looptest][0], DATA[looptest][1],
              DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], false);
          hotspot += callPriceAd.derivatives().length + putPriceAd.derivatives().length;
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " function + AAD Optimized: " + (endTime - startTime) + " ms ... " + hotspot);
      // Performance note: price AD Optimized: 04-Aug-2015: On Mac Book Pro 2.6 GHz Intel Core i7: 125 ms for 5x2x100,000 derivatives.

      startTime = System.currentTimeMillis();
      hotspot = 0;
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          TapeAad tapeCall = new TapeAad();
          DoubleAad[][] dataAad = new DoubleAad[5][2];
          TapeAad tapePut = new TapeAad();
          for (int loopi = 0; loopi < 5; loopi++) {
            int va0 = tapeCall.addEntry(new TapeEntryAad(OperationTypeAad.INPUT, DATA[looptest][loopi]));
            dataAad[loopi][0] = new DoubleAad(DATA[looptest][loopi], va0);
            int va1 = tapePut.addEntry(new TapeEntryAad(OperationTypeAad.INPUT, DATA[looptest][loopi]));
            dataAad[loopi][1] = new DoubleAad(DATA[looptest][loopi], va1);
          }
          DoubleAad callPriceAd = BlackFormula.price_Aad_Automatic(dataAad[0][0], dataAad[1][0],
              dataAad[2][0], dataAad[3][0], dataAad[4][0], true, tapeCall);
          double[] dCall = TapeUtils.interpret(tapeCall);
          DoubleAad putPriceAd = BlackFormula.price_Aad_Automatic(dataAad[0][1], dataAad[1][1],
              dataAad[2][1], dataAad[3][1], dataAad[4][1], false, tapePut);
          double[] dPut = TapeUtils.interpret(tapePut);
          hotspot += dCall.length + dPut.length;
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " function + AAD Automatic: " + (endTime - startTime) + " ms ... " + hotspot);
      // Performance note: price AD Automatic: 04-Aug-2015: On Mac Book Pro 2.6 GHz Intel Core i7: 660 ms for 5x2x100,000 derivatives.
      // XXX ms recording only (no interpretation).
      
      startTime = System.currentTimeMillis();
      hotspot = 0;
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          TapeAad tapeCall = new TapeAad();
          DoubleAad[][] dataAad = new DoubleAad[5][2];
          TapeAad tapePut = new TapeAad();
          for (int loopi = 0; loopi < 5; loopi++) {
            int va0 = tapeCall.addEntry(new TapeEntryAad(OperationTypeAad.INPUT, DATA[looptest][loopi]));
            dataAad[loopi][0] = new DoubleAad(DATA[looptest][loopi], va0);
            int va1 = tapePut.addEntry(new TapeEntryAad(OperationTypeAad.INPUT, DATA[looptest][loopi]));
            dataAad[loopi][1] = new DoubleAad(DATA[looptest][loopi], va1);
          }
          DoubleAad callPriceAd = BlackFormula.price_Aad_Automatic2(dataAad[0][0], dataAad[1][0],
              dataAad[2][0], dataAad[3][0], dataAad[4][0], true, tapeCall);
          double[] dCall = TapeUtils.interpret(tapeCall);
          DoubleAad putPriceAd = BlackFormula.price_Aad_Automatic2(dataAad[0][1], dataAad[1][1],
              dataAad[2][1], dataAad[3][1], dataAad[4][1], false, tapePut);
          double[] dPut = TapeUtils.interpret(tapePut);
          hotspot += dCall.length + dPut.length;
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " function + AAD Automatic 2: " + (endTime - startTime) + " ms ... " + hotspot);
      // Performance note: price AD Automatic 2: 03-Oct-2015: On Mac Book Pro 2.6 GHz Intel Core i7: 530 ms for 5x2x100,000 derivatives.
      
    } // End repetition

  }
  
}

/** Inner class to write Black call price as a Function. */
class blackCall implements Function<double[], Double> {

  @Override
  public Double apply(double[] x) {
      return BlackFormula.price(x[0], x[1], x[2], x[3], x[4], true);
  }
  
}

/** Inner class to write Black put price as a Function. */
class blackPut implements Function<double[], Double> {

  @Override
  public Double apply(double[] x) {
      return BlackFormula.price(x[0], x[1], x[2], x[3], x[4], false);
  }
  
}
