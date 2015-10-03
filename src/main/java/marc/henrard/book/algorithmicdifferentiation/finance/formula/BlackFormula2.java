/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.finance.formula;

import org.apache.commons.math3.distribution.NormalDistribution;

import marc.henrard.book.algorithmicdifferentiation.mathad.MathAad;
import marc.henrard.book.algorithmicdifferentiation.mathad.MathSad;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeAad;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleAad;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleDerivatives;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleSad;

/**
 * Implementation of the Black-Scholes formula for option pricing.
 * <p>
 * Implementation of the price and different algorithmic differentiation versions.
 * <p>
 * The mathematical library underlying is 
 * <a href="http://commons.apache.org/proper/commons-math/">Apache Commons Mathematics Library</a>
 */
public class BlackFormula2 {

  /** The normal distribution implementation. */
  private static final NormalDistribution NORMAL = new NormalDistribution(0.0d, 1.0d);

  /**
   * Returns the option price computed by the Black-Scholes formula.
   * @param forward The forward price/rate.
   * @param volatility The log-normal volatility of the model.
   * @param numeraire The numeraire.
   * @param strike The strike price/rate.
   * @param expiry The time to expiry.
   * @param isCall The call (true) / put (false) flag.
   * @return The price.
   * @throws MathException 
   */
  public static double price(
      double forward, 
      double volatility, 
      double numeraire,
      double strike, 
      double expiry, 
      boolean isCall) {
    double periodVolatility = volatility * Math.sqrt(expiry);
    double dPlus = Math.log(forward / strike) / periodVolatility + 0.5d * periodVolatility;
    double dMinus = dPlus - periodVolatility;
    double omega = isCall ? 1.0d : -1.0d;
    double nPlus = NORMAL.cumulativeProbability(omega * dPlus);
    double nMinus = NORMAL.cumulativeProbability(omega * dMinus);
    double price = numeraire * omega * (forward * nPlus - strike * nMinus);
    return price;
  }

  /**
   * Returns the option price for the Black-Scholes formula and its derivatives with respect to 
   * [0] forward, [1] volatility, [2] numeraire, [3] strike, and [4] expiry.
   * The derivatives are computed by Standard Algorithmic Differentiation.
   * @param forward The forward price/rate.
   * @param volatility The log-normal volatility of the model.
   * @param numeraire The numeraire.
   * @param strike The strike price/rate.
   * @param expiry The time to expiry.
   * @param isCall The call (true) / put (false) flag.
   * @return The price and derivatives.
   * @throws MathException 
   */
  public static DoubleDerivatives price_Sad(
      double forward, 
      double volatility, 
      double numeraire,
      double strike, 
      double expiry, 
      boolean isCall) {
    // Forward sweep - function
    double omega = isCall ? 1.0d : -1.0d;
    double periodVolatility = volatility * Math.sqrt(expiry);
    double dPlus = Math.log(forward / strike) / periodVolatility + 0.5d * periodVolatility;
    double dMinus = dPlus - periodVolatility;
    double nPlus = NORMAL.cumulativeProbability(omega * dPlus);
    double nMinus = NORMAL.cumulativeProbability(omega * dMinus);
    double price = numeraire * omega * (forward * nPlus - strike * nMinus);
    // Forward sweep - derivatives
    int nbInputs = 5;
    double[] inputDot = new double[nbInputs];
    for (int loopinput = 0; loopinput < nbInputs; loopinput++) {
      inputDot[loopinput] = 1.0d;
    }
    double[] periodVolatilityDot = new double[nbInputs];
    periodVolatilityDot[1] = Math.sqrt(expiry) * inputDot[1];
    periodVolatilityDot[4] = volatility * 0.5d / Math.sqrt(expiry) * inputDot[4];
    double[] dPlusDot = new double[nbInputs];
    for (int loopinput = 0; loopinput < nbInputs; loopinput++) {
      dPlusDot[loopinput] = (Math.log(forward / strike) * -1.0d / (periodVolatility * periodVolatility) + 0.5d)
          * periodVolatilityDot[loopinput];
    }
    dPlusDot[0] += 1.0d / (periodVolatility * forward) * inputDot[0];
    dPlusDot[3] += -1.0d / (periodVolatility * forward) * inputDot[3];
    double[] dMinusDot = new double[nbInputs];
    for (int loopinput = 0; loopinput < nbInputs; loopinput++) {
      dMinusDot[loopinput] = dPlusDot[loopinput] - periodVolatilityDot[loopinput];
    }
    double[] nPlusDot = new double[nbInputs];
    double nPdfpPlus = NORMAL.density(omega * dPlus);
    for (int loopinput = 0; loopinput < nbInputs; loopinput++) {
      nPlusDot[loopinput] = nPdfpPlus * omega * dPlusDot[loopinput];
    }
    double[] nMinusDot = new double[nbInputs];
    double nPdfdMinus = NORMAL.density(omega * dMinus);
    for (int loopinput = 0; loopinput < nbInputs; loopinput++) {
      nMinusDot[loopinput] = nPdfdMinus * omega * dMinusDot[loopinput];
    }
    double[] priceDot = new double[nbInputs];
    for (int loopinput = 0; loopinput < nbInputs; loopinput++) {
      priceDot[loopinput] = numeraire * omega * forward * nPlusDot[loopinput]
          - numeraire * omega * strike * nMinusDot[loopinput];
    }
    priceDot[0] += numeraire * omega * nPlus * inputDot[0];
    priceDot[2] += omega * (forward * nPlus - strike * nMinus) * inputDot[2];
    priceDot[3] += -numeraire * omega * nMinus * inputDot[3];
    return new DoubleDerivatives(price, priceDot);
  }

