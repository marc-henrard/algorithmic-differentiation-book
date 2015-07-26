/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.example;


import java.util.function.Function;

import marc.henrard.utils.FiniteDifferenceFirstOrder;
import marc.henrard.utils.FiniteDifferenceSchemes;

import org.testng.annotations.Test;
import org.testng.internal.junit.ArrayAsserts;

import com.opengamma.strata.collect.tuple.Pair;

/**
 * Analyzes the Algorithmic Differentiation starter function. 
 * Both Standard and Forward Algorithmic Differentiation are analyzed.
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
  /* The tolerance for the first order derivatives computations comparison with finite difference. */
  static private final double TOLERANCE_DELTA_1 = 1.0E-2;
  /* The tolerance for the first order derivatives computations comparison between AD versions. */
  static private final double TOLERANCE_DELTA_2 = 1.0E-7;
  
  /** Tests the implementation of the AD by comparison to finite difference. */
  @Test
  public void derivativesCorrectness() {
    for(int looptest=0; looptest < NB_TESTS; looptest++) {
      double[] dFwd = FiniteDifferenceFirstOrder.differentiate(new fFunction(), A[looptest], EPSILON, 
          FiniteDifferenceSchemes.FORWARD);
      double[] dBac = FiniteDifferenceFirstOrder.differentiate(new fFunction(), A[looptest], EPSILON, 
          FiniteDifferenceSchemes.BACKWARD);
      double[] dSym = FiniteDifferenceFirstOrder.differentiate(new fFunction(), A[looptest], EPSILON, 
          FiniteDifferenceSchemes.SYMMETRICAL);
      double[] d3Or = FiniteDifferenceFirstOrder.differentiate(new fFunction(), A[looptest], EPSILON, 
          FiniteDifferenceSchemes.FOURTH_ORDER);
      Pair<Double, double[]> dAad = AdStarter.f_Aad(A[looptest]);
      Pair<Double, double[]> dAad2 = AdStarter.f_Aad_Alternative(A[looptest]);
      Pair<Double, double[]> dSad = AdStarter.f_Sad(A[looptest]);
      ArrayAsserts.assertArrayEquals("adStarterAnalysis " + looptest, dFwd, dAad.getSecond(), TOLERANCE_DELTA_1);
      ArrayAsserts.assertArrayEquals("adStarterAnalysis " + looptest, dBac, dAad.getSecond(), TOLERANCE_DELTA_1);
      ArrayAsserts.assertArrayEquals("adStarterAnalysis " + looptest, dSym, dAad.getSecond(), TOLERANCE_DELTA_2);
      ArrayAsserts.assertArrayEquals("adStarterAnalysis " + looptest, d3Or, dAad.getSecond(), TOLERANCE_DELTA_2);
      ArrayAsserts.assertArrayEquals("adStarterAnalysis " + looptest, dAad.getSecond(), dAad2.getSecond(), TOLERANCE_DELTA_2);
      ArrayAsserts.assertArrayEquals("adStarterAnalysis " + looptest, dAad.getSecond(), dSad.getSecond(), TOLERANCE_DELTA_2);
    }
  }  

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
      System.out.println("Performance review: run " + looprep);
      
      startTime = System.currentTimeMillis();
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          double f = AdStarter.f(A[looptest]);
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " function: " + (endTime - startTime) + " ms");
      // Performance note: f: 8-Nov-2014: On Mac Book Pro 2.6 GHz Intel Core i7: 109 ms for 100,000 functions.

      startTime = System.currentTimeMillis();
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          double[] dFwd = FiniteDifferenceFirstOrder.differentiate(new fFunction(), A[looptest], EPSILON,
              FiniteDifferenceSchemes.FORWARD);
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " forward: " + (endTime - startTime) + " ms");
      // Performance note: f - FD forward: 8-Nov-2014: On Mac Book Pro 2.6 GHz Intel Core i7: 580 ms for 100,000 functions.

      startTime = System.currentTimeMillis();
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          double[] dBac = FiniteDifferenceFirstOrder.differentiate(new fFunction(), A[looptest], EPSILON,
              FiniteDifferenceSchemes.BACKWARD);
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " backward: " + (endTime - startTime) + " ms");
      // Performance note: f - FD backward: 8-Nov-2014: On Mac Book Pro 2.6 GHz Intel Core i7: 590 ms for 100,000 functions.

      startTime = System.currentTimeMillis();
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          double[] dSym = FiniteDifferenceFirstOrder.differentiate(new fFunction(), A[looptest], EPSILON,
              FiniteDifferenceSchemes.SYMMETRICAL);
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " symmetrical: " + (endTime - startTime) + " ms");
      // Performance note: f - FD Symmetrical: 8-Nov-2014: On Mac Book Pro 2.6 GHz Intel Core i7: 950 ms for 100,000 functions.

      startTime = System.currentTimeMillis();
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          double[] d3Or = FiniteDifferenceFirstOrder.differentiate(new fFunction(), A[looptest], EPSILON,
              FiniteDifferenceSchemes.FOURTH_ORDER);
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " 4rd order: " + (endTime - startTime) + " ms");
      // Performance note: f - FD Third order: 8-Nov-2014: On Mac Book Pro 2.6 GHz Intel Core i7: 1850 ms for 100,000 functions.

      startTime = System.currentTimeMillis();
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          Pair<Double, double[]> dAd = AdStarter.f_Aad(A[looptest]);
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " AAD: " + (endTime - startTime) + " ms");
      // Performance note: f - Adjoint AD: 8-Nov-2014: On Mac Book Pro 2.6 GHz Intel Core i7: 225 ms for 100,000 functions.

      startTime = System.currentTimeMillis();
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          Pair<Double, double[]> dAd = AdStarter.f_Sad(A[looptest]);
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " SAD: " + (endTime - startTime) + " ms");
      // Performance note: f - Adjoint AD: 8-Nov-2014: On Mac Book Pro 2.6 GHz Intel Core i7: 350 ms for 100,000 functions.

      startTime = System.currentTimeMillis();
      for (int loopperf = 0; loopperf < nbTest; loopperf++) {
        for (int looptest = 0; looptest < NB_TESTS; looptest++) {
          Pair<Double, double[]> dAd2 = AdStarter.f_Aad_Alternative(A[looptest]);
        }
      }
      endTime = System.currentTimeMillis();
      System.out.println("  |--> " + nbTest + " AAD cache: " + (endTime - startTime) + " ms");
      // Performance note: f - Standard AD: 8-Nov-2014: On Mac Book Pro 2.6 GHz Intel Core i7: 170 ms for 100,000 functions.

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
