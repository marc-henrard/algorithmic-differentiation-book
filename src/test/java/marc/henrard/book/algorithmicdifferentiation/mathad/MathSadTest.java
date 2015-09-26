/**
 * Copyright (C) 2015 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.mathad;

import static org.testng.AssertJUnit.assertEquals;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleSad;

import org.testng.annotations.Test;

import cern.jet.random.Normal;

/**
 * Tests related to {@link MathSad}. 
 */
public class MathSadTest {

  /* Test data */
  private static final double VALUE_1 = 123.4d;
  private static final double VALUE_2 = 3.2;
  private static final double[] DERIVATIVES_1 = {1.1, 2.2, 3.3};
  private static final double[] DERIVATIVES_2 = {2.1, 3.2, 4.3};
  private static final DoubleSad OBJECT_1 = new DoubleSad(VALUE_1, DERIVATIVES_1);
  private static final DoubleSad OBJECT_2 = new DoubleSad(VALUE_2, DERIVATIVES_2);
  /* Tolerance for double comparison */
  private static final double TOLERANCE_DOUBLE = 1.0E-12;
  
  /** The normal distribution implementation. */
  private static final Normal NORMAL = new Normal(0.0d, 1.0d, null);

  
  @Test
  public void plus() {
    DoubleSad plus = MathSad.plus(OBJECT_1, OBJECT_2);
    assertEquals("DoubleSad: plus", VALUE_1 + VALUE_2, plus.value(), TOLERANCE_DOUBLE);
    assertEquals("DoubleSad: plus", DERIVATIVES_1.length, plus.getNbDerivatives());
    for (int i = 0; i < DERIVATIVES_1.length; i++) {
      assertEquals("DoubleSad: plus", DERIVATIVES_1[i] + DERIVATIVES_2[i], plus.derivatives()[i], TOLERANCE_DOUBLE);
    }
  }
  
  @Test
  public void minus() {
    DoubleSad minus = MathSad.minus(OBJECT_1, OBJECT_2);
    assertEquals("DoubleSad: minus", VALUE_1 - VALUE_2, minus.value(), TOLERANCE_DOUBLE);
    assertEquals("DoubleSad: minus", DERIVATIVES_1.length, minus.getNbDerivatives());
    for (int i = 0; i < DERIVATIVES_1.length; i++) {
      assertEquals("DoubleSad: minus", DERIVATIVES_1[i] - DERIVATIVES_2[i], minus.derivatives()[i], TOLERANCE_DOUBLE);
    }
  }
  
  @Test
  public void multipliedBy1() {
    DoubleSad mult = MathSad.multipliedBy(OBJECT_1, VALUE_2);
    assertEquals("DoubleSad: multiplyBy1", VALUE_1 * VALUE_2, mult.value(), TOLERANCE_DOUBLE);
    assertEquals("DoubleSad: multiplyBy1", DERIVATIVES_1.length, mult.getNbDerivatives());
    for (int i = 0; i < DERIVATIVES_1.length; i++) {
      assertEquals("DoubleSad: multiplyBy1", DERIVATIVES_1[i] * VALUE_2, mult.derivatives()[i], TOLERANCE_DOUBLE);
    }
  }

  @Test
  public void multipliedBy2() {
    DoubleSad mult = MathSad.multipliedBy(OBJECT_1, OBJECT_2);
    assertEquals("DoubleSad: multiplyBy2", VALUE_1 * VALUE_2, mult.value(), TOLERANCE_DOUBLE);
    assertEquals("DoubleSad: multiplyBy2", DERIVATIVES_1.length, mult.getNbDerivatives());
    for (int i = 0; i < DERIVATIVES_1.length; i++) {
      assertEquals("DoubleSad: multiplyBy1", DERIVATIVES_1[i] * VALUE_2 + DERIVATIVES_2[i] * VALUE_1,
          mult.derivatives()[i], TOLERANCE_DOUBLE);
    }
  }

  @Test
  public void dividedBy() {
    DoubleSad result = MathSad.dividedBy(OBJECT_1, OBJECT_2);
    assertEquals("DoubleSad: multiplyBy2", VALUE_1 / VALUE_2, result.value(), TOLERANCE_DOUBLE);
    assertEquals("DoubleSad: multiplyBy2", DERIVATIVES_1.length, result.getNbDerivatives());
    for (int i = 0; i < DERIVATIVES_1.length; i++) {
      assertEquals("DoubleSad: multiplyBy1", (DERIVATIVES_1[i] * VALUE_2 - DERIVATIVES_2[i] * VALUE_1) 
          / (VALUE_2 * VALUE_2), result.derivatives()[i], TOLERANCE_DOUBLE);
    }
  }

