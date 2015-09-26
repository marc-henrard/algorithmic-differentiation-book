/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.type;

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

}
