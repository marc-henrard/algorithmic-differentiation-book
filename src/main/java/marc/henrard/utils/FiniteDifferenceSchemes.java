/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.utils;

/**
 * Enumeration of the different finite difference schemes used to compute the first order derivatives of functions.
 */
public enum FiniteDifferenceSchemes {
  
  /** Forward difference: $\frac{f(a+\epsilon)-f(a)}{\epsilon}$ */
  FORWARD,
  /** Backward difference: $\frac{f(a+\epsilon)-f(a)}{\epsilon}$ */
  BACKWARD,
  /** Symmetrical difference: $\frac{f(a+\epsilon)-f(a-\epsilon)}{2\epsilon}$ */
  SYMMETRICAL,
  /** Fourth order: $\frac{-f(a+2\epsilon)+8f(a+\epsilon)-8F(a-\epsilon) + f(a-2\epsilon)}{12\epsilon}$ */
  FOURTH_ORDER

}
