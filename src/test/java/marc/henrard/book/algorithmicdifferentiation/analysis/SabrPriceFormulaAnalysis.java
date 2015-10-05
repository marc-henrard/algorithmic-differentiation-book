/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.analysis;

import marc.henrard.book.algorithmicdifferentiation.finance.formula.SabrPriceFormula;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeAad;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeEntryAad;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeUtils;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleAad;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleDerivatives;
import marc.henrard.book.algorithmicdifferentiation.type.OperationTypeAad;

import org.testng.annotations.Test;

/**
 * Analyzes the Black formula and its Algorithmic Differentiation implementations.
 */
public class SabrPriceFormulaAnalysis {

  // forward, alpha, beta, rho, nu, numeraire, strike, expiry
  static private final double[][] DATA = {
      {0.02, 0.05, 0.50, -0.25, 0.50, 1.00, 0.03, 1.00},
      {0.05, 0.10, 0.40, 0.00, 0.40, 0.99, 0.05, 2.00},
      {0.10, 0.02, 0.60, 0.25, 0.30, 10.00, 0.20, 0.10},
      {0.05, 0.05, 0.30, -0.25, 0.60, 0.10, 0.03, 5.50},
      {0.04, 0.05, 0.30, -0.15, 0.60, 0.10, 0.03, 10.00}};
  static private final int NB_TESTS = DATA.length;
  static private final int NB_INPUT = DATA[0].length;

  /** Compare the performance between different algorithmic differentiation implementations. */
  @SuppressWarnings("unused")
  @Test(enabled = true)
  public void derivativesPerformance() {

    long startTime, endTime;
    final int nbTest = 100_000;
    int nbRep = 5;
    long hotspot = 0; // To ensure that no code is hotspot away
    boolean[] callPut = {true, false};

    for (int looprep = 0; looprep < nbRep; looprep++) { // Start repetitions
      System.out.println("SABR formula - performance review: run " + looprep);

      startTime = System.currentTimeMillis();
      hotspot = 0;
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          for (int looppc = 0; looppc < 2; looppc++) {
            double price = SabrPriceFormula.price(DATA[looptest][0], DATA[looptest][1],
                DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], DATA[looptest][5], DATA[looptest][6],
                DATA[looptest][7], callPut[looppc]);
            hotspot += (int) (price * 10);
          }
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " SABR price: " + (endTime - startTime) + " ms ... " + hotspot);
      // Performance: price: 02-Oct-2015: Mac Book Pro 2.6 GHz Intel Core i7: 260 ms for 5x2x100,000 functions.

