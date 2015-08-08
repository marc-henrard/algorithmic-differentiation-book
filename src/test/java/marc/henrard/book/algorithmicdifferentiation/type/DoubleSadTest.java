/**
 * Copyright (C) 2015 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.type;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.Test;

/**
 * Tests related to {@link DoubleSad}. 
 */
public class DoubleSadTest {

  /* Test data */
  private static final double VALUE_1 = 123.4d;
  private static final double VALUE_2 = 3.2;
  private static final double[] DERIVATIVES_1 = {1.1, 2.2, 3.3};
  private static final double[] DERIVATIVES_2 = {2.1, 3.2, 4.3};
  private static final DoubleSad OBJECT_1 = new DoubleSad(VALUE_1, DERIVATIVES_1);
  private static final DoubleSad OBJECT_2 = new DoubleSad(VALUE_2, DERIVATIVES_2);
  /* Tolerance for double comparison */
  private static final double TOLERANCE_DOUBLE = 1.0E-12;

  @Test
  public void getter() {
    assertEquals("DoubleSad: getter", VALUE_1, OBJECT_1.value());
    for (int i = 0; i < DERIVATIVES_1.length; i++) {
      assertEquals("DoubleSad: getter", DERIVATIVES_1[i], OBJECT_1.derivatives()[i], TOLERANCE_DOUBLE);
    }
    assertEquals("DoubleSad: getter", 3, OBJECT_1.getNbDerivatives());
  }
  
  @Test
  public void init() {
    DoubleSad[] init = DoubleSad.init(DERIVATIVES_1);
    assertEquals("DoubleSad: init", DERIVATIVES_1.length, init.length);
    for (int i = 0; i < DERIVATIVES_1.length; i++) {
      assertEquals("DoubleSad: init", DERIVATIVES_1.length, init[i].derivatives().length);
      for (int j = 0; j < DERIVATIVES_1.length; j++) {
        if (j != i) {
          assertEquals("DoubleSad: init", 0.0d, init[i].derivatives()[j], TOLERANCE_DOUBLE);
        } else {
          assertEquals("DoubleSad: init", 1.0d, init[i].derivatives()[j], TOLERANCE_DOUBLE);
        }
      }
    }
  }
  
  @Test
  public void plus() {
    DoubleSad plus = OBJECT_1.plus(OBJECT_2);
    assertEquals("DoubleSad: plus", VALUE_1 + VALUE_2, plus.value(), TOLERANCE_DOUBLE);
    assertEquals("DoubleSad: plus", DERIVATIVES_1.length, plus.getNbDerivatives());
    for (int i = 0; i < DERIVATIVES_1.length; i++) {
      assertEquals("DoubleSad: plus", DERIVATIVES_1[i] + DERIVATIVES_2[i], plus.derivatives()[i], TOLERANCE_DOUBLE);
    }
  }
  
  @Test
  public void minus() {
    DoubleSad minus = OBJECT_1.minus(OBJECT_2);
    assertEquals("DoubleSad: minus", VALUE_1 - VALUE_2, minus.value(), TOLERANCE_DOUBLE);
    assertEquals("DoubleSad: minus", DERIVATIVES_1.length, minus.getNbDerivatives());
    for (int i = 0; i < DERIVATIVES_1.length; i++) {
      assertEquals("DoubleSad: minus", DERIVATIVES_1[i] - DERIVATIVES_2[i], minus.derivatives()[i], TOLERANCE_DOUBLE);
    }
  }
  
  @Test
  public void muiltipliedBy1() {
    DoubleSad mult = OBJECT_1.multipliedBy(VALUE_2);
    assertEquals("DoubleSad: muiltiplyBy1", VALUE_1 * VALUE_2, mult.value(), TOLERANCE_DOUBLE);
    assertEquals("DoubleSad: muiltiplyBy1", DERIVATIVES_1.length, mult.getNbDerivatives());
    for (int i = 0; i < DERIVATIVES_1.length; i++) {
      assertEquals("DoubleSad: muiltiplyBy1", DERIVATIVES_1[i] * VALUE_2, mult.derivatives()[i], TOLERANCE_DOUBLE);
    }
  }
  
  @Test
  public void muiltipliedBy2() {
    DoubleSad mult = OBJECT_1.multipliedBy(OBJECT_2);
    assertEquals("DoubleSad: muiltiplyBy1", VALUE_1 * VALUE_2, mult.value(), TOLERANCE_DOUBLE);
    assertEquals("DoubleSad: muiltiplyBy1", DERIVATIVES_1.length, mult.getNbDerivatives());
    for (int i = 0; i < DERIVATIVES_1.length; i++) {
      assertEquals("DoubleSad: muiltiplyBy1", DERIVATIVES_1[i] * VALUE_2 + DERIVATIVES_2[i] * VALUE_1, 
          mult.derivatives()[i], TOLERANCE_DOUBLE);
    }
  }
  
}
