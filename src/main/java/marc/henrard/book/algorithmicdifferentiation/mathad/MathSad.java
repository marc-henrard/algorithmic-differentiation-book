/**
 * Copyright (C) 2015 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.mathad;

import cern.jet.random.Normal;

import com.opengamma.strata.collect.ArgChecker;

import marc.henrard.book.algorithmicdifferentiation.type.DoubleSad;

/**
 * Class with basic mathematical operations applied to {@link DoubleSad}.
 */
public class MathSad {
  
  /** The normal distribution implementation. */
  private static final Normal NORMAL = new Normal(0.0d, 1.0d, null);
  
  /**
   * Returns the sum of the two augmented doubles. 
   * The values and the derivatives are added and returned in a new object. 
   * The original augmented doubles are not modified.
   * @param d1 The first augmented double.
   * @param d2 The second augmented double.
   * @return The sum.
   */
  public static DoubleSad plus(DoubleSad d1, DoubleSad d2){
    int nbDerivatives = d1.getNbDerivatives();
    ArgChecker.isTrue(d2.getNbDerivatives() == nbDerivatives, "derivative lengths should be equal");
    double valueOutput = d1.value() + d2.value();
    double[] derivativesOutput = new double[nbDerivatives];
    for(int loopd=0; loopd< nbDerivatives; loopd++) {
      derivativesOutput[loopd] = d1.derivatives()[loopd] + d2.derivatives()[loopd];
    }
    return new DoubleSad(valueOutput, derivativesOutput);
  }

  /**
   * Returns the difference of the two augmented doubles. 
   * The values and the derivatives are subtracted and returned in a new object. 
   * The original augmented doubles are not modified.
   * @param d1 The first augmented double.
   * @param d2 The second augmented double.
   * @return The difference.
   */
  public static DoubleSad minus(DoubleSad d1, DoubleSad d2){
    int nbDerivatives = d1.getNbDerivatives();
    ArgChecker.isTrue(d2.getNbDerivatives() == nbDerivatives, "derivative lengths should be equal");
    double valueOutput = d1.value() - d2.value();
    double[] derivativesOutput = new double[nbDerivatives];
    for(int loopd=0; loopd< nbDerivatives; loopd++) {
      derivativesOutput[loopd] = d1.derivatives()[loopd] - d2.derivatives()[loopd];
    }
    return new DoubleSad(valueOutput, derivativesOutput);
  }

  /**
   * Returns the product of the two augmented doubles. 
   * The values are multiplied, the derivatives are combine according to standard formula for the
   * derivative of a product and returned in a new object. 
   * The original augmented doubles are not modified.
   * @param d1 The first augmented double.
   * @param d2 The second augmented double.
   * @return The product.
   */
  public static DoubleSad multipliedBy(DoubleSad d1, DoubleSad d2){
    int nbDerivatives = d1.getNbDerivatives();
    ArgChecker.isTrue(d2.getNbDerivatives() == nbDerivatives, "derivative lengths should be equal");
    double valueOutput = d1.value() * d2.value();
    double[] derivativesOutput = new double[nbDerivatives];
    for(int loopd=0; loopd< nbDerivatives; loopd++) {
      derivativesOutput[loopd] = d1.derivatives()[loopd] * d2.value() + d1.value() * d2.derivatives()[loopd];
    }
    return new DoubleSad(valueOutput, derivativesOutput);
  }

  /**
   * Returns the product of the an augmented double with a primitive double. 
   * The values and the derivatives are multiplied and returned in a new object. 
   * The original augmented double is not modified.
   * @param d1 The augmented double.
   * @param o The primitive double.
   * @return The product.
   */
  public static DoubleSad multipliedBy(DoubleSad d1, double d2) {
    int nbDerivatives = d1.getNbDerivatives();
    double valueOutput = d1.value() * d2;
    double[] derivativesOutput = new double[nbDerivatives];
    for(int loopd=0; loopd< nbDerivatives; loopd++) {
      derivativesOutput[loopd] = d1.derivatives()[loopd] * d2;
    }
    return new DoubleSad(valueOutput, derivativesOutput);
  }