      startTime = System.currentTimeMillis();
      hotspot = 0;
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          for (int looppc = 0; looppc < 2; looppc++) {
            DoubleDerivatives price = SabrPriceFormula.price_Aad(DATA[looptest][0], DATA[looptest][1],
                DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], DATA[looptest][5], DATA[looptest][6],
                DATA[looptest][7], callPut[looppc]);
            hotspot += price.derivatives().length;
          }
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " SABR price (Manual AAD): " + (endTime - startTime) + " ms ... " + hotspot);
      // Performance: price Manual AAD: 25-Dec-2014: Mac Book Pro 2.6 GHz Intel Core i7: 470 ms for 5x2x100,000 functions.

      startTime = System.currentTimeMillis();
      hotspot = 0;
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          for (int looppc = 0; looppc < 2; looppc++) {
            TapeAad tape = new TapeAad();
            DoubleAad[] dataAad = new DoubleAad[NB_INPUT];
            for (int loopi = 0; loopi < NB_INPUT; loopi++) {
              int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.INPUT, DATA[looptest][loopi]));
              dataAad[loopi] = new DoubleAad(DATA[looptest][loopi], index);
            }
            DoubleAad price = SabrPriceFormula.price_Aad_Automatic(dataAad[0], dataAad[1],
                dataAad[2], dataAad[3], dataAad[4], dataAad[5], dataAad[6], dataAad[7], callPut[looppc], tape);
            double[] d = TapeUtils.interpret(tape);
            hotspot += d.length;
          }
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " SABR price (Automatic AAD): " + (endTime - startTime) + " ms ... " + hotspot);
      // Performance: price Automatic AAD (no interpret/interpret): 25-Dec-2014: Mac Book Pro 2.6 GHz Intel Core i7: 1300/1910 ms for 5x2x100,000 functions.

      startTime = System.currentTimeMillis();
      hotspot = 0;
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          for (int looppc = 0; looppc < 2; looppc++) {
            TapeAad tape = new TapeAad();
            DoubleAad[] dataAad = new DoubleAad[NB_INPUT];
            for (int loopi = 0; loopi < NB_INPUT; loopi++) {
              int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.INPUT, DATA[looptest][loopi]));
              dataAad[loopi] = new DoubleAad(DATA[looptest][loopi], index);
            }
            DoubleAad price = SabrPriceFormula.price_Aad_Automatic2(dataAad[0], dataAad[1],
                dataAad[2], dataAad[3], dataAad[4], dataAad[5], dataAad[6], dataAad[7], callPut[looppc], tape);
            double[] d = TapeUtils.interpret(tape);
            hotspot += d.length;
          }
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " SABR price (Automatic2 AAD): " + (endTime - startTime) + " ms ... " + hotspot);
      // Performance: price Automatic2 AAD (no interpret/interpret): 25-Dec-2014: Mac Book Pro 2.6 GHz Intel Core i7: XXX/XXX ms for 5x2x100,000 functions.

      startTime = System.currentTimeMillis();
      hotspot = 0;
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          for (int looppc = 0; looppc < 2; looppc++) {
            DoubleDerivatives price = SabrPriceFormula.price_Aad_Mixed_M_1(DATA[looptest][0], DATA[looptest][1],
                DATA[looptest][2], DATA[looptest][3], DATA[looptest][4], DATA[looptest][5], DATA[looptest][6],
                DATA[looptest][7], callPut[looppc]);
            hotspot += price.derivatives().length;
          }
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " SABR price (AAD Mixed M 1): " + (endTime - startTime) + " ms ... " + hotspot);
      // Performance note: price Mixed1 AAD: 25-Dec-2014: 
      //   On Mac Book Pro 2.6 GHz Intel Core i7: 1050 ms for 5x2x100,000 functions.

      startTime = System.currentTimeMillis();
      hotspot = 0;
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          for (int looppc = 0; looppc < 2; looppc++) {
            TapeAad tape = new TapeAad();
            DoubleAad[] dataAad = new DoubleAad[NB_INPUT];
            for (int loopi = 0; loopi < NB_INPUT; loopi++) {
              int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.INPUT, DATA[looptest][loopi]));
              dataAad[loopi] = new DoubleAad(DATA[looptest][loopi], index);
            }
            DoubleAad price = SabrPriceFormula.price_Aad_Mixed_A_1(dataAad[0], dataAad[1],
                dataAad[2], dataAad[3], dataAad[4], dataAad[5], dataAad[6], dataAad[7], callPut[looppc], tape);
            double[] d = TapeUtils.interpret(tape);
            hotspot += d.length;
          }
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " SABR price (AAD Mixed A 1): " + (endTime - startTime) + " ms ... " + hotspot);
      // Performance note: price Mixed2 AAD (no interpret/interpret): 25-Dec-2014: 
      //   On Mac Book Pro 2.6 GHz Intel Core i7: XXX/1720 ms for 5x2x100,000 functions.

      startTime = System.currentTimeMillis();
      hotspot = 0;
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          for (int looppc = 0; looppc < 2; looppc++) {
            TapeAad tape = new TapeAad();
            DoubleAad[] dataAad = new DoubleAad[NB_INPUT];
            for (int loopi = 0; loopi < NB_INPUT; loopi++) {
              int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.INPUT, DATA[looptest][loopi]));
              dataAad[loopi] = new DoubleAad(DATA[looptest][loopi], index);
            }
            DoubleAad price = SabrPriceFormula.price_Aad_Mixed_A_2(dataAad[0], dataAad[1],
                dataAad[2], dataAad[3], dataAad[4], dataAad[5], dataAad[6], dataAad[7], callPut[looppc], tape);
            double[] d = TapeUtils.interpret(tape);
            hotspot += d.length;
          }
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " SABR price (AAD Mixed A 2): " + (endTime - startTime) + " ms ... " + hotspot);
      // Performance note: price Mixed3 AAD (no interpret/interpret): 25-Dec-2014: 
      //   On Mac Book Pro 2.6 GHz Intel Core i7: XXX/1185 ms for 5x2x100,000 functions.
    }
  }

}
