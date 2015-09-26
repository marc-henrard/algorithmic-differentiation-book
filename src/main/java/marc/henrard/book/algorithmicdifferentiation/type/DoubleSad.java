/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.type;

/**
 * Class describing a augmented double for Standard (or Tangent or Forward) Algorithmic Differentiation.
 */
public class DoubleSad {
  
  /** The value of the variable. */
  private final double value;
  /** The number of derivatives. */
  private final int nbDerivatives;
  /** The derivative of the variable with respect to all inputs. */
  private final double[] derivatives;
  
  /**
   * Constructor.
   * @param value The variable value.
   * @param derivatives The derivative of the variable with respect to all inputs.
   */
  public DoubleSad(double value, double[] derivatives) {
    this.value = value;
    this.derivatives = derivatives;
    nbDerivatives = derivatives.length;
  }
  
  /**
   * Create an array of augmented double suitable as a first step for the Standard AD.
   * The array is compose of one augmented double for each input. The i-th augmented double has the i-th input as
   * value and the derivatives are 0 except for the i-th one which is 1.
   * @param inputs The array of input values.
   * @return The array of augmented doubles.
   */
  static public DoubleSad[] init(double[] inputs) {
    int nbInputs = inputs.length;
    DoubleSad[] init = new DoubleSad[nbInputs];
    for(int loopi = 0; loopi < nbInputs; loopi++) {
      double[] initDot = new double[nbInputs];
      initDot[loopi] = 1.0d;
      init[loopi] =  new DoubleSad(inputs[loopi], initDot);
    }
    return init;
  }

  /**
   * Returns the value.
   * @return The value.
   */
  public double value() {
    return value;
  }

  /**
   * Returns the number of derivatives.
   * @return The number.
   */
  public int getNbDerivatives() {
    return nbDerivatives;
  }

  /**
   * Returns the array with the derivatives with respect to each input.
   * @return The derivatives.
   */
  public double[] derivatives() {
    return derivatives;
  }

}
