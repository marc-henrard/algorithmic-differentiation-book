/**
 * Copyright (C) 2015 - present by Marc Henrard
 * 
 * <p> The code complements the book: Henrard, Marc, Algorithmic Differentiation in Finance, 2015.
 */
package marc.henrard.book.algorithmicdifferentiation.finance.formula;

import cern.jet.random.Normal;
import marc.henrard.book.algorithmicdifferentiation.finance.data.BlackSmileDescription;
import marc.henrard.book.algorithmicdifferentiation.finance.data.BlackSmileDescriptionAad;
import marc.henrard.book.algorithmicdifferentiation.mathad.MathAad;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeAad;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleAad;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleDerivatives;

/**
 * Implementation of the Black-Scholes formula for option pricing with a smile (implied volatility is strike dependent).
 * <p>Implementation of the price and different algorithmic differentiation versions.
 */
public class BlackSmileFormula {
  
  /* The normal distribution implementation. */
  private static final Normal NORMAL = new Normal(0.0d, 1.0d, null);
  
  /**
   * Default implementation. Uses the linear interpolation.
   */
  public static BlackSmileFormula DEFAULT = new BlackSmileFormula();

  /**
   * Returns the option price for the Black-Scholes formula.
   * @param forward The forward price/rate.
   * @param smile The Black implied volatility by strike.
   * @param numeraire The numeraire.
   * @param strike The strike price/rate.
   * @param expiry The time to expiry.
   * @param isCall The call (true) / put (false) flag.
   * @return The price.
   */
  public double price(double forward, BlackSmileDescription smile, double numeraire,
      double strike, double expiry, boolean isCall) {
    double volatility = smile.volatility(strike, forward);
    double periodVolatility = volatility * Math.sqrt(expiry);
    double dPlus = Math.log(forward / strike) / periodVolatility + 0.5d * periodVolatility;
    double dMinus = dPlus - periodVolatility;
    double omega = isCall ? 1.0d : -1.0d;
    double nPlus = NORMAL.cdf(omega * dPlus);
    double nMinus = NORMAL.cdf(omega * dMinus);
    double price = numeraire * omega * (forward * nPlus - strike * nMinus);
    return price;
  }

  /**
   * Returns the option price for the Black-Scholes formula and its derivatives with respect to 
   * [0] forward, [1] volatility, [2] numeraire, [3] strike, and [4] expiry.
   * The derivatives are computed by Adjoint Algorithmic Differentiation.
   * The formula is optimized to reduce computation time by using domain specific knowledge.
   * <p> 
   * The derivative with respect to the forward is <i>Sticky Strike</i>.
   * The <i>Sticky Strike</i> explanation and formula are described in Section 5.2.1 of the book.
   * @param forward The forward price/rate.
   * @param smile The Black implied volatility by strike.
   * @param numeraire The numeraire.
   * @param strike The strike price/rate.
   * @param expiry The time to expiry.
   * @param isCall The call (true) / put (false) flag.
   * @return The price and derivatives.
   */
  public DoubleDerivatives price_Aad_StickyStrike(double forward, BlackSmileDescription smile, double numeraire,
      double strike, double expiry, boolean isCall) {
    // Forward sweep - function
    double volatility = smile.volatility(strike, forward);
    double omega = isCall ? 1.0d : -1.0d;
    double sqrtExpiry = Math.sqrt(expiry);
    double periodVolatility = volatility * sqrtExpiry;
    double dPlus = Math.log(forward / strike) / periodVolatility + 0.5d * periodVolatility;
    double dMinus = dPlus - periodVolatility;
    double nPlus = NORMAL.cdf(omega * dPlus);
    double nMinus = NORMAL.cdf(omega * dMinus);
    double price = numeraire * omega * (forward * nPlus - strike * nMinus);
    // Backward sweep - derivatives
    double priceBar = 1.0;
    double nMinusBar = numeraire * omega * -strike * priceBar;
    double dMinusBar = NORMAL.pdf(omega * dMinus) * omega * nMinusBar;
    double periodVolatilityBar = -1.0d * dMinusBar;
    double[] inputBar = new double[5]; // forward, volatility, numeraire, strike, expiry
    inputBar[4] = volatility * 0.5 / sqrtExpiry * periodVolatilityBar;
    inputBar[3] = numeraire * omega * -nMinus * priceBar;
    inputBar[2] = omega * (forward * nPlus - strike * nMinus) * priceBar;
    inputBar[1] = sqrtExpiry * periodVolatilityBar;
    inputBar[0] = numeraire * omega * nPlus * priceBar;
    return new DoubleDerivatives(price, inputBar);
  }

