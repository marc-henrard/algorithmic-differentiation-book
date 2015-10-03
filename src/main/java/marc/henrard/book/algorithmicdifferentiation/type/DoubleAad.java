/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.type;

import marc.henrard.book.algorithmicdifferentiation.tape.TapeAad;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeEntryAad;

/**
 * Class describing a augmented double for Adjoint (or Reverse) Algorithmic Differentiation.
 */
public class DoubleAad {
  
  /** The value of the variable. */
  private final double value;
  /** The index of the variable in the tape. */
  private final int tapeIndex;
  
  /**
   * Constructor.
   * @param value The double value.
   * @param tapeIndex The index of the double in the AAD tape.
   */
  public DoubleAad(double value, int tapeIndex) {
    this.value = value;
    this.tapeIndex = tapeIndex;
  }

  /**
   * Returns the value of the Double.
   * @return The value.
   */
  public double value() {
    return value;
  }

  /** 
   * Returns the tape index.
   * @return The index.
   */
  public int tapeIndex() {
    return tapeIndex;
  }
  
  /**
   * Returns a new DoubleAad with the value and add an entry in the tape with that value and 
   * operation type INPUT.
   * @param value The input value
   * @param tape The tape.
   * @return The input DoubleAad.
   */
  public static DoubleAad input(double value, TapeAad tape){
    return new DoubleAad(value, tape.addEntry(new TapeEntryAad(OperationTypeAad.INPUT, value)));
  }

}
