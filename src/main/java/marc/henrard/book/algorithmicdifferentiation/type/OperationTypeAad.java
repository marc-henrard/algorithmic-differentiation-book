/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.type;

/**
 * Enumeration of the operation types currently available for {@link DoubleAad}.
 */
public enum OperationTypeAad {
  
  /** Input with respect to which the derivatives are computed. */
  INPUT,
  /** Used for manual code providing AD results in a Automatic AD setting. */
  MANUAL,
  /** Addition of two AAD doubles. */
  ADDITION,
  /** Addition of one AAD doubles with a constant double. */
  ADDITION1,
  /** Subtraction of two AAD doubles. */
  SUBTRACTION,
  /** Multiplication of two AAD doubles. */
  MULTIPLICATION,
  /** Multiplication of one AAD double with a constant double. */
  MULTIPLICATION1,
  /** Division of two AAD doubles. */
  DIVISION,
  /** Sine of a AAD double. */
  SIN,
  /** Cosine of a AAD double. */
  COS, 
  /** Exponential of a AAD double. */
  EXP,
  /** Logarithm of a AAD double. */
  LOG,
  /** Square root of a AAD double. */
  SQRT,
  /** Power of a AAD double by another AAD double. */
  POW,
  /** Power of a AAD double with a standard double. */
  POW1,
  /** Normal cumulative density function of a AAD double. */
  NORMALCDF

}
