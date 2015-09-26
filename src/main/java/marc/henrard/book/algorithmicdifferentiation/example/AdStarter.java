/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.example;

import marc.henrard.book.algorithmicdifferentiation.mathad.MathAad;
import marc.henrard.book.algorithmicdifferentiation.mathad.MathSad;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeAad;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleAad;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleDerivatives;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleSad;

/**
 * Simple function with Algorithmic Differentiation example.
 * Starter example in book "Algorithmic Differentiation in Finance Explained".
 */
public class AdStarter {
  
  /**
   * Returns cos(a[0]+ exp(a[1])) (sin(a[2]) + cos(a[3])) + (a[1])^(3/2) + a[3].
   * @param a The parameters
   * @return The result.
   */
  static public double f(double[] a) {
    double b1 = a[0] + Math.exp(a[1]);
    double b2 = Math.sin(a[2]) + Math.cos(a[3]);
    double b3 = Math.pow(a[1], 1.5d) + a[3];
    double b4 = Math.cos(b1) * b2 + b3;
    return b4;
  }
  
  /**
   * Returns the value of the function f and its derivatives with respect to all the inputs.
   * The function is f(a) = cos(a[0]+ exp(a[1])) (sin(a[2]) + cos(a[3])) + (a[1])^(3/2) + a[3].
   * The derivatives are computed by Standard Algorithmic Differentiation.
   * @param a The parameters
   * @return The value of f and its derivatives.
   */
  static public DoubleDerivatives f_Sad(double[] a) {
    // Forward sweep - function
    double b1 = a[0] + Math.exp(a[1]);
    double b2 = Math.sin(a[2]) + Math.cos(a[3]);
    double b3 = Math.pow(a[1], 1.5d) + a[3];
    double b4 = Math.cos(b1) * b2 + b3;
    // Forward sweep - derivatives
    int nbA = a.length;
    double[] b1Dot = new double[nbA];
    b1Dot[0] = 1.0;
    b1Dot[1] = Math.exp(a[1]);
    double[] b2Dot = new double[nbA];
    b2Dot[2] = Math.cos(a[2]);
    b2Dot[3] = - Math.sin(a[3]);
    double[] b3Dot = new double[nbA];
    b3Dot[1] = 1.5d * Math.sqrt(a[1]);
    b3Dot[3] = 1.0d;
    double[] b4Dot = new double[nbA];
    for(int loopa = 0; loopa < nbA ; loopa++) {
      b4Dot[loopa] = b2 * - Math.sin(b1) * b1Dot[loopa] + 
          Math.cos(b1) * b2Dot[loopa] + 1.0d * b3Dot[loopa];
    }
    return new DoubleDerivatives(b4, b4Dot);
  }
  
  /**
   * Returns the value of the function f and its derivatives with respect to all the inputs.
   * The function is f(a) = cos(a[0]+ exp(a[1])) (sin(a[2]) + cos(a[3])) + (a[1])^(3/2) + a[3].
   * The derivatives are computed by Standard Algorithmic Differentiation.
   * @param a The parameters
   * @return The value of f and its derivatives.
   */
  static public DoubleDerivatives f_Sad_Optimized(double[] a) {
    // Forward sweep - function
    double expa1 = Math.exp(a[1]);
    double b1 = a[0] + expa1;
    double b2 = Math.sin(a[2]) + Math.cos(a[3]);
    double b3 = Math.pow(a[1], 1.5d) + a[3];
    double cosb1 = Math.cos(b1);
    double b4 = cosb1 * b2 + b3;
    // Forward sweep - derivatives
    int nbA = a.length;
    double[] b1Dot = new double[nbA];
    b1Dot[0] = 1.0;
    b1Dot[1] = expa1;
    double[] b2Dot = new double[nbA];
    b2Dot[2] = Math.cos(a[2]);
    b2Dot[3] = - Math.sin(a[3]);
    double[] b3Dot = new double[nbA];
    b3Dot[1] = 1.5d * Math.sqrt(a[1]);
    b3Dot[3] = 1.0d;
    double[] b4Dot = new double[nbA];
    double sinb1 = Math.sin(b1);
    for(int loopa = 0; loopa < nbA ; loopa++) {
      b4Dot[loopa] = b2 * - sinb1 * b1Dot[loopa] + 
          cosb1 * b2Dot[loopa] + 1.0d * b3Dot[loopa];
    }
    return new DoubleDerivatives(b4, b4Dot);
  }
  
