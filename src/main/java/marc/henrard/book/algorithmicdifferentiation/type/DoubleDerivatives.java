/**
 * Copyright (C) 2015 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.type;

/**
 * Class describing a double value and its derivatives with respect to several inputs.
 * This class is used for "manual" implementation of AD. 
 * Similar classes for automatic AD are {@link DoubleSad} and {@link DoubleAad}.
 */
public class DoubleDerivatives {
  
  /** The value of the variable. */
  private final double value;
  /** The derivatives of the value with respect to inputs. */
  private final double[] derivatives;
  
  /**
   * Constructor from the value and the derivatives.
   * @param value The variable value.
   * @param derivatives The variable derivatives.
   */
  public DoubleDerivatives(double value, double[] derivatives) {
    this.value = value;
    this.derivatives = derivatives;
  }

  /** 
   * The variable value.
   * @return The value.
   */
  public double value() {
    return value;
  }

  /**
   * The variable derivatives.
   * @return The derivatives.
   */
  public double[] derivatives() {
    return derivatives;
  }

}
