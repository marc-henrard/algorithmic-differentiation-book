/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.analysis;

import java.util.function.Function;

import marc.henrard.book.algorithmicdifferentiation.example.AdStarter;
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
 * Analyzes the performance of Algorithmic Differentiation for the starter function. 
 * The implementations covered include forward and adjoint on one side and manual and automatic on the other side.
 * The results of this analysis are described in the book in Sections 2.3 and Section XXX.
 */
public class AdStarterAnalysis {

  /* Five sets of data for test purposed. */
  static private final double[][] A = {
    {0.0, 1.0, 2.0, 3.0 },
    {1.0, 2.0, 3.0, 4.0 },
    {0.5, 0.1, -0.5, 0.0 },
    {2.0, 2.0, 2.0, 2.0 },
    {5.0, 2.0, -5.0, 6.0 } };
  /* The number of test data. */
  static private final int NB_TESTS = A.length;
  /* The shift for the finite difference derivative tests. */
  static private final double EPSILON = 1.0E-6;

  /** Compare the performance between algorithmic differentiation and finite difference. */
  @SuppressWarnings("unused")
  @Test(enabled = true)
  public void derivativesPerformance() {
    /* Number of repetitions (for warm-up). */
    int nbRep = 3;
    /* To measure the execution time (in milliseconds). */
    long startTime, endTime;
    /* The number of runs for one performance measurement. */
    int nbTest = 100_000;

    for (int looprep = 0; looprep < nbRep; looprep++) { // Start repetitions
      System.out.println("Starter example performance review: run " + looprep);
      
      startTime = System.currentTimeMillis();
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          double f = AdStarter.f(A[looptest]);
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " function: " + (endTime - startTime) + " ms");
      // Performance note: f: 04-Aug-2015: On Mac Book Pro 2.6 GHz Intel Core i7: 109 ms for 100,000 functions.

      startTime = System.currentTimeMillis();
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          double[] dFwd = FiniteDifferenceFirstOrder.differentiate(new fFunction(), A[looptest], EPSILON,
              FiniteDifferenceSchemes.FORWARD);
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " forward: " + (endTime - startTime) + " ms");
      // Performance note: f - FD forward: 04-Aug-2015: On Mac Book Pro 2.6 GHz Intel Core i7: 570 ms for 100,000 functions.

      startTime = System.currentTimeMillis();
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          double[] dBac = FiniteDifferenceFirstOrder.differentiate(new fFunction(), A[looptest], EPSILON,
              FiniteDifferenceSchemes.BACKWARD);
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " backward: " + (endTime - startTime) + " ms");
      // Performance note: f - FD backward: 04-Aug-2015: On Mac Book Pro 2.6 GHz Intel Core i7: 580 ms for 100,000 functions.

      startTime = System.currentTimeMillis();
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          double[] dSym = FiniteDifferenceFirstOrder.differentiate(new fFunction(), A[looptest], EPSILON,
              FiniteDifferenceSchemes.SYMMETRICAL);
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " symmetrical: " + (endTime - startTime) + " ms");
      // Performance note: f - FD Symmetrical: 04-Aug-2015: On Mac Book Pro 2.6 GHz Intel Core i7: 910 ms for 100,000 functions.

      startTime = System.currentTimeMillis();
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          double[] d3Or = FiniteDifferenceFirstOrder.differentiate(new fFunction(), A[looptest], EPSILON,
              FiniteDifferenceSchemes.FOURTH_ORDER);
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " 4rd order: " + (endTime - startTime) + " ms");
      // Performance note: f - FD Third order: 04-Aug-2015: On Mac Book Pro 2.6 GHz Intel Core i7: 1830 ms for 100,000 functions.

      startTime = System.currentTimeMillis();
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          DoubleDerivatives dAd = AdStarter.f_Sad(A[looptest]);
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " SAD: " + (endTime - startTime) + " ms");
      // Performance note: f - Adjoint AD: 04-Aug-2015: On Mac Book Pro 2.6 GHz Intel Core i7: 350 ms for 100,000 functions.

      startTime = System.currentTimeMillis();
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          DoubleDerivatives dAd = AdStarter.f_Sad_Optimized(A[looptest]);
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " SAD Optimized: " + (endTime - startTime) + " ms");
   // Performance note: f - Standard AD Optimized: 04-Aug-2015: On Mac Book Pro 2.6 GHz Intel Core i7: 190 ms for 100,000 functions.
      
      startTime = System.currentTimeMillis();
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          DoubleDerivatives dAd = AdStarter.f_Aad(A[looptest]);
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " AAD: " + (endTime - startTime) + " ms");
      // Performance note: f - Adjoint AD: 04-Aug-2015: On Mac Book Pro 2.6 GHz Intel Core i7: 225 ms for 100,000 functions.

      startTime = System.currentTimeMillis();
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          DoubleDerivatives dAd2 = AdStarter.f_Aad_Optimized(A[looptest]);
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " AAD Optimized: " + (endTime - startTime) + " ms");
      // Performance note: f - AAD Optimized: 04-Aug-2015: On Mac Book Pro 2.6 GHz Intel Core i7: 170 ms for 100,000 functions.

      startTime = System.currentTimeMillis();
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          DoubleSad[] aSad = DoubleSad.init(A[looptest]);
          DoubleSad dAd = AdStarter.f_Sad_Automatic(aSad);
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " SAD Automatic: " + (endTime - startTime) + " ms");
      // Performance note: f - Standard AD Automatic: 04-Aug-2015: On Mac Book Pro 2.6 GHz Intel Core i7: 335 ms for 100,000 functions.
      
      startTime = System.currentTimeMillis();
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          TapeAad tape = new TapeAad();
          DoubleAad[] a = new DoubleAad[4];
          for(int loopi=0; loopi<4; loopi++) {
            int va = tape.addEntry(new TapeEntryAad(OperationTypeAad.INPUT, A[looptest][loopi]));
            a[loopi] = new DoubleAad(A[looptest][loopi], va);
          }
          DoubleAad dAd2 = AdStarter.f_Aad_Automatic(a, tape);
          TapeUtils.interpret(tape);
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " AAD Automatic: " + (endTime - startTime) + " ms");
      // Performance note: f - Adjoint AD Automatic: 04-Aug-2015: On Mac Book Pro 2.6 GHz Intel Core i7: 395 ms for 100,000 functions.
      // 250 ms recording only (no interpretation).
      
    } // End repetition
  } 
  
}

/** Inner class to write f as a Function1D. */
class fFunction implements Function<double[], Double> {

  @Override
  public Double apply(double[] x) {
    return AdStarter.f(x);
  }
  
}