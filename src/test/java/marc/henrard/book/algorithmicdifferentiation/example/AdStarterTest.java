/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.example;

import static org.testng.AssertJUnit.assertEquals;

import java.util.function.Function;

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
 * Tests {@link AdStarter}.
 * Analyzes the Algorithmic Differentiation starter function. 
 */
public class AdStarterTest {
  
  static private final double[][] A = { 
    {0.0, 1.0, 2.0, 3.0}, 
    {1.0, 2.0, 3.0, 4.0}, 
    {0.5, 0.1, -0.5, 0.0}, 
    {2.0, 2.0, 2.0, 2.0}, 
    {5.0, 2.0, -5.0, 6.0} };
  static private final int NB_TESTS = A.length;
  static private final double EPSILON = 1.0E-6;
  static private final double TOLERANCE_VALUE = 1.0E-6;
  static private final double TOLERANCE_DELTA_1 = 1.0E-2;
  static private final double TOLERANCE_DELTA_2 = 1.0E-7;
  
  /** Tests the implementation of the AD by comparison to finite difference. */
  @Test
  public void derivativesCorrectness() {
    for(int looptest=0; looptest < NB_TESTS; looptest++) {
      double f = AdStarter.f(A[looptest]);
      double[] dFwd = FiniteDifferenceFirstOrder.differentiate(new f_Function(), A[looptest], EPSILON, 
          FiniteDifferenceSchemes.FORWARD);
      double[] dBac = FiniteDifferenceFirstOrder.differentiate(new f_Function(), A[looptest], EPSILON, 
          FiniteDifferenceSchemes.BACKWARD);
      double[] dSym = FiniteDifferenceFirstOrder.differentiate(new f_Function(), A[looptest], EPSILON, 
          FiniteDifferenceSchemes.SYMMETRICAL);
      double[] d3Or = FiniteDifferenceFirstOrder.differentiate(new f_Function(), A[looptest], EPSILON, 
          FiniteDifferenceSchemes.FOURTH_ORDER);
      DoubleDerivatives dSad = AdStarter.f_Sad(A[looptest]);
      DoubleDerivatives dSad2 = AdStarter.f_Sad_Optimized(A[looptest]);
      DoubleSad[] aSad = DoubleSad.init(A[looptest]);
      DoubleSad dSadA = AdStarter.f_Sad_Automatic(aSad);
      DoubleDerivatives dAad = AdStarter.f_Aad(A[looptest]);
      DoubleDerivatives dAad2 = AdStarter.f_Aad_Optimized(A[looptest]);
      assertEquals("adStarterAnalysis " + looptest, f, dSad.value(), TOLERANCE_VALUE);
      ArrayAsserts.assertArrayEquals("adStarterAnalysis " + looptest, dFwd, dSad.derivatives(), TOLERANCE_DELTA_1);
      ArrayAsserts.assertArrayEquals("adStarterAnalysis " + looptest, dBac, dSad.derivatives(), TOLERANCE_DELTA_1);
      ArrayAsserts.assertArrayEquals("adStarterAnalysis " + looptest, dSym, dSad.derivatives(), TOLERANCE_DELTA_2);
      ArrayAsserts.assertArrayEquals("adStarterAnalysis " + looptest, d3Or, dSad.derivatives(), TOLERANCE_DELTA_2);
      assertEquals("adStarterAnalysis " + looptest, f, dSad2.value(), TOLERANCE_VALUE);
      ArrayAsserts.assertArrayEquals("adStarterAnalysis " + looptest, dSad.derivatives(), dSad2.derivatives(), TOLERANCE_DELTA_2);
      assertEquals("adStarterAnalysis " + looptest, f, dSadA.value(), TOLERANCE_VALUE);
      ArrayAsserts.assertArrayEquals("adStarterAnalysis " + looptest, dSad.derivatives(), dSadA.derivatives(), TOLERANCE_DELTA_2);
      assertEquals("adStarterAnalysis " + looptest, f, dAad.value(), TOLERANCE_VALUE);
      ArrayAsserts.assertArrayEquals("adStarterAnalysis " + looptest, dSad.derivatives(), dAad.derivatives(), TOLERANCE_DELTA_2);
      assertEquals("adStarterAnalysis " + looptest, f, dAad2.value(), TOLERANCE_VALUE);
      ArrayAsserts.assertArrayEquals("adStarterAnalysis " + looptest, dAad.derivatives(), dAad2.derivatives(), TOLERANCE_DELTA_2);
    }
  }

  /** Tests the correctness of the tape record, interpretation and derivatives on the AdStarter function. */
  @Test
  public void derivativesCorrectnessTape() {
    for (int looptest = 0; looptest < NB_TESTS; looptest++) {
      TapeAad tape = new TapeAad();
      DoubleAad[] a = new DoubleAad[4];
      for (int loopi = 0; loopi < 4; loopi++) {
        int va = tape.addEntry(new TapeEntryAad(OperationTypeAad.INPUT, A[looptest][loopi]));
        a[loopi] = new DoubleAad(A[looptest][loopi], va);
      }
      DoubleDerivatives dAad = AdStarter.f_Aad(A[looptest]);
      DoubleAad dAadA = AdStarter.f_Aad_Automatic(a, tape);
      assertEquals("adStarterAnalysis " + looptest, dAad.value(), dAadA.value(), TOLERANCE_VALUE);
      TapeUtils.interpret(tape); 
      double[] d = TapeUtils.extractDerivatives(tape);
      ArrayAsserts.assertArrayEquals("adStarterAnalysis " + looptest, dAad.derivatives(),  d, TOLERANCE_DELTA_2);
    }
  }
  
}

/** Inner class to write f as a Function. */
class f_Function implements Function<double[], Double> {

  @Override
  public Double apply(double[] x) {
    return AdStarter.f(x);
  }
  
}
