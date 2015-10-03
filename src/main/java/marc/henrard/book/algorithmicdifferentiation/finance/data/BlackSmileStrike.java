/**
 * Copyright (C) 2015 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.finance.data;

import marc.henrard.book.algorithmicdifferentiation.data.Interpolation;
import marc.henrard.book.algorithmicdifferentiation.data.InterpolationDataDouble;
import marc.henrard.book.algorithmicdifferentiation.data.InterpolationLinear;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleDerivatives;

/**
 * Description of the Black implied smile with volatility strike dependent.
 */
public class BlackSmileStrike implements BlackSmileDescription {
  
  /* The default interpolation (linear) */
  private static final Interpolation DEFAULT_INTERPOLATION = new InterpolationLinear();

  /* The interpolation used for the smile. */
  private final Interpolation interpolation;
  /* The smile data. */
  private final InterpolationDataDouble smile;

  /**
   * Constructor of the smile from the interpolator and the smile data. The volatility is interpolated on the strike.
   * @param interpolation The interpolation scheme.
   * @param smile The smile data.
   */
  public BlackSmileStrike(Interpolation interpolation, InterpolationDataDouble smile) {
    this.interpolation = interpolation;
    this.smile = smile;
  }

  /**
   * Constructor of the smile from the interpolator and the smile data. 
   * The volatility is linearly interpolated on the strike.
   * @param smile The smile data.
   */
  public BlackSmileStrike(InterpolationDataDouble smile) {
    this.interpolation = DEFAULT_INTERPOLATION;
    this.smile = smile;
  }

  @Override
  public double volatility(double strike, double forward) {
    return interpolation.interpolate(strike, smile);
  }

  @Override
  public DoubleDerivatives volatilityParameterSensitivity(double strike, double forward) {
    return interpolation.interpolate_Aad(strike, smile);
  }

  @Override
  public double derivativeStrike(double strike, double forward) {
    return interpolation.derivativeX(strike, smile);
  }

}
