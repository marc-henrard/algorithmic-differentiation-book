/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.type;

import cern.jet.random.Normal;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeAad;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeEntryAad;
import marc.henrard.book.algorithmicdifferentiation.type.OperationTypeAad;

/**
 * Class describing a augmented double for Adjoint (or Reverse) Algorithmic Differentiation.
 */
public class DoubleAad {
  
  /** The value of the variable. */
  private final double value;
  /** The index of the variable in the tape. */
  private final int tapeIndex;
  
  /** The normal distribution implementation. */
  private static final Normal NORMAL = new Normal(0.0d, 1.0d, null);
  
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
  
  /* Note: The operation below could be part of a MathAad class. 
   * That would match more closely the way standard code proceed, by using the java.lang.Math class.
   */
  
  /**
   * Adds a variable {@link DoubleAad} to the current one and puts the total in a new variable. 
   * The Current {@link DoubleAad} is not modified.
   * @param o The other Double.
   * @param tape The tape in which the operation is recorded.
   * @return The sum.
   */
  public DoubleAad plus(DoubleAad o, TapeAad tape) {
    double valueOutput = value + o.value;
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.ADDITION, tapeIndex, o.tapeIndex, valueOutput));
    return new DoubleAad(valueOutput, index);
  }

  /**
   * Adds a constant {@link double} to the current {@link DoubleAad} and puts the total in a new variable. 
   * The Current {@link DoubleAad} is not modified.
   * @param o The other Double.
   * @param tape The tape in which the operation is recorded.
   * @return The sum.
   */
  public DoubleAad plus(double o, TapeAad tape) {
    double valueOutput = value + o;
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.ADDITION1, tapeIndex, valueOutput, o));
    return new DoubleAad(valueOutput, index);
  }
  
  /**
   * Subtracts a variable {@link DoubleAad} from the current one and puts the difference in a new variable. 
   * The Current {@link DoubleAad} is not modified.
   * @param o The other Double.
   * @param tape The tape in which the operation is recorded.
   * @return The difference.
   */
  public DoubleAad minus(DoubleAad o, TapeAad tape) {
    double valueOutput = value - o.value;
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.SUBTRACTION, tapeIndex, o.tapeIndex, valueOutput));
    return new DoubleAad(valueOutput, index);
  }
  
  /**
   * Multiplies the current {@link DoubleAad} by an other one and puts the product in a new variable. 
   * The Current {@link DoubleAad} is not modified.
   * @param o The other Double.
   * @param tape The tape in which the operation is recorded.
   * @return The product.
   */
  public DoubleAad multipliedBy(DoubleAad o, TapeAad tape) {
    double valueOutput = value * o.value;
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.MULTIPLICATION, tapeIndex, o.tapeIndex, valueOutput));
    return new DoubleAad(valueOutput, index);
  }

  /**
   * Multiplies the current {@link DoubleAad} by a constant {@link double} and puts the product in a new variable. 
   * The Current {@link DoubleAad} is not modified.
   * @param o The other Double.
   * @param tape The tape in which the operation is recorded.
   * @return The product.
   */
  public DoubleAad multipliedBy(double o, TapeAad tape) {
    double valueOutput = value * o;
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.MULTIPLICATION1, tapeIndex, valueOutput, o));
    return new DoubleAad(valueOutput, index);
  }
  
  /**
   * Divides the current {@link DoubleAad} by an other one and puts the ratio in a new variable. 
   * The Current {@link DoubleAad} is not modified.
   * @param o The other Double.
   * @param tape The tape in which the operation is recorded.
   * @return The ratio.
   */
  public DoubleAad dividedBy(DoubleAad o, TapeAad tape) {
    double valueOutput = value / o.value;
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.DIVISION, tapeIndex, o.tapeIndex, valueOutput));
    return new DoubleAad(valueOutput, index);
  }
  
  /**
   * Takes the power of the current {@link DoubleAad} by an other one and puts the result in a new variable. 
   * The Current {@link DoubleAad} is not modified.
   * @param o The other Double.
   * @param tape The tape in which the operation is recorded.
   * @return The power.
   */
  public DoubleAad pow(DoubleAad p, TapeAad tape) {
    double valueOutput = Math.pow(value, p.value);
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.POW, tapeIndex, p.tapeIndex, valueOutput));
    return new DoubleAad(valueOutput, index);
  }
  
  /**
   * Takes the power of the current {@link DoubleAad} by by a constant {@link double} and puts the result in a new variable. 
   * The Current {@link DoubleAad} is not modified.
   * @param o The other Double.
   * @param tape The tape in which the operation is recorded.
   * @return The power.
   */
  public DoubleAad pow(double p, TapeAad tape) {
    double valueOutput = Math.pow(value, p);
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.POW1, tapeIndex, valueOutput, p));
    return new DoubleAad(valueOutput, index);
  }
  
  /**
   * Takes the sine of the current {@link DoubleAad} and puts the result in a new variable. 
   * The Current {@link DoubleAad} is not modified.
   * @param o The other Double.
   * @param tape The tape in which the operation is recorded.
   * @return The sine.
   */
  public DoubleAad sin(TapeAad tape) {
    double valueOutput = Math.sin(value);
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.SIN, tapeIndex, valueOutput));
    return new DoubleAad(valueOutput, index);
  }
  
  /**
   * Computes the cosine of the current {@link DoubleAad} and puts the result in a new variable. 
   * The Current {@link DoubleAad} is not modified.
   * @param o The other Double.
   * @param tape The tape in which the operation is recorded.
   * @return The cosine.
   */
  public DoubleAad cos(TapeAad tape) {
    double valueOutput = Math.cos(value);
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.COS, tapeIndex, valueOutput));
    return new DoubleAad(valueOutput, index);
  }
  
  /**
   * Computes the square root of the current value and record the operation in the tape.
   * The output is a new {@link DoubleAad}; the current object is not modified.
   * @param tape The tape where the operation should be recorded.
   * @return The square root result, including the index in the tape.
   */
  public DoubleAad sqrt(TapeAad tape) {
    double valueOutput = Math.sqrt(value);
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.SQRT, tapeIndex, valueOutput));
    return new DoubleAad(valueOutput, index);
  }
  
  /**
   * Computes the exponential of the current value and record the operation in the tape.
   * The output is a new {@link DoubleAad}; the current object is not modified.
   * @param tape The tape where the operation should be recorded.
   * @return The exponential result, including the index in the tape.
   */
  public DoubleAad exp(TapeAad tape) {
    double valueOutput = Math.exp(value);
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.EXP, tapeIndex, valueOutput));
    return new DoubleAad(valueOutput, index);
  }
  
  /**
   * Computes the natural logarithm of the current value and record the operation in the tape.
   * The output is a new {@link DoubleAad}; the current object is not modified.
   * @param tape The tape where the operation should be recorded.
   * @return The logarithm result, including the index in the tape.
   */
  public DoubleAad log(TapeAad tape) {
    double valueOutput = Math.log(value);
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.LOG, tapeIndex, valueOutput));
    return new DoubleAad(valueOutput, index);
  }
  
  /**
   * Computes the normal cumulative density function of the current value and records the operation in the tape.
   * The output is a new {@link DoubleAad}; the current object is not modified.
   * @param tape The tape where the operation should be recorded.
   * @return The cumulative density function result, including the index in the tape.
   */
  public DoubleAad normalCdf(TapeAad tape) {
    double valueOutput = NORMAL.cdf(value);
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.NORMALCDF, tapeIndex, valueOutput));
    return new DoubleAad(valueOutput, index);
  }

}