  @Test
  public void sin() {
    DoubleSad sin = MathSad.sin(OBJECT_1);
    assertEquals("DoubleSad: sin", Math.sin(VALUE_1), sin.value(), TOLERANCE_DOUBLE);
    assertEquals("DoubleSad: sin", DERIVATIVES_1.length, sin.getNbDerivatives());
    for (int i = 0; i < DERIVATIVES_1.length; i++) {
      assertEquals("DoubleSad: sin", Math.cos(VALUE_1) * DERIVATIVES_1[i], sin.derivatives()[i], TOLERANCE_DOUBLE);
    }
  }

  @Test
  public void cos() {
    DoubleSad cos = MathSad.cos(OBJECT_1);
    assertEquals("DoubleSad: cos", Math.cos(VALUE_1), cos.value(), TOLERANCE_DOUBLE);
    assertEquals("DoubleSad: cos", DERIVATIVES_1.length, cos.getNbDerivatives());
    for (int i = 0; i < DERIVATIVES_1.length; i++) {
      assertEquals("DoubleSad: cos", -Math.sin(VALUE_1) * DERIVATIVES_1[i], cos.derivatives()[i], TOLERANCE_DOUBLE);
    }
  }

  @Test
  public void exp() {
    DoubleSad result = MathSad.exp(OBJECT_1);
    assertEquals("DoubleSad: exp", Math.exp(VALUE_1), result.value(), TOLERANCE_DOUBLE);
    assertEquals("DoubleSad: exp", DERIVATIVES_1.length, result.getNbDerivatives());
    for (int i = 0; i < DERIVATIVES_1.length; i++) {
      assertEquals("DoubleSad: exp", Math.exp(VALUE_1) * DERIVATIVES_1[i], result.derivatives()[i], TOLERANCE_DOUBLE);
    }
  }

  @Test
  public void log() {
    DoubleSad result = MathSad.log(OBJECT_1);
    assertEquals("DoubleSad: log", Math.log(VALUE_1), result.value(), TOLERANCE_DOUBLE);
    assertEquals("DoubleSad: log", DERIVATIVES_1.length, result.getNbDerivatives());
    for (int i = 0; i < DERIVATIVES_1.length; i++) {
      assertEquals("DoubleSad: log", 1.0d  / VALUE_1 * DERIVATIVES_1[i], result.derivatives()[i], TOLERANCE_DOUBLE);
    }
  }

  @Test
  public void sqrt() {
    DoubleSad result = MathSad.sqrt(OBJECT_1);
    assertEquals("DoubleSad: sqrt", Math.sqrt(VALUE_1), result.value(), TOLERANCE_DOUBLE);
    assertEquals("DoubleSad: sqrt", DERIVATIVES_1.length, result.getNbDerivatives());
    for (int i = 0; i < DERIVATIVES_1.length; i++) {
      assertEquals("DoubleSad: sqrt", 0.5d  / Math.sqrt(VALUE_1) * DERIVATIVES_1[i], result.derivatives()[i], TOLERANCE_DOUBLE);
    }
  }

  @Test
  public void mormcdf() {
    DoubleSad result = MathSad.normalCdf(OBJECT_1);
    assertEquals("DoubleSad: mormcdf", NORMAL.cdf(VALUE_1), result.value(), TOLERANCE_DOUBLE);
    assertEquals("DoubleSad: mormcdf", DERIVATIVES_1.length, result.getNbDerivatives());
    for (int i = 0; i < DERIVATIVES_1.length; i++) {
      assertEquals("DoubleSad: mormcdf", NORMAL.pdf(VALUE_1) * DERIVATIVES_1[i], result.derivatives()[i], TOLERANCE_DOUBLE);
    }
  }
  
  @Test
  public void pow() {
    DoubleSad result = MathSad.pow(OBJECT_1, VALUE_2);
    assertEquals("DoubleSad: pow", Math.pow(VALUE_1, VALUE_2), result.value(), TOLERANCE_DOUBLE);
    assertEquals("DoubleSad: pow", DERIVATIVES_1.length, result.getNbDerivatives());
    for (int i = 0; i < DERIVATIVES_1.length; i++) {
      assertEquals("DoubleSad: pow", VALUE_2 * Math.pow(VALUE_1, VALUE_2 - 1.0d) * DERIVATIVES_1[i], 
          result.derivatives()[i], TOLERANCE_DOUBLE);
    }
  }

}
