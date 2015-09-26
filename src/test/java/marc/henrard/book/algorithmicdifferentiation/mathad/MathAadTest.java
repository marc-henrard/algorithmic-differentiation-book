/**
 * Copyright (C) 2015 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.mathad;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import marc.henrard.book.algorithmicdifferentiation.tape.TapeAad;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeEntryAad;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleAad;
import marc.henrard.book.algorithmicdifferentiation.type.OperationTypeAad;

import org.testng.annotations.Test;

/**
 * Tests related to {@link MathAad}. 
 */
public class MathAadTest {
  
  public static final double VALUE1 = 123.4;
  public static final double VALUE2 = 98.7;
  /* Tolerance for double comparison */
  private static final double TOLERANCE_DOUBLE = 1.0E-12;
  
  @Test
  public void plus() {
    TapeAad tape = new TapeAad();
    int i1 = tape.addEntry(new TapeEntryAad(OperationTypeAad.INPUT, VALUE1));
    DoubleAad d1 = new DoubleAad(VALUE1, i1);
    int i2 = tape.addEntry(new TapeEntryAad(OperationTypeAad.INPUT, VALUE2));
    DoubleAad d2 = new DoubleAad(VALUE2, i2);
    DoubleAad r = MathAad.plus(d1, d2, tape);
    assertEquals(VALUE1 + VALUE2, r.value(), TOLERANCE_DOUBLE);
    assertTrue(tape.size() == 3);
    assertTrue(tape.getEntry(2).getOperationType() == OperationTypeAad.ADDITION);
    assertTrue(tape.getEntry(2).getIndexArg1() == 0);
    assertTrue(tape.getEntry(2).getIndexArg2() == 1);
    assertEquals(tape.getEntry(2).getValue(), VALUE1 + VALUE2, TOLERANCE_DOUBLE);
  }
  
  @Test
  public void minus() {
    TapeAad tape = new TapeAad();
    int i1 = tape.addEntry(new TapeEntryAad(OperationTypeAad.INPUT, VALUE1));
    DoubleAad d1 = new DoubleAad(VALUE1, i1);
    int i2 = tape.addEntry(new TapeEntryAad(OperationTypeAad.INPUT, VALUE2));
    DoubleAad d2 = new DoubleAad(VALUE2, i2);
    DoubleAad r = MathAad.minus(d1, d2, tape);
    assertEquals(VALUE1 - VALUE2, r.value(), TOLERANCE_DOUBLE);
    assertTrue(tape.size() == 3);
    assertTrue(tape.getEntry(2).getOperationType() == OperationTypeAad.SUBTRACTION);
    assertTrue(tape.getEntry(2).getIndexArg1() == 0);
    assertTrue(tape.getEntry(2).getIndexArg2() == 1);
    assertEquals(tape.getEntry(2).getValue(), VALUE1 - VALUE2, TOLERANCE_DOUBLE);
  }
  
}
