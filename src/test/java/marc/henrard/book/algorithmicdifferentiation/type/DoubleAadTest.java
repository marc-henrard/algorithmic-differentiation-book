/**
 * Copyright (C) 2015 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.type;

import static org.testng.AssertJUnit.assertEquals;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeUtilsTest;

import org.testng.annotations.Test;

/**
 * Tests related to {@link DoubleAad}. 
 * Other tests related to the same class can be found in {@link TapeUtilsTest}.
 */
public class DoubleAadTest {
  
  @Test
  public void getter() {
    double value = 123.4d;
    int index = 21;
    DoubleAad object = new DoubleAad(value, index);
    assertEquals("DoubleAad: getter", value, object.value());
    assertEquals("DoubleAad: getter", index, object.tapeIndex());
  }
  
}
