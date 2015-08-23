/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.tape;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeAad;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeEntryAad;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeUtils;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleAad;
import marc.henrard.book.algorithmicdifferentiation.type.OperationTypeAad;

import org.testng.annotations.Test;

/**
 * Tests related to {@link TapeUtils} and {@link DoubleAad}.
 */
public class TapeUtilsTest {

  /* The tolerance for the comparison of operations on doubles. */
  static private final double TOLERANCE_DOUBLE = 1.0E-10;
  
  @Test
  public void interpretInput() {
    TapeAad tape = new TapeAad();
    double value = 123.4d;
    TapeEntryAad inEntry = new TapeEntryAad(OperationTypeAad.INPUT, value);
    tape.addEntry(inEntry);
    TapeUtils.interpret(tape);
    assertTrue("TapeUtils: interpret - INPUT", tape.size()==1);
    assertTrue("TapeUtils: interpret - INPUT", tape.getEntry(0).getOperationType() == OperationTypeAad.INPUT);
    assertEquals("TapeUtils: interpret - INPUT", tape.getEntry(0).getValue(), value, TOLERANCE_DOUBLE);
    assertEquals("TapeUtils: interpret - INPUT", tape.getEntry(0).getValueBar(), 1.0d, TOLERANCE_DOUBLE);
    assertEquals("TapeUtils: interpret - INPUT", tape.getEntry(0).getIndexArg1(), -1);
    assertEquals("TapeUtils: interpret - INPUT", tape.getEntry(0).getIndexArg2(), -1);
  }
  
  @Test
  public void interpretAddition1() {
    TapeAad tape = new TapeAad();
    double value = 123.4d;
    TapeEntryAad inEntry = new TapeEntryAad(OperationTypeAad.INPUT, value);
    int indexIn = tape.addEntry(inEntry);
    DoubleAad in = new DoubleAad(value, indexIn);
    DoubleAad add1 = in.plus(1.0d, tape);
    TapeUtils.interpret(tape);
    assertTrue("TapeUtils: interpret - ADDITION1", tape.size()==2);
    assertTrue("TapeUtils: interpret - ADDITION1", tape.getEntry(0).getOperationType() == OperationTypeAad.INPUT);
    assertTrue("TapeUtils: interpret - ADDITION1", tape.getEntry(1).getOperationType() == OperationTypeAad.ADDITION1);
    assertEquals("TapeUtils: interpret - ADDITION1", tape.getEntry(0).getValue(), value, TOLERANCE_DOUBLE);
    assertEquals("TapeUtils: interpret - ADDITION1", tape.getEntry(1).getValue(), value+1.0d, TOLERANCE_DOUBLE);
    assertEquals("TapeUtils: interpret - ADDITION1", tape.getEntry(1).getValue(), add1.value(), TOLERANCE_DOUBLE);
    assertEquals("TapeUtils: interpret - ADDITION1", tape.getEntry(0).getValueBar(), 1.0d, TOLERANCE_DOUBLE);
    assertEquals("TapeUtils: interpret - ADDITION1", tape.getEntry(1).getValueBar(), 1.0d, TOLERANCE_DOUBLE);
    assertEquals("TapeUtils: interpret - ADDITION1", tape.getEntry(1).getIndexArg1(), indexIn);
    assertEquals("TapeUtils: interpret - ADDITION1", tape.getEntry(1).getIndexArg2(), -1);
  }
  
  @Test
  public void interpretAddition() {
    TapeAad tape = new TapeAad();
    double value1 = 123.4d;
    double value2 = 567.8d;
    TapeEntryAad in1Entry = new TapeEntryAad(OperationTypeAad.INPUT, value1);
    int index1In = tape.addEntry(in1Entry);
    TapeEntryAad in2Entry = new TapeEntryAad(OperationTypeAad.INPUT, value2);
    int index2In = tape.addEntry(in2Entry);
    DoubleAad in1 = new DoubleAad(value1, index1In);
    DoubleAad in2 = new DoubleAad(value2, index2In);
    DoubleAad add = in1.plus(in2, tape);
    TapeUtils.interpret(tape);
    assertTrue("TapeUtils: interpret - ADDITION", tape.size()==3);
    assertTrue("TapeUtils: interpret - ADDITION", tape.getEntry(0).getOperationType() == OperationTypeAad.INPUT);
    assertTrue("TapeUtils: interpret - ADDITION", tape.getEntry(1).getOperationType() == OperationTypeAad.INPUT);
    assertTrue("TapeUtils: interpret - ADDITION", tape.getEntry(2).getOperationType() == OperationTypeAad.ADDITION);
    assertEquals("TapeUtils: interpret - ADDITION", tape.getEntry(0).getValue(), value1, TOLERANCE_DOUBLE);
    assertEquals("TapeUtils: interpret - ADDITION", tape.getEntry(1).getValue(), value2, TOLERANCE_DOUBLE);
    assertEquals("TapeUtils: interpret - ADDITION", tape.getEntry(2).getValue(), value1+value2, TOLERANCE_DOUBLE);
    assertEquals("TapeUtils: interpret - ADDITION", tape.getEntry(2).getValue(), add.value(), TOLERANCE_DOUBLE);
    assertEquals("TapeUtils: interpret - ADDITION", tape.getEntry(0).getValueBar(), 1.0d, TOLERANCE_DOUBLE);
    assertEquals("TapeUtils: interpret - ADDITION", tape.getEntry(1).getValueBar(), 1.0d, TOLERANCE_DOUBLE);
    assertEquals("TapeUtils: interpret - ADDITION", tape.getEntry(2).getValueBar(), 1.0d, TOLERANCE_DOUBLE);
    assertEquals("TapeUtils: interpret - ADDITION", tape.getEntry(2).getIndexArg1(), index1In);
    assertEquals("TapeUtils: interpret - ADDITION", tape.getEntry(2).getIndexArg2(), index2In);
  }
  
  //TODO: add tests for other operations
  
}