  /**
   * Returns the value of the function f and its derivative with respect to the "input" variables implied by the 
   * parameters a.
   * @param a The input parameters.
   * @return The value of f and its derivatives.
   */
  static public DoubleSad f_Sad_Automatic(DoubleSad[] a) {
    DoubleSad b1 = MathSad.plus(a[0], MathSad.exp(a[1]));
    DoubleSad b2 = MathSad.plus(MathSad.sin(a[2]), MathSad.cos(a[3]));
    DoubleSad b3 = MathSad.plus(MathSad.pow(a[1], 1.5d), a[3]);
    DoubleSad b4 = MathSad.plus(MathSad.multipliedBy(MathSad.cos(b1), b2), b3);
    return b4;
  }
  
  /**
   * Returns the value of the function f and its derivatives with respect to all the inputs.
   * The function is f(a) = cos(a[0]+ exp(a[1])) (sin(a[2]) + cos(a[3])) + (a[1])^(3/2) + a[3].
   * The derivatives are computed by Adjoint Algorithmic Differentiation.
   * @param a The parameters
   * @return The value of f and its derivatives.
   */
  static public DoubleDerivatives f_Aad(double[] a) {
    // Forward sweep - function
    double b1 = a[0] + Math.exp(a[1]);
    double b2 = Math.sin(a[2]) + Math.cos(a[3]);
    double b3 = Math.pow(a[1], 1.5d) + a[3];
    double b4 = Math.cos(b1) * b2 + b3;
    // Backward sweep - derivatives
    double[] aBar = new double[a.length];
    double b4Bar = 1.0;
    double b3Bar = 1.0d * b4Bar;
    double b2Bar = Math.cos(b1) * b4Bar + 0.0 * b3Bar;
    double b1Bar = b2 * - Math.sin(b1) * b4Bar + 0.0 * b3Bar + 0.0 * b2Bar;
    aBar[3] = 1.0 * b3Bar - Math.sin(a[3]) * b2Bar + 0.0 * b1Bar;
    aBar[2] = Math.cos(a[2]) * b2Bar;
    aBar[1] = 1.5d * Math.sqrt(a[1]) * b3Bar + Math.exp(a[1]) * b1Bar;
    aBar[0] = 1.0 * b1Bar;
    return new DoubleDerivatives(b4, aBar);
  }
  
  /**
   * Returns the value of the function f and its derivatives with respect to all the inputs.
   * The function is f(a) = cos(a[0]+ exp(a[1])) (sin(a[2]) + cos(a[3])) + (a[1])^(3/2) + a[3].
   * The value of exp(a[1]) is cached and used twice.
   * @param a The parameters
   * @return The value of f and its derivatives.
   */
  static public DoubleDerivatives f_Aad_Optimized(double[] a) {
    // Forward sweep - function
    double tmp1 = Math.exp(a[1]);
    double tmp2 = Math.pow(a[1], 1.5d);
    double b1 = a[0] + tmp1;
    double b2 = Math.sin(a[2]) + Math.cos(a[3]);
    double tmp3 = Math.cos(b1);
    double b3 = tmp2 + a[3];
    double b4 = tmp3 * b2 + b3;
    // Backward sweep - derivatives
    double[] aBar = new double[a.length];
    double b4Bar = 1.0;
    double b3Bar = 1.0d * b4Bar;
    double b2Bar = tmp3 * b4Bar + 0.0 * b3Bar;
    double b1Bar = b2 * - Math.sin(b1) * b4Bar + 0.0 * b3Bar + 0.0 * b2Bar;
    aBar[3] = 1.0 * b3Bar - Math.sin(a[3]) * b2Bar + 0.0 * b1Bar;
    aBar[2] = Math.cos(a[2]) * b2Bar;
    aBar[1] = 1.5d * tmp2 / a[1] * b3Bar + tmp1 * b1Bar;
    aBar[0] = 1.0 * b1Bar;
    return new DoubleDerivatives(b4, aBar);
  }
  
  /**
   * Returns the value of the function f and record all the operation in the tape.
   * The tape can be interpreted to obtain the derivatives of f.
   * @param a The input parameters.
   * @param tape The tape where the operations are recorded. The tape is modified by the method.
   * @return The value of f.
   */
  static public DoubleAad f_Aad_Automatic(DoubleAad[] a, TapeAad tape) {
    DoubleAad b1 = MathAad.plus(a[0], MathAad.exp(a[1], tape), tape);
    DoubleAad b2 = MathAad.plus(MathAad.sin(a[2], tape), MathAad.cos(a[3], tape), tape);
    DoubleAad b3 = MathAad.plus(MathAad.pow(a[1], 1.5d, tape), a[3], tape);
    DoubleAad b4 = MathAad.plus(MathAad.multipliedBy(MathAad.cos(b1, tape), b2, tape), b3, tape);
    return b4;
  }

}
