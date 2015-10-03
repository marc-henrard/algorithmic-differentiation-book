/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.data;

import marc.henrard.book.algorithmicdifferentiation.mathad.MathAad;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeAad;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleAad;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleDerivatives;

/**
 * Implementation of the linear interpolation.
 * <p>
 * Implementation of the value and different algorithmic differentiation versions.
 */
public class InterpolationLinear implements Interpolation {
  
  /**
   * Returns the linear interpolated level for a given input value.
   * @param x The input value. It should be between the nodes smallest value and largest value.
   * @param data The data defining the interpolation nodes.
   * @return The interpolated level.
   */
  @Override
  public double interpolate(double x, InterpolationDataDouble data) {
    int i = upperIndex(x, data.nodes());
    double num = 1.0d / (data.nodes()[i] - data.nodes()[i-1]);
    double slope = (data.nodesValue()[i] - data.nodesValue()[i-1]) * num;
    double interp = data.nodesValue()[i-1] + (x - data.nodes()[i-1]) * slope;
    return interp;
  }
  
  /**
   * Returns the first order derivative of the method as a function of the value x. The linear interpolation may not
   * be derivable at the node points. In that case the left derivative is returned.
   * @param x The input value. It should be between the nodes smallest value and largest value.
   * @param data The data defining the interpolation nodes.
   * @return The derivative with respect to X.
   */
  @Override
  public double derivativeX(double x, InterpolationDataDouble data) {
    int i = upperIndex(x, data.nodes());
    double num = 1.0d / (data.nodes()[i] - data.nodes()[i-1]);
    double slope = (data.nodesValue()[i] - data.nodesValue()[i-1]) * num;
    return slope;
  }

  /**
   * Returns the linear interpolated level for a given input value and the derivatives of the level with respect
   * to the node levels.
   * @param x The input value.
   * @param data The data defining the interpolation nodes.
   * @return The interpolated level.
   */
  @Override
  public DoubleDerivatives interpolate_Aad(double x, InterpolationDataDouble data) {
    // Forward sweep - function
    int i = upperIndex(x, data.nodes());
    double num = 1.0d / (data.nodes()[i] - data.nodes()[i-1]);
    double slope = (data.nodesValue()[i] - data.nodesValue()[i-1]) * num;
    double interp = data.nodesValue()[i-1] + (x - data.nodes()[i-1]) * slope;
    // Forward sweep - derivatives
    double interpBar = 1.0;
    double slopeBar = (x - data.nodes()[i-1]) * interpBar;
    int nbInputs = data.nodesValue().length;
    double[] inputBar = new double[nbInputs];
    inputBar[i-1] += interpBar;
    inputBar[i] += num * slopeBar;
    inputBar[i-1] += -num * slopeBar;
    return new DoubleDerivatives(interp, inputBar);
  }

  @Override
  public DoubleAad interpolate_Aad_Automatic(DoubleAad x, InterpolationDataDoubleAad data, TapeAad tape) {
    int i = upperIndex(x.value(), data.nodes());
    DoubleAad slope =  MathAad.multipliedBy(MathAad.minus(data.nodesValue()[i], data.nodesValue()[i-1], tape), 
        1.0d / (data.nodes()[i] - data.nodes()[i-1]), tape);
    DoubleAad interp = MathAad.plus(data.nodesValue()[i-1], 
        MathAad.multipliedBy(slope, MathAad.plus(x, - data.nodes()[i-1], tape), tape), tape);
    return interp;
  }
  
  /* Returns the upper bound for a given value in the nodes */
  private static int upperIndex(double value, double[] nodes) {
    if (value == nodes[0]) {
      return 1;
    }
    int i = 1;
    while(nodes[i] < value) {
      i++;
    }
    return i;
  }

}
