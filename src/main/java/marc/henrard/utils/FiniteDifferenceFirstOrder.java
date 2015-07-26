/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.utils;

import java.util.function.Function;

/**
 * Tools to compute the first order differentiation of function by finite difference. 
 * <p>
 * The functions are functions from R^n - represented by a double[] - to R - represented by a Double.
 */
public class FiniteDifferenceFirstOrder {
  
  /**
   * Compute the first order differentiation of function by finite difference. 
   * The underlying function take a double[] as input and output a Double.
   * @param function The function to differentiate.
   * @param x The value at which the derivative should be computed. 
   * @param epsilon The finite difference shift.
   * @param scheme The finite difference scheme.
   * @return The derivative.
   */
  public static double[] differentiate(Function<double[], Double> function, double[] x,
      double epsilon, FiniteDifferenceSchemes scheme) {
    int nbX = x.length;
    double[] derivative = new double[nbX];
    switch (scheme) {
      case FORWARD: {
        Double y0 = function.apply(x);
        for (int loopx = 0; loopx < nbX; loopx++) {
          double[] xShifted = x.clone();
          xShifted[loopx] += epsilon;
          derivative[loopx] = (function.apply(xShifted) - y0)/epsilon;
        }
        return derivative;
      }
      case BACKWARD: {
        Double y0 = function.apply(x);
        for(int loopx = 0; loopx < nbX; loopx++) {
          double[] xShifted = x.clone();
          xShifted[loopx] -= epsilon;
          derivative[loopx] = (y0 - function.apply(xShifted))/epsilon;
        }
        return derivative;
      }
      case SYMMETRICAL: {
        for(int loopx = 0; loopx < nbX; loopx++) {
          double[] xShiftedP = x.clone();
          double[] xShiftedM = x.clone();
          xShiftedP[loopx] += epsilon;
          xShiftedM[loopx] -= epsilon;
          derivative[loopx] = (function.apply(xShiftedP) - function.apply(xShiftedM)) / (2 * epsilon);
        }
        return derivative;
      }
      case FOURTH_ORDER: {
        for(int loopx = 0; loopx < nbX; loopx++) {
          double[] xShiftedP1 = x.clone();
          double[] xShiftedM1 = x.clone();
          double[] xShiftedP2 = x.clone();
          double[] xShiftedM2 = x.clone();
          xShiftedP1[loopx] += epsilon;
          xShiftedP2[loopx] += 2 * epsilon;
          xShiftedM1[loopx] -= epsilon;
          xShiftedM2[loopx] -= 2 * epsilon;
          derivative[loopx] = (-function.apply(xShiftedP2) + 8 * function.apply(xShiftedP1)
              - 8 * function.apply(xShiftedM1) + function.apply(xShiftedM2)) / (12 * epsilon);
        }
        return derivative;
      }
      default:
        throw new IllegalArgumentException(
            "Finite difference scheme should be FORWARD, BACKWARD, SYMMETRICAL or FOURTH_ORDER");
    }
  }

}