  /**
   * Returns the option price for the Black-Scholes formula and its derivatives with respect to 
   * [0] forward, [1] volatility, [2] numeraire, [3] strike, and [4] expiry.
   * The derivatives are computed by Adjoint Algorithmic Differentiation.
   * The formula is optimized to reduce computation time by using domain specific knowledge.
   * <p> 
   * The derivative with respect to the forward is <i>Sticky Simple Moneyness</i>.
   * The <i>Sticky Simple Moneyness</i> explanation and formula are described in Section 5.2.2 of the book, Equation (5.6).
   * @param forward The forward price/rate.
   * @param smile The Black implied volatility by strike.
   * @param numeraire The numeraire.
   * @param strike The strike price/rate.
   * @param expiry The time to expiry.
   * @param isCall The call (true) / put (false) flag.
   * @return The price and derivatives.
   */
  public DoubleDerivatives price_Aad_StickySimpleMoney(double forward, BlackSmileDescription smile, double numeraire,
      double strike, double expiry, boolean isCall) {
    // Forward sweep - function
    double volatility = smile.volatility(strike, forward);
    double omega = isCall ? 1.0d : -1.0d;
    double sqrtExpiry = Math.sqrt(expiry);
    double periodVolatility = volatility * sqrtExpiry;
    double dPlus = Math.log(forward / strike) / periodVolatility + 0.5d * periodVolatility;
    double dMinus = dPlus - periodVolatility;
    double nPlus = NORMAL.cdf(omega * dPlus);
    double nMinus = NORMAL.cdf(omega * dMinus);
    double price = numeraire * omega * (forward * nPlus - strike * nMinus);
    // Backward sweep - derivatives
    double priceBar = 1.0;
    double nMinusBar = numeraire * omega * -strike * priceBar;
    double dMinusBar = NORMAL.pdf(omega * dMinus) * omega * nMinusBar;
    double periodVolatilityBar = -1.0d * dMinusBar;
    double[] inputBar = new double[5]; // forward, volatility, numeraire, strike, expiry
    inputBar[4] = volatility * 0.5 / sqrtExpiry * periodVolatilityBar;
    inputBar[3] = numeraire * omega * -nMinus * priceBar;
    inputBar[2] = omega * (forward * nPlus - strike * nMinus) * priceBar;
    inputBar[1] = sqrtExpiry * periodVolatilityBar; // Vega
    inputBar[0] = numeraire * omega * nPlus * priceBar; // Delta
    double dVolatilitydK =  smile.derivativeStrike(strike, forward);
    inputBar[0] += - inputBar[1] * dVolatilitydK;
    return new DoubleDerivatives(price, inputBar);
  }

