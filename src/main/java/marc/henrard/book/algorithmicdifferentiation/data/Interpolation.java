/**
 * Copyright (C) 2015 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.data;

import marc.henrard.book.algorithmicdifferentiation.tape.TapeAad;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleAad;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleDerivatives;

/**
 * Interface to interpolation methods.
 */
public interface Interpolation {
  
  /**
   * Returns the interpolated level for a given input value.
   * @param x The input value. It should be between the nodes smallest value and largest value.
   * @param data The data defining the interpolation nodes.
   * @return The interpolated level.
   */
  public double interpolate(double x, InterpolationDataDouble data);
  
  /**
   * Returns the first order derivative of the method as a function of the value x.
   * @param x The input value. It should be between the nodes smallest value and largest value.
   * @param data The data defining the interpolation nodes.
   * @return The derivative with respect to X.
   */
  public double derivativeX(double x, InterpolationDataDouble data);
  
  /**
   * Returns the interpolated level for a given input value and the derivatives of the level with respect
   * to the node levels.
   * @param x The input value.
   * @param data The data defining the interpolation nodes.
   * @return The interpolated level and its derivatives.
   */
  public DoubleDerivatives interpolate_Aad(double x, InterpolationDataDouble data);
  
  /**
   * Returns the interpolated level for a given input value. The method operations are recorded in a AAD tape.
   * @param x The input value.
   * @param data The data defining the interpolation nodes.
   * @param tape The AAD tape recording the operations.
   * @return
   */
  public DoubleAad interpolate_Aad_Automatic(DoubleAad x, InterpolationDataDoubleAad data, TapeAad tape);

}
