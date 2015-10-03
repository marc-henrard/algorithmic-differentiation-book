/**
 * Copyright (C) 2015 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.finance.data;

import marc.henrard.book.algorithmicdifferentiation.type.DoubleDerivatives;

/**
 * Description of the smile related to an implied Black volatility.
 */
public interface BlackSmileDescription {
  
  /**
   * Returns the implied Black volatility.
   * @param strike The strike.
   * @param forward The forward.
   * @return The volatility.
   */
  double volatility(double strike, double forward);
  
  /**
   * Returns the sensitivity of the volatility to the parameters describing the smile.
   * @param strike The strike.
   * @param forward The forward.
   * @return The sensitivity.
   */
  DoubleDerivatives volatilityParameterSensitivity(double strike, double forward);
  
  /**
   * Returns the derivative of the smile with respect to the strike.
   * @param strike The strike.
   * @param forward The forward.
   * @return The derivatives.
   */
  double derivativeStrike(double strike, double forward);

}
