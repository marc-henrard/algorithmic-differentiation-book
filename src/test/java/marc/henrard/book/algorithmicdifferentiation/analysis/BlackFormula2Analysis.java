/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.analysis;

import marc.henrard.book.algorithmicdifferentiation.finance.BlackFormula2;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeAad;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeEntryAad;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeUtils;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleAad;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleDerivatives;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleSad;
import marc.henrard.book.algorithmicdifferentiation.type.OperationTypeAad;

import org.testng.annotations.Test;

/**
 * Analyzes the Black formula and its Algorithmic Differentiation implementations.
 * The results of this analysis are described in the book in Sections 3.1 and Section XXX.
 * <p>
 * The mathematical library underlying is 
 * <a href="http://commons.apache.org/proper/commons-math/">Apache Commons Mathematics Library</a>
 */
public class BlackFormula2Analysis {
  
  // Forward / volatility / Numeraire / Strike / Expiry
  static private final double[][] DATA = { 
    {1.0, 0.50, 1.0, 1.0, 1.0}, 
    {0.05, 0.20, 0.90, 0.04, 2.0}, 
    {100.0, 0.15, 0.75, 111.1, 5.0}, 
    {0.0010, 0.99, 0.99, 0.0015, 1.25}, 
    {1234.0, 0.25, 0.99, 1432.0, 10.0} };
  static private final int NB_TESTS = DATA.length;

  /** Compare the performance between algorithmic differentiation and finite difference. 
   * @throws MathException */
  @SuppressWarnings("unused")
  @Test(enabled = true)
  public void derivativesPerformance() {

    long startTime, endTime;
    final int nbTest = 100_000;
    int nbRep = 3;

    for (int looprep = 0; looprep < nbRep; looprep++) { // Start repetitions
      System.out.println("Black formula - performance review (Apache Commons Mathematics library): run " + looprep);

      startTime = System.currentTimeMillis();
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          double call = BlackFormula2.price(DATA[looptest][0], DATA[looptest][1],
              DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], true);
          double put = BlackFormula2.price(DATA[looptest][0], DATA[looptest][1],
              DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], false);
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " function: " + (endTime - startTime) + " ms");
      // Performance note: price: 04-Aug-2015: On Mac Book Pro 2.6 GHz Intel Core i7: 515 ms for 5x2x100,000 functions.

      startTime = System.currentTimeMillis();
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          DoubleDerivatives callPriceAd = BlackFormula2.price_Sad(DATA[looptest][0], DATA[looptest][1],
              DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], true);
          DoubleDerivatives putPriceAd = BlackFormula2.price_Sad(DATA[looptest][0], DATA[looptest][1],
              DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], false);
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " function + SAD: " + (endTime - startTime) + " ms");
      // Performance note: price SAD: 04-Aug-2015: On Mac Book Pro 2.6 GHz Intel Core i7: 560 ms for 5x2x100,000 derivatives.

      startTime = System.currentTimeMillis();
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          DoubleSad callPriceAd = BlackFormula2.price_Sad_Automatic(DATA[looptest][0], DATA[looptest][1],
              DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], true);
          DoubleSad putPriceAd = BlackFormula2.price_Sad_Automatic(DATA[looptest][0], DATA[looptest][1],
              DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], false);
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " function + SAD Automatic: " + (endTime - startTime) + " ms");
      // Performance note: price SAD: 04-Aug-2015: On Mac Book Pro 2.6 GHz Intel Core i7: 935 ms for 5x2x100,000 derivatives.

      startTime = System.currentTimeMillis();
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          DoubleDerivatives callPriceAd = BlackFormula2.price_Aad(DATA[looptest][0], DATA[looptest][1],
              DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], true);
          DoubleDerivatives putPriceAd = BlackFormula2.price_Aad(DATA[looptest][0], DATA[looptest][1],
              DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], false);
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " function + AAD: " + (endTime - startTime) + " ms");
      // Performance note: price AD: 04-Aug-2015: On Mac Book Pro 2.6 GHz Intel Core i7: 540 ms for 5x2x100,000 derivatives.

      startTime = System.currentTimeMillis();
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          DoubleDerivatives callPriceAd = BlackFormula2.price_Aad_Optimized(DATA[looptest][0], DATA[looptest][1],
              DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], true);
          DoubleDerivatives putPriceAd = BlackFormula2.price_Aad_Optimized(DATA[looptest][0], DATA[looptest][1],
              DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], false);
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " function + AAD Optimized: " + (endTime - startTime) + " ms");
      // Performance note: price AD Optimized: 04-Aug-2015: On Mac Book Pro 2.6 GHz Intel Core i7: 520 ms for 5x2x100,000 derivatives.

      startTime = System.currentTimeMillis();
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
          DoubleAad callPriceAd = BlackFormula2.price_Aad_Automatic(dataAad[0][0], dataAad[1][0],
              dataAad[2][0], dataAad[3][0], dataAad[4][0], true, tapeCall);
          TapeUtils.interpret(tapeCall);
          DoubleAad putPriceAd = BlackFormula2.price_Aad_Automatic(dataAad[0][1], dataAad[1][1],
              dataAad[2][1], dataAad[3][1], dataAad[4][1], false, tapePut);
          TapeUtils.interpret(tapePut);
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " function + AAD Automatic: " + (endTime - startTime) + " ms");
      // Performance note: price AD Optimized: 04-Aug-2015: On Mac Book Pro 2.6 GHz Intel Core i7: 970 ms for 5x2x100,000 derivatives.
      // XXX ms recording only (no interpretation).
      
    } // End repetition

  }
  
}