  /**
   * Returns the option price for the Black-Scholes formula and its derivatives with respect to 
   * [0] forward, [1] volatility, [2] numeraire, [3] strike, and [4] expiry.
   * The derivatives are computed by automatic Standard Algorithmic Differentiation.
   * <p>
   * The mathematical library underlying the automatic AD is 
   *  <a href="https://dst.lbl.gov/ACSSoftware/colt/index.html">Colt</a> 
   * @param forward The forward price/rate.
   * @param volatility The log-normal volatility of the model.
   * @param numeraire The numeraire.
   * @param strike The strike price/rate.
   * @param expiry The time to expiry.
   * @param isCall The call (true) / put (false) flag.
   * @return The price and derivatives.
   * @throws MathException 
   */
  public static DoubleSad price_Sad_Automatic(
      double forward, 
      double volatility, 
      double numeraire,
      double strike, 
      double expiry, 
      boolean isCall) {
    int nbInputs = 5;
    double[] input = new double[nbInputs];
    input[0] = forward;
    input[1] = volatility;
    input[2] = numeraire;
    input[3] = strike;
    input[4] = expiry;
    DoubleSad[] inputSad = DoubleSad.init(input);
    double omega = isCall ? 1.0d : -1.0d;
    DoubleSad periodVolatility = MathSad.multipliedBy(inputSad[1], MathSad.sqrt(inputSad[4]));
    DoubleSad dPlus = MathSad.plus(
        MathSad.dividedBy(MathSad.log(MathSad.dividedBy(inputSad[0], inputSad[3])), periodVolatility), 
        MathSad.multipliedBy(periodVolatility, 0.5d));
    DoubleSad dMinus = MathSad.minus(dPlus, periodVolatility);
    DoubleSad nPlus = MathSad.normalCdf(MathSad.multipliedBy(dPlus, omega));
    DoubleSad nMinus = MathSad.normalCdf(MathSad.multipliedBy(dMinus, omega));
    DoubleSad price = MathSad.multipliedBy(MathSad.multipliedBy(inputSad[2], omega), 
        MathSad.minus(MathSad.multipliedBy(inputSad[0], nPlus), MathSad.multipliedBy(inputSad[3], nMinus)));
    return price;
  }

  /**
   * Returns the option price for the Black-Scholes formula and its derivatives with respect to 
   * [0] forward, [1] volatility, [2] numeraire, [3] strike, and [4] expiry.
   * The derivatives are computed by Adjoint Algorithmic Differentiation.
   * @param forward The forward price/rate.
   * @param volatility The log-normal volatility of the model.
   * @param numeraire The numeraire.
   * @param strike The strike price/rate.
   * @param expiry The time to expiry.
   * @param isCall The call (true) / put (false) flag.
   * @return The price and derivatives.
   * @throws MathException 
   */
  public static DoubleDerivatives price_Aad(
      double forward, 
      double volatility, 
      double numeraire,
      double strike, 
      double expiry, 
      boolean isCall) {
    // Forward sweep - function
    double omega = isCall ? 1.0d : -1.0d;
    double periodVolatility = volatility * Math.sqrt(expiry);
    double dPlus = Math.log(forward / strike) / periodVolatility + 0.5d * periodVolatility;
    double dMinus = dPlus - periodVolatility;
    double nPlus = NORMAL.cumulativeProbability(omega * dPlus);
    double nMinus = NORMAL.cumulativeProbability(omega * dMinus);
    double price = numeraire * omega * (forward * nPlus - strike * nMinus);
    // Backward sweep - derivatives
    double priceBar = 1.0;
    double nMinusBar = numeraire * omega * -strike * priceBar;
    double nPlusBar = numeraire * omega * forward * priceBar;
    double dMinusBar = NORMAL.density(omega * dMinus) * omega * nMinusBar;
    double dPlusBar = 1.0d * dMinusBar + NORMAL.density(omega * dPlus) * omega * nPlusBar;
    // Note: dPlusBar is always 0; it is the optimal exercise boundary.
    double periodVolatilityBar = -1.0d * dMinusBar +
        (-Math.log(forward / strike) / (periodVolatility * periodVolatility) + 0.5d) * dPlusBar;
    double[] inputBar = new double[5]; // forward, volatility, numeraire, strike, expiry
    inputBar[4] = volatility * 0.5 / Math.sqrt(expiry) * periodVolatilityBar;
    inputBar[3] = -1.0d / strike / periodVolatility * dPlusBar + numeraire * omega * -nMinus * priceBar;
    inputBar[2] = omega * (forward * nPlus - strike * nMinus) * priceBar;
    inputBar[1] = Math.sqrt(expiry) * periodVolatilityBar;
    inputBar[0] = 1.0d / forward / periodVolatility * dPlusBar + numeraire * omega * nPlus * priceBar;
    return new DoubleDerivatives(price, inputBar);
  }

