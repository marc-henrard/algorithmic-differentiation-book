/**
 * Copyright (C) 2015 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.mathad;

import marc.henrard.book.algorithmicdifferentiation.tape.TapeAad;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeEntryAad;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleAad;
import marc.henrard.book.algorithmicdifferentiation.type.OperationTypeAad;
import cern.jet.random.Normal;

/**
 * Class with basic mathematical operations applied to {@link DoubleAad}.
 */
public class MathAad {
  
  /** The normal distribution implementation. */
  private static final Normal NORMAL = new Normal(0.0d, 1.0d, null);
  
  /**
   * Sums two augmented doubles and puts the total in a new variable. 
   * The original {@link DoubleAad} are not modified.
   * @param d1 The first augmented double.
   * @param d2 The second augmented double.
   * @param tape The tape in which the operation is recorded.
   * @return The sum.
   */
  public static DoubleAad plus(DoubleAad d1, DoubleAad d2, TapeAad tape) {
    double valueOutput = d1.value() + d2.value();
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.ADDITION, d1.tapeIndex(), d2.tapeIndex(), valueOutput));
    return new DoubleAad(valueOutput, index);
  }

  /**
   * Sums an augmented double to a primitive one and puts the total in a new variable. 
   * The original augmented double is not modified.
   * @param d The augmented Double.
   * @param tape The tape in which the operation is recorded.
   * @return The sum.
   */
  public static DoubleAad plus(DoubleAad d, double o, TapeAad tape) {
    double valueOutput = d.value() + o;
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.ADDITION1, d.tapeIndex(), valueOutput, o));
    return new DoubleAad(valueOutput, index);
  }
  
  /**
   * Subtracts two augmented doubles and puts the difference in a new variable. 
   * The original {@link DoubleAad} are not modified.
   * @param d1 The first augmented double.
   * @param d2 The second augmented double.
   * @param tape The tape in which the operation is recorded.
   * @return The difference.
   */
  public static DoubleAad minus(DoubleAad d1, DoubleAad d2, TapeAad tape) {
    double valueOutput = d1.value() - d2.value();
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.SUBTRACTION, d1.tapeIndex(), d2.tapeIndex(), valueOutput));
    return new DoubleAad(valueOutput, index);
  }
  
  /**
   * Multiplies two augmented doubles and puts the product in a new variable. 
   * The original {@link DoubleAad} are not modified.
   * @param d1 The first augmented double.
   * @param d2 The second augmented double.
   * @param tape The tape in which the operation is recorded.
   * @return The product.
   */
  public static DoubleAad multipliedBy(DoubleAad d1, DoubleAad d2, TapeAad tape) {
    double valueOutput = d1.value() * d2.value();
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.MULTIPLICATION, d1.tapeIndex(), d2.tapeIndex(), valueOutput));
    return new DoubleAad(valueOutput, index);
  }

  /**
   * Multiplies an augmented double with a primitive double and puts the product in a new variable. 
   * The original {@link DoubleAad} is not modified.
   * @param d2 The other Double.
   * @param tape The tape in which the operation is recorded.
   * @return The product.
   */
  public static DoubleAad multipliedBy(DoubleAad d1, double d2, TapeAad tape) {
    double valueOutput = d1.value() * d2;
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.MULTIPLICATION1, d1.tapeIndex(), valueOutput, d2));
    return new DoubleAad(valueOutput, index);
  }
  
  /**
   * Divides two augmented doubles and puts the ratio in a new variable. 
   * The original {@link DoubleAad} are not modified.
   * @param d1 The first augmented double.
   * @param d2 The second augmented double.
   * @param tape The tape in which the operation is recorded.
   * @return The ratio.
   */
  public static DoubleAad dividedBy(DoubleAad d1, DoubleAad d2, TapeAad tape) {
    double valueOutput = d1.value() / d2.value();
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.DIVISION, d1.tapeIndex(), d2.tapeIndex(), valueOutput));
    return new DoubleAad(valueOutput, index);
  }
  
  /**
   * Takes the power of an augmented double by an other one and puts the result in a new variable. 
   * The original {@link DoubleAad} are not modified.
   * @param d1 The first augmented double.
   * @param d2 The second augmented double.
   * @param tape The tape in which the operation is recorded.
   * @return The power.
   */
  public static DoubleAad pow(DoubleAad d1, DoubleAad d2, TapeAad tape) {
    double valueOutput = Math.pow(d1.value(), d2.value());
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.POW, d1.tapeIndex(), d2.tapeIndex(), valueOutput));
    return new DoubleAad(valueOutput, index);
  }
  
  /**
   * Takes the power of an augmented double by a primitive double and puts the result in a new variable. 
   * The original {@link DoubleAad} is not modified.
   * @param d1 The augmented double.
   * @param d2 The primitive double.
   * @param tape The tape in which the operation is recorded.
   * @return The power.
   */
  public static DoubleAad pow(DoubleAad d1, double d2, TapeAad tape) {
    double valueOutput = Math.pow(d1.value(), d2);
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.POW1, d1.tapeIndex(), valueOutput, d2));
    return new DoubleAad(valueOutput, index);
  }
  
  /**
   * Takes the sine of an augmented double and puts the result in a new variable. 
   * The original {@link DoubleAad} is not modified.
   * @param d The other Double.
   * @param tape The tape in which the operation is recorded.
   * @return The sine.
   */
  public static DoubleAad sin(DoubleAad d, TapeAad tape) {
    double valueOutput = Math.sin(d.value());
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.SIN, d.tapeIndex(), valueOutput));
    return new DoubleAad(valueOutput, index);
  }
  
  /**
   * Takes the cosine of an augmented double and puts the result in a new variable. 
   * The original {@link DoubleAad} is not modified.
   * @param d The other Double.
   * @param tape The tape in which the operation is recorded.
   * @return The cosine.
   */
  public static DoubleAad cos(DoubleAad d1, TapeAad tape) {
    double valueOutput = Math.cos(d1.value());
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.COS, d1.tapeIndex(), valueOutput));
    return new DoubleAad(valueOutput, index);
  }
  
  /**
   * Takes the square root of an augmented double and puts the result in a new variable. 
   * The original {@link DoubleAad} is not modified.
   * @param d The other Double.
   * @param tape The tape where the operation should be recorded.
   * @return The square root result, including the index in the tape.
   */
  public static DoubleAad sqrt(DoubleAad d1, TapeAad tape) {
    double valueOutput = Math.sqrt(d1.value());
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.SQRT, d1.tapeIndex(), valueOutput));
    return new DoubleAad(valueOutput, index);
  }
  
  /**
   * Takes the exponential of an augmented double and puts the result in a new variable. 
   * The original {@link DoubleAad} is not modified.
   * @param d The other Double.
   * @param tape The tape where the operation should be recorded.
   * @return The exponential result, including the index in the tape.
   */
  public static DoubleAad exp(DoubleAad d1, TapeAad tape) {
    double valueOutput = Math.exp(d1.value());
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.EXP, d1.tapeIndex(), valueOutput));
    return new DoubleAad(valueOutput, index);
  }
  
  /**
   * Takes the logarithm of an augmented double and puts the result in a new variable. 
   * The original {@link DoubleAad} is not modified.
   * @param d The other Double.
   * @param tape The tape where the operation should be recorded.
   * @return The logarithm result, including the index in the tape.
   */
  public static DoubleAad log(DoubleAad d1, TapeAad tape) {
    double valueOutput = Math.log(d1.value());
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.LOG, d1.tapeIndex(), valueOutput));
    return new DoubleAad(valueOutput, index);
  }
  
  /**
   * Takes the normal cumulative distribution function of an augmented double and puts the result in a new variable. 
   * The original {@link DoubleAad} is not modified.
   * @param d The other Double.
   * @param tape The tape where the operation should be recorded.
   * @return The cumulative density function result, including the index in the tape.
   */
  public static DoubleAad normalCdf(DoubleAad d1, TapeAad tape) {
    double valueOutput = NORMAL.cdf(d1.value());
    int index = tape.addEntry(new TapeEntryAad(OperationTypeAad.NORMALCDF, d1.tapeIndex(), valueOutput));
    return new DoubleAad(valueOutput, index);
  }

}