  /**
   * Returns the option price for the Black-Scholes formula and its derivatives with respect to 
   * [0] forward, [1] volatility, [2] numeraire, [3] strike, and [4] expiry.
   * The derivatives are computed by Adjoint Algorithmic Differentiation.
   * The formula is optimized to reduce computation time by using domain specific knowledge.
   * <p> 
   * The derivative with respect to the forward is <i>Sticky Log-Moneyness</i>.
   * The <i>Sticky Log-Moneyness</i> explanation and formula are described in Section 5.2.2 of the book, Equation (5.7).
   * @param forward The forward price/rate.
   * @param smile The Black implied volatility by strike.
   * @param numeraire The numeraire.
   * @param strike The strike price/rate.
   * @param expiry The time to expiry.
   * @param isCall The call (true) / put (false) flag.
   * @return The price and derivatives.
   */
  public DoubleDerivatives price_Aad_StickyLogMoney(double forward, BlackSmileDescription smile, double numeraire,
      double strike, double expiry, boolean isCall) {
    // Forward sweep - function
    double volatility = smile.volatility(strike, forward);
    double omega = isCall ? 1.0d : -1.0d;
    double sqrtExpiry = Math.sqrt(expiry);
    double periodVolatility = volatility * sqrtExpiry;
    double dPlus = Math.log(forward / strike) / periodVolatility + 0.5d * periodVolatility;
    double dMinus = dPlus - periodVolatility;
    double nPlus = NORMAL.cdf(omega * dPlus);
    double nMinus = NORMAL.cdf(omega * dMinus);
    double price = numeraire * omega * (forward * nPlus - strike * nMinus);
    // Backward sweep - derivatives
    double priceBar = 1.0;
    double nMinusBar = numeraire * omega * -strike * priceBar;
    double dMinusBar = NORMAL.pdf(omega * dMinus) * omega * nMinusBar;
    double periodVolatilityBar = -1.0d * dMinusBar;
    double[] inputBar = new double[5]; // forward, volatility, numeraire, strike, expiry
    inputBar[4] = volatility * 0.5 / sqrtExpiry * periodVolatilityBar;
    inputBar[3] = numeraire * omega * -nMinus * priceBar;
    inputBar[2] = omega * (forward * nPlus - strike * nMinus) * priceBar;
    inputBar[1] = sqrtExpiry * periodVolatilityBar; // Vega
    inputBar[0] = numeraire * omega * nPlus * priceBar; // Delta
    double dVolatilitydK = smile.derivativeStrike(strike, forward);
    inputBar[0] += - inputBar[1] * dVolatilitydK * strike / forward;
    return new DoubleDerivatives(price, inputBar);
  }

  /**
   * Returns the option price for the Black-Scholes formula.
   * @param forward The forward price/rate.
   * @param smile The Black implied volatility by strike.
   * @param numeraire The numeraire.
   * @param strike The strike price/rate.
   * @param expiry The time to expiry.
   * @param isCall The call (true) / put (false) flag.
   * @return The price.
   */
  public DoubleAad price_Aad_Automatic(DoubleAad forward, BlackSmileDescriptionAad smile, DoubleAad numeraire,
      DoubleAad strike, DoubleAad expiry, boolean isCall, TapeAad tape) {
    DoubleAad volatility = smile.volatility(strike, forward, tape);
    DoubleAad periodVolatility = MathAad.multipliedBy(volatility,  MathAad.sqrt(expiry, tape), tape);
    DoubleAad dPlus = MathAad.plus(MathAad.dividedBy(MathAad.log(MathAad.dividedBy(forward, strike, tape), tape),  
        periodVolatility, tape), MathAad.multipliedBy(periodVolatility, 0.5d, tape), tape);
    DoubleAad dMinus = MathAad.minus(dPlus, periodVolatility, tape);
    double omega = isCall ? 1.0d : -1.0d;
    DoubleAad nPlus = MathAad.normalCdf(MathAad.multipliedBy(dPlus, omega, tape), tape);
    DoubleAad nMinus = MathAad.normalCdf(MathAad.multipliedBy(dMinus, omega, tape), tape);
    DoubleAad price = MathAad.multipliedBy(MathAad.multipliedBy(numeraire, omega, tape), 
        MathAad.minus(MathAad.multipliedBy(forward, nPlus, tape), MathAad.multipliedBy(strike, nMinus, tape), tape), tape);
    return price;
  }

}
