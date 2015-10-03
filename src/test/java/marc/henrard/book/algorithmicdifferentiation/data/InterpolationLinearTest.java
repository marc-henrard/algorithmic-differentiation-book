/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.data;

import static org.testng.AssertJUnit.assertEquals;

import java.util.function.Function;

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
 * Tests {@link InterpolationLinear}
 */
public class InterpolationLinearTest {
  
  /* The interpolator (linear) */
  private static final Interpolation INTERPOLATION = new InterpolationLinear();
  
  static private final double[][] DATA_NODES = { 
    {1.0, 2.0, 3.0, 4.0, 5.0}, 
    {0.00, 0.25, 0.50, 1.00, 2.00, 5.00, 10.0, 30.0}, 
    {0.0, 2.0, 4.0, 6.0}, 
    {-10.0, -5.0, 0.0, 5.0, 10.0}, 
    {-100.0, 100.0, 1000.0, 10000.0} };
  static private final double[][] DATA_VALUES = { 
    {1.0, 2.0, 3.0, 4.0, 5.0}, 
    {0.0000, 0.0010, 0.0050, 0.0100, 0.0125, 0.0150, 0.0150, 0.0125}, 
    {1.0, 1.0, 1.0, 1.0}, 
    {1.0, -1.0, 1.0, -1.0, 1.0}, 
    {230.0, 231.0, 222.0, 123.0} };
  static private final int NB_TESTS = DATA_NODES.length;
  static private final InterpolationDataDouble[] DATA = new InterpolationDataDouble[NB_TESTS];
  static {
    for(int loopt=0; loopt<NB_TESTS; loopt++) {
      DATA[loopt] = new InterpolationDataDouble(DATA_NODES[loopt], DATA_VALUES[loopt]);
    }
  }
  static private final double[][] X = {
    {1.0, 1.5, 2.0, 2.01, 2.99, 3.00, 5.0},
    {0.10, 7.50},
    {1.0, 5.0},
    {-9.0, 0.0, 9.99},
    {-3.14, 6000.0} };

  static private final double EPSILON = 1.0E-6;
  static private final double TOLERANCE_VALUE = 1.0E-10;
  static private final double TOLERANCE_DELTA = 1.0E-6;
  
  @Test
  public void value() {
    for (int looptest = 0; looptest < NB_TESTS; looptest++) {
      for (int loopv = 0; loopv < X[looptest].length; loopv++) {
        int ilower = lowerIndex(X[looptest][loopv], DATA_NODES[looptest]);
        double alpha = (X[looptest][loopv] - DATA_NODES[looptest][ilower])
            / (DATA_NODES[looptest][ilower + 1] - DATA_NODES[looptest][ilower]);
        double interpExpected = DATA_VALUES[looptest][ilower] * (1.0d - alpha) +
            DATA_VALUES[looptest][ilower + 1] * alpha;
        double interpComputed = INTERPOLATION.interpolate(X[looptest][loopv], DATA[looptest]);
        assertEquals("InterpolationLinear " + looptest + " - " + loopv,
            interpExpected, interpComputed, TOLERANCE_VALUE);
      }
    }
  }

  @Test
  public void derivativesX() {
    for (int looptest = 3; looptest < NB_TESTS; looptest++) {
      for (int loopv = 0; loopv < X[looptest].length; loopv++) {
        double derComputed = INTERPOLATION.derivativeX(X[looptest][loopv], DATA[looptest]);
        double value0 = INTERPOLATION.interpolate(X[looptest][loopv], DATA[looptest]);
        double derExpected;
        if (X[looptest][loopv] == DATA_NODES[looptest][0]) { // First node
          double valueP = INTERPOLATION.interpolate(X[looptest][loopv] + EPSILON, DATA[looptest]);
          derExpected = (valueP - value0) / EPSILON;
        } else {
          double valueM = INTERPOLATION.interpolate(X[looptest][loopv] - EPSILON, DATA[looptest]);
          derExpected = (value0 - valueM) / EPSILON;
        }
        assertEquals("InterpolationLinear " + looptest + " - " + loopv + " / " + X[looptest][loopv],
            derComputed, derExpected, TOLERANCE_DELTA);
      }
    }
  }