  /**
   * Returns the ratio of the two augmented doubles. 
   * The values are divided, the derivatives are combine according to standard formula for the
   * derivative of a ratio and returned in a new object. 
   * The original augmented doubles are not modified.
   * @param d1 The first augmented double.
   * @param d2 The second augmented double.
   * @return The ratio.
   */
  public static DoubleSad dividedBy(DoubleSad d1, DoubleSad d2){
    int nbDerivatives = d1.getNbDerivatives();
    ArgChecker.isTrue(d2.getNbDerivatives() == nbDerivatives, "derivative lengths should be equal");
    double valueOutput = d1.value() / d2.value();
    double[] derivativesOutput = new double[nbDerivatives];
    double ovalue2 = d2.value() * d2.value();
    for(int loopd=0; loopd< nbDerivatives; loopd++) {
      derivativesOutput[loopd] = (d1.derivatives()[loopd] * d2.value() - d1.value() * d2.derivatives()[loopd]) 
          / ovalue2;
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
  public static DoubleSad sin(DoubleSad d) {
    int nbDerivatives = d.getNbDerivatives();
    double valueOutput = Math.sin(d.value());
    double[] derivativesOutput = new double[nbDerivatives];
    double opDot = Math.cos(d.value());
    for(int loopd=0; loopd< nbDerivatives; loopd++) {
      derivativesOutput[loopd] = opDot * d.derivatives()[loopd];
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
  public static DoubleSad cos(DoubleSad d) {
    int nbDerivatives = d.getNbDerivatives();
    double valueOutput = Math.cos(d.value());
    double[] derivativesOutput = new double[nbDerivatives];
    double opDot = -Math.sin(d.value());
    for(int loopd=0; loopd< nbDerivatives; loopd++) {
      derivativesOutput[loopd] = opDot * d.derivatives()[loopd];
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
  public static DoubleSad exp(DoubleSad d) {
    int nbDerivatives = d.getNbDerivatives();
    double valueOutput = Math.exp(d.value());
    double[] derivativesOutput = new double[nbDerivatives];
    double opDot = Math.exp(d.value());
    for(int loopd=0; loopd< nbDerivatives; loopd++) {
      derivativesOutput[loopd] = opDot * d.derivatives()[loopd];
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
  public static DoubleSad log(DoubleSad d) {
    int nbDerivatives = d.getNbDerivatives();
    double valueOutput = Math.log(d.value());
    double[] derivativesOutput = new double[nbDerivatives];
    double opDot = 1.0d / d.value();
    for(int loopd=0; loopd< nbDerivatives; loopd++) {
      derivativesOutput[loopd] = opDot * d.derivatives()[loopd];
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
  public static DoubleSad sqrt(DoubleSad d) {
    int nbDerivatives = d.getNbDerivatives();
    double valueOutput = Math.sqrt(d.value());
    double[] derivativesOutput = new double[nbDerivatives];
    double opDot = 0.5d / valueOutput;
    for(int loopd=0; loopd< nbDerivatives; loopd++) {
      derivativesOutput[loopd] = opDot * d.derivatives()[loopd];
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
  public static DoubleSad pow(DoubleSad d, double p) {
    int nbDerivatives = d.getNbDerivatives();
    double valueOutput = Math.pow(d.value(), p);
    double[] derivativesOutput = new double[nbDerivatives];
    double opDot = p * Math.pow(d.value(), p - 1.0d);
    for(int loopd=0; loopd< nbDerivatives; loopd++) {
      derivativesOutput[loopd] = opDot * d.derivatives()[loopd];
    }
    return new DoubleSad(valueOutput, derivativesOutput);
  }

  /**
   * Returns the cumulative density function of the current augmented double. 
   * The cumulative density of the value is taken, the derivatives are combine according to standard formula for the
   * derivative of the cumulative density and returned in a new object. 
   * The original augmented doubles are not modified.
   * @return The cumulative density.
   */
  public static DoubleSad normalCdf(DoubleSad d) {
    int nbDerivatives = d.getNbDerivatives();
    double valueOutput = NORMAL.cdf(d.value());
    double[] derivativesOutput = new double[nbDerivatives];
    double opDot = NORMAL.pdf(d.value());
    for(int loopd=0; loopd< nbDerivatives; loopd++) {
      derivativesOutput[loopd] = opDot * d.derivatives()[loopd];
    }
    return new DoubleSad(valueOutput, derivativesOutput);
  }

}
