/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.type;

import org.apache.commons.math3.distribution.NormalDistribution;

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
  
  /** The normal distribution implementation. */
  private static final NormalDistribution NORMAL = new NormalDistribution(0.0d, 1.0d);
  
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
  
  /* Note: The operation below could be part of a MathSad class. 
   * That would match more closely the way standard code proceed, by using the {@link Math} class.
   */
  
  /**
   * Returns the sum of the current augmented double with another one. 
   * The values and the derivatives are added and returned in a new object. 
   * The original augmented doubles are not modified.
   * @param o The other augmented double.
   * @return The sum.
   */
  public DoubleSad plus(DoubleSad o) {
    double valueOutput = value + o.value;
    double[] derivativesOutput = new double[nbDerivatives];
    for(int loopd=0; loopd< nbDerivatives; loopd++) {
      derivativesOutput[loopd] = derivatives[loopd] + o.derivatives[loopd];
    }
    return new DoubleSad(valueOutput, derivativesOutput);
  }

  /**
   * Returns the difference of the current augmented double with another one. 
   * The values and the derivatives are subtracted and returned in a new object. 
   * The original augmented doubles are not modified.
   * @param o The other augmented double.
   * @return The difference.
   */
  public DoubleSad minus(DoubleSad o) {
    double valueOutput = value - o.value;
    double[] derivativesOutput = new double[nbDerivatives];
    for(int loopd=0; loopd< nbDerivatives; loopd++) {
      derivativesOutput[loopd] = derivatives[loopd] - o.derivatives[loopd];
    }
    return new DoubleSad(valueOutput, derivativesOutput);
  }

  /**
   * Returns the product of the current augmented double with another one. 
   * The values are multiplied, the derivatives are combine according to standard formula for the
   * derivative of a product and returned in a new object. 
   * The original augmented doubles are not modified.
   * @param o The other augmented double.
   * @return The product.
   */
  public DoubleSad multipliedBy(DoubleSad o) {
    double valueOutput = value * o.value;
    double[] derivativesOutput = new double[nbDerivatives];
    for(int loopd=0; loopd< nbDerivatives; loopd++) {
      derivativesOutput[loopd] = derivatives[loopd] * o.value + value * o.derivatives[loopd];
    }
    return new DoubleSad(valueOutput, derivativesOutput);
  }

  /**
   * Returns the product of the current augmented double with a standard double. 
   * The values and the derivatives are multiplied and returned in a new object. 
   * The original augmented double is not modified.
   * @param o The double.
   * @return The product.
   */
  public DoubleSad multipliedBy(double o) {
    double valueOutput = value * o;
    double[] derivativesOutput = new double[nbDerivatives];
    for(int loopd=0; loopd< nbDerivatives; loopd++) {
      derivativesOutput[loopd] = derivatives[loopd] * o;
    }
    return new DoubleSad(valueOutput, derivativesOutput);
  }

  /**
   * Returns the ratio of the current augmented double with another one. 
   * The values are divided, the derivatives are combine according to standard formula for the
   * derivative of a ratio and returned in a new object. 
   * The original augmented doubles are not modified.
   * @param o The other augmented double.
   * @return The ratio.
   */
  public DoubleSad dividedBy(DoubleSad o) {
    double valueOutput = value / o.value;
    double[] derivativesOutput = new double[nbDerivatives];
    double ovalue2 = o.value * o.value;
    for(int loopd=0; loopd< nbDerivatives; loopd++) {
      derivativesOutput[loopd] = (derivatives[loopd] * o.value + value * o.derivatives[loopd]) / ovalue2;
    }
    return new DoubleSad(valueOutput, derivativesOutput);
  }

  /**
   * Returns the sine of the current augmented double. 
   * The sine of the value is taken, the derivatives are combine according to standard formula for the
   * derivative of the sine and returned in a new object. 
   * The original augmented doubles are not modified.
   * @param o The other augmented double.
   * @return The sine.
   */
  public DoubleSad sin() {
    double valueOutput = Math.sin(value);
    double[] derivativesOutput = new double[nbDerivatives];
    double opDot = Math.cos(value);
    for(int loopd=0; loopd< nbDerivatives; loopd++) {
      derivativesOutput[loopd] = opDot * derivatives[loopd];
    }
    return new DoubleSad(valueOutput, derivativesOutput);
  }

  /**
   * Returns the cosine of the current augmented double. 
   * The sine of the value is taken, the derivatives are combine according to standard formula for the
   * derivative of the cosine and returned in a new object. 
   * The original augmented doubles are not modified.
   * @param o The other augmented double.
   * @return The cosine.
   */
  public DoubleSad cos() {
    double valueOutput = Math.cos(value);
    double[] derivativesOutput = new double[nbDerivatives];
    double opDot = -Math.sin(value);
    for(int loopd=0; loopd< nbDerivatives; loopd++) {
      derivativesOutput[loopd] = opDot * derivatives[loopd];
    }
    return new DoubleSad(valueOutput, derivativesOutput);
  }

  /**
   * Returns the exponential of the current augmented double. 
   * The sine of the value is taken, the derivatives are combine according to standard formula for the
   * derivative of the exponential and returned in a new object. 
   * The original augmented doubles are not modified.
   * @param o The other augmented double.
   * @return The exponential.
   */
  public DoubleSad exp() {
    double valueOutput = Math.exp(value);
    double[] derivativesOutput = new double[nbDerivatives];
    double opDot = Math.exp(value);
    for(int loopd=0; loopd< nbDerivatives; loopd++) {
      derivativesOutput[loopd] = opDot * derivatives[loopd];
    }
    return new DoubleSad(valueOutput, derivativesOutput);
  }

  /**
   * Returns the natural logarithm of the current augmented double. 
   * The sine of the value is taken, the derivatives are combine according to standard formula for the
   * derivative of the logarithm and returned in a new object. 
   * The original augmented doubles are not modified.
   * @param o The other augmented double.
   * @return The logarithm.
   */
  public DoubleSad log() {
    double valueOutput = Math.log(value);
    double[] derivativesOutput = new double[nbDerivatives];
    double opDot = 1.0d / value;
    for(int loopd=0; loopd< nbDerivatives; loopd++) {
      derivativesOutput[loopd] = opDot * derivatives[loopd];
    }
    return new DoubleSad(valueOutput, derivativesOutput);
  }

  /**
   * Returns the square root of the current augmented double. 
   * The sine of the value is taken, the derivatives are combine according to standard formula for the
   * derivative of the square root and returned in a new object. 
   * The original augmented doubles are not modified.
   * @param o The other augmented double.
   * @return The square root.
   */
  public DoubleSad sqrt() {
    double valueOutput = Math.sqrt(value);
    double[] derivativesOutput = new double[nbDerivatives];
    double opDot = 0.5d / valueOutput;
    for(int loopd=0; loopd< nbDerivatives; loopd++) {
      derivativesOutput[loopd] = opDot * derivatives[loopd];
    }
    return new DoubleSad(valueOutput, derivativesOutput);
  }

  /**
   * Returns the power of the current augmented double by a given double. 
   * The power of the value is taken, the derivatives are combine according to standard formula for the
   * derivative of the power and returned in a new object. 
   * The original augmented doubles are not modified.
   * @param o The other augmented double.
   * @return The power.
   */
  public DoubleSad pow(double p) {
    double valueOutput = Math.pow(value, p);
    double[] derivativesOutput = new double[nbDerivatives];
    double opDot = p * Math.pow(value, p - 1.0d);
    for(int loopd=0; loopd< nbDerivatives; loopd++) {
      derivativesOutput[loopd] = opDot * derivatives[loopd];
    }
    return new DoubleSad(valueOutput, derivativesOutput);
  }

  /**
   * Returns the cumulative density function of the current augmented double. 
   * The cumulative density of the value is taken, the derivatives are combine according to standard formula for the
   * derivative of the cumulative density and returned in a new object. 
   * The original augmented doubles are not modified.
   * @param o The other augmented double.
   * @return The cumulative density.
   */
  public DoubleSad normalCdf() {
    double valueOutput = NORMAL.cumulativeProbability(value);
    double[] derivativesOutput = new double[nbDerivatives];
    double opDot = NORMAL.probability(value);
    for(int loopd=0; loopd< nbDerivatives; loopd++) {
      derivativesOutput[loopd] = opDot * derivatives[loopd];
    }
    return new DoubleSad(valueOutput, derivativesOutput);
  }

}