  @Test
  public void derivativesNodeYValues() {
    for (int looptest = 0; looptest < NB_TESTS; looptest++) {
      for (int loopv = 0; loopv < X[looptest].length; loopv++) {
        double interpComputed = INTERPOLATION.interpolate(X[looptest][loopv], DATA[looptest]);
        double[] d4 = FiniteDifferenceFirstOrder.differentiate(
            new InterpolationFn(X[looptest][loopv], DATA_NODES[looptest]), 
            DATA_VALUES[looptest], EPSILON, FiniteDifferenceSchemes.FOURTH_ORDER);
        DoubleDerivatives interp = INTERPOLATION.interpolate_Aad(X[looptest][loopv], DATA[looptest]);
        assertEquals("InterpolationLinear" + looptest + " - " + loopv, 
            interpComputed, interp.value(), TOLERANCE_VALUE);
        ArrayAsserts.assertArrayEquals("InterpolationLinear" + looptest + " - " + loopv, 
            d4, interp.derivatives(), TOLERANCE_DELTA);
      }
    }
  }

  @Test
  public void derivativesNodeYValuesTape() {
    for (int looptest = 0; looptest < NB_TESTS; looptest++) {
      for (int loopv = 0; loopv < X[looptest].length; loopv++) {
        TapeAad tape = new TapeAad();
        DoubleAad[] nodeValuesAad = new DoubleAad[DATA_NODES[looptest].length];
        for (int loopi = 0; loopi < DATA_NODES[looptest].length; loopi++) {
          int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.INPUT, DATA_VALUES[looptest][loopi]));
          nodeValuesAad[loopi] = new DoubleAad(DATA_VALUES[looptest][loopi], index);
        }
        int indexx = tape.addEntry(new TapeEntryAad(OperationTypeAad.INPUT, X[looptest][loopv]));
        DoubleAad x = new DoubleAad(X[looptest][loopv], indexx);
        InterpolationDataDoubleAad data = new InterpolationDataDoubleAad(DATA_NODES[looptest], nodeValuesAad);
        DoubleDerivatives interpAad = INTERPOLATION.interpolate_Aad(X[looptest][loopv], DATA[looptest]);
        double interpP = INTERPOLATION.interpolate(X[looptest][loopv]+EPSILON, DATA[looptest]);
        DoubleAad interpAadAutomatic = INTERPOLATION.interpolate_Aad_Automatic(x, data, tape);
        assertEquals("InterpolationLinear" + looptest + " - " + loopv,
            interpAad.value(), interpAadAutomatic.value(), TOLERANCE_VALUE);
        double[] d = TapeUtils.interpret(tape);
        for (int loopi = 0; loopi < DATA_NODES[looptest].length; loopi++) {
          assertEquals("InterpolationLinear" + looptest + " - " + loopv,
              interpAad.derivatives()[loopi], d[loopi], TOLERANCE_DELTA);
        }
        assertEquals((interpP - interpAad.value()) / EPSILON, d[DATA_NODES[looptest].length], TOLERANCE_DELTA);
      }
    }
  }
  
  private static int lowerIndex(double value, double[] nodes) {
    if(value == nodes[nodes.length - 1]) {
      return nodes.length - 2;
    }
    int i = nodes.length - 1;
    while(nodes[i] > value) {
      i--;
    }
    return i;
  }
  
}

/* Inner class to write the linear interpolation price as a Function. */
class InterpolationFn implements Function<double[], Double> {

  private final double x;
  private final double[] nodes;
  /* The interpolator (linear) */
  private static final Interpolation INTERPOLATION = new InterpolationLinear();
  
  public InterpolationFn(double x, double[] nodes) {
    this.x = x;
    this.nodes = nodes;
  }
  
  @Override
  public Double apply(double[] nodeValues) {
    InterpolationDataDouble data = new InterpolationDataDouble(nodes, nodeValues);
    return INTERPOLATION.interpolate(x, data);
  }
  
}