  /**
   * Returns the option price for the Black-Scholes formula and its derivatives with respect to 
   * [0] forward, [1] volatility, [2] numeraire, [3] strike, and [4] expiry.
   * The derivatives are computed by Adjoint Algorithmic Differentiation.
   * The formula is optimized to reduce computation time by using domain specific knowledge.
   * @param forward The forward price/rate.
   * @param volatility The log-normal volatility of the model.
   * @param numeraire The numeraire.
   * @param strike The strike price/rate.
   * @param expiry The time to expiry.
   * @param isCall The call (true) / put (false) flag.
   * @return The price and derivatives.
   * @throws MathException 
   */
  public static DoubleDerivatives price_Aad_Optimized(
      double forward, 
      double volatility, 
      double numeraire,
      double strike, 
      double expiry, 
      boolean isCall) {
    // Forward sweep - function
    double omega = isCall ? 1.0d : -1.0d;
    double sqrtExpiry = Math.sqrt(expiry);
    double periodVolatility = volatility * sqrtExpiry;
    double dPlus = Math.log(forward / strike) / periodVolatility + 0.5d * periodVolatility;
    double dMinus = dPlus - periodVolatility;
    double nPlus = NORMAL.cumulativeProbability(omega * dPlus);
    double nMinus = NORMAL.cumulativeProbability(omega * dMinus);
    double price = numeraire * omega * (forward * nPlus - strike * nMinus);
    // Backward sweep - derivatives
    double priceBar = 1.0;
    double nMinusBar = numeraire * omega * -strike * priceBar;
    double dMinusBar = NORMAL.density(omega * dMinus) * omega * nMinusBar;
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
   * Returns the option price for the Black-Scholes formula as a {@link DoubleAad}.
   * The method also update the tape of type {@link TapeAad} which can be use to compute derivatives
   * by automatic AD.
   * @param forward The forward price/rate.
   * @param volatility The log-normal volatility of the model.
   * @param numeraire The numeraire.
   * @param strike The strike price/rate.
   * @param expiry The time to expiry.
   * @param isCall The call (true) / put (false) flag.
   * @return The price and derivatives.
   * @throws MathException 
   */
  public static DoubleAad price_Aad_Automatic(
      DoubleAad forward, 
      DoubleAad volatility, 
      DoubleAad numeraire,
      DoubleAad strike, 
      DoubleAad expiry, 
      boolean isCall, 
      TapeAad tape) {
    double omega = isCall ? 1.0d : -1.0d;
    DoubleAad periodVolatility = MathAad.multipliedBy(volatility, MathAad.sqrt(expiry, tape), tape);
    DoubleAad dPlus = MathAad.plus(MathAad.dividedBy(MathAad.log(MathAad.dividedBy(forward, strike, tape), tape), 
        periodVolatility, tape), MathAad.multipliedBy(periodVolatility, 0.5d, tape), tape);
    DoubleAad dMinus = MathAad.minus(dPlus, periodVolatility, tape);
    DoubleAad nPlus = MathAad.normalCdf(MathAad.multipliedBy(dPlus, omega, tape), tape);
    DoubleAad nMinus = MathAad.normalCdf(MathAad.multipliedBy(dMinus, omega, tape), tape);
    DoubleAad price = MathAad.multipliedBy(MathAad.multipliedBy(numeraire, omega, tape), 
        MathAad.minus(MathAad.multipliedBy(forward, nPlus, tape), MathAad.multipliedBy(strike, nMinus, tape), tape), tape);
    return price;
  }
  
}
