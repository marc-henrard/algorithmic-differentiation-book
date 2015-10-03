/**
 * Copyright (C) 2015 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.finance.data;

import marc.henrard.book.algorithmicdifferentiation.data.Interpolation;
import marc.henrard.book.algorithmicdifferentiation.data.InterpolationDataDoubleAad;
import marc.henrard.book.algorithmicdifferentiation.data.InterpolationLinear;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeAad;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleAad;

/**
 * Description of the Black implied smile with volatility strike dependent.
 */
public class BlackSmileStrikeAad implements BlackSmileDescriptionAad {
  
  /* The default interpolation (linear) */
  private static final Interpolation DEFAULT_INTERPOLATION = new InterpolationLinear();

  /* The interpolation used for the smile. */
  private final Interpolation interpolation;
  /* The smile data. */
  private final InterpolationDataDoubleAad smile;

  /**
   * Constructor of the smile from the interpolator and the smile data. The volatility is interpolated on the strike.
   * @param interpolation The interpolation scheme.
   * @param smile The smile data.
   */
  public BlackSmileStrikeAad(Interpolation interpolation, InterpolationDataDoubleAad smile) {
    this.interpolation = interpolation;
    this.smile = smile;
  }

  /**
   * Constructor of the smile from the interpolator and the smile data. 
   * The volatility is linearly interpolated on the strike.
   * @param smile The smile data.
   */
  public BlackSmileStrikeAad(InterpolationDataDoubleAad smile) {
    this.interpolation = DEFAULT_INTERPOLATION;
    this.smile = smile;
  }

  @Override
  public DoubleAad volatility(DoubleAad strike, DoubleAad forward, TapeAad tape) {
    return interpolation.interpolate_Aad_Automatic(strike, smile, tape);
  }

}
