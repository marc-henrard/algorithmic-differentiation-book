/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.tape;

import marc.henrard.book.algorithmicdifferentiation.type.OperationTypeAad;

/**
 * The description of one entry for a Adjoint (or reverse) Algorithmic Differentiation tape.
 */
public class TapeEntryAad {
  
  /** The operation type */
  private final OperationTypeAad operationType;
  /** The tape index of the first argument. */
  private final int indexArg1;
  /** The tape index of the second argument. Optional. If there is no second argument, the index is set to -1.*/
  private final int indexArg2;
  /** The value of the double. */
  private final double value;
  /** Extra value required for further processing. It can be a multiplication factor, an additive term 
   *  or an externally provided derivative. */
  private final double extraValue;
  /** The derivative of the final value with respect to the variable under consideration. 
   * Is populated only after the tape has been interpreted. */
  private double valueBar;
  
  /**
   * Constructor of a tape entry with two arguments and an extra value.
   * @param operationType  The AD type of operation
   * @param indexArg1 The tape index of the operation first argument.
   * @param indexArg2 The tape index of the operation second argument.
   * @param value The value of the result.
   * @param extraValue Extra value required in some cases for further processing.
   */
  public TapeEntryAad(
      OperationTypeAad operationType, 
      int indexArg1, 
      int indexArg2, 
      double value, 
      double extraValue) {
    this.operationType = operationType;
    this.indexArg1 = indexArg1;
    this.indexArg2 = indexArg2;
    this.value = value;
    this.extraValue = extraValue;
    valueBar = 0.0d;
  }
  
  /**
   * Constructor of a tape entry with two arguments.
   * @param operationType  The AD type of operation
   * @param indexArg1 The tape index of the operation first argument.
   * @param indexArg2 The tape index of the operation second argument.
   * @param value The value of the result.
   */
  public TapeEntryAad(
      OperationTypeAad operationType, 
      int indexArg1, 
      int indexArg2, 
      double value) {
    this.operationType = operationType;
    this.indexArg1 = indexArg1;
    this.indexArg2 = indexArg2;
    this.value = value;
    this.extraValue = 0.0d;
    valueBar = 0.0d;
  }

  /**
   * Constructor of a tape entry with one argument.
   * @param operationType  The AD type of operation
   * @param indexArg The tape index of the operation argument.
   * @param value The value of the result.
   */
  public TapeEntryAad(
      OperationTypeAad operationType, 
      int indexArg, 
      double value) {
    this.operationType = operationType;
    this.indexArg1 = indexArg;
    this.indexArg2 = -1;
    this.value = value;
    this.extraValue = 0.0d;
    valueBar = 0.0d;
  }

  /**
   * Constructor of a tape entry with no argument.
   * @param operationType  The AD type of operation
   * @param value The value of the result.
   */
  public TapeEntryAad(OperationTypeAad operationType, double value) {
    this.operationType = operationType;
    this.indexArg1 = -1;
    this.indexArg2 = -1;
    this.value = value;
    this.extraValue = 0.0d;
    valueBar = 0.0d;
  }
  
  /**
   * Constructor of a tape entry with one argument and an extra value.
   * @param operationType  The AD type of operation
   * @param indexArg The tape index of the operation argument.
   * @param value The value of the result.
   * @param extraValue Extra value required in some cases for further processing.
   */
  public TapeEntryAad(
      OperationTypeAad operationType, 
      int indexArg, 
      double value, 
      double extraValue) {
    this.operationType = operationType;
    this.indexArg1 = indexArg;
    this.indexArg2 = -1;
    this.value = value;
    this.extraValue = extraValue;
    valueBar = 0.0d;
  }

  /**
   * Returns the operation type.
   * @return The operation type.
   */
  public OperationTypeAad getOperationType() {
    return operationType;
  }

  /**
   * Returns the tape index of the first argument.
   * @return The index.
   */
  public int getIndexArg1() {
    return indexArg1;
  }

  /**
   * Returns the tape index of the second argument.
   * @return The index.
   */
  public int getIndexArg2() {
    return indexArg2;
  }

  /**
   * Returns the entry value.
   * @return The value.
   */
  public double getValue() {
    return value;
  }

  /**
   * Returns the derivative of the output with respect to the value, also called value bar.
   * The variable is populated only when the tape has been interpreted, see {@link TapeUtils#interpret}.
   * @return The derivative.
   */
  public double getValueBar() {
    return valueBar;
  }

  /**
   * Adds a given value to the valueBar argument. The entry is modified by this method.
   * @param valueBar Return the total.
   */
  public void addValueBar(double valueBar) {
    this.valueBar += valueBar;
  }

  /**
   * Returns the extra value required for further processing. 
   * It can be a multiplication factor, an additive term or an externally provided derivative.
   * @return The extra value.
   */
  public double getExtraValue() {
    return extraValue;
  }
  
  @Override
  public String toString() {
    String s = "TapeEntry: " + operationType + ", " + indexArg1 + ", " + indexArg2 + ": " + 
      value + ", " + extraValue + ", " + valueBar;
    return s;
  }
  
  
}
