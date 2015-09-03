/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.finance;

import marc.henrard.book.algorithmicdifferentiation.tape.TapeAad;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeEntryAad;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeUtils;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleAad;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleDerivatives;
import marc.henrard.book.algorithmicdifferentiation.type.OperationTypeAad;

/**
 * Implementation of the SABR model with implied volatility by approximation formula for option pricing.
 * <p>
 * Reference: Hagan, P., Kumar, D., Lesniewski, A. and Woodward, D. (2002). Managing Smile Risk. Wilmott Magazine, September: 84-108
 * <p>
 * Implementation of the price and different algorithmic differentiation versions.
 */
public class SabrFormula {
  
  /**
   * The option price in the SABR model with implied volatility.
   * @param forward The forward price.
   * @param alpha SABR Alpha parameter.
   * @param beta SABR Beta parameter.
   * @param rho SABR Rho parameter. 
   * @param nu SABR Nu parameter.
   * @param numeraire The numeraire.
   * @param strike The option strike.
   * @param expiry The option expiry.
   * @param isCall The call (true) / put (false) flag.
   * @return The price.
   */
  public static double price(
      double forward, 
      double alpha, 
      double beta, 
      double rho, 
      double nu, 
      double numeraire, 
      double strike, 
      double expiry, 
      boolean isCall) {
    double volatility = SabrVolatilityFormula.volatility(forward, alpha, beta, rho, nu, strike, expiry);
    double price = BlackFormula.price(forward, volatility, numeraire, strike, expiry, isCall);
    return price;
  }
  
  /**
   * The option price in the SABR model with implied volatility and its derivatives with respect to the (8) inputs.
   * @param forward The forward price.
   * @param alpha SABR Alpha parameter.
   * @param beta SABR Beta parameter.
   * @param rho SABR Rho parameter. 
   * @param nu SABR Nu parameter.
   * @param numeraire The numeraire.
   * @param strike The option strike.
   * @param expiry The option expiry.
   * @param isCall The call (true) / put (false) flag.
   * @return The price and its derivatives.
   */
  public static DoubleDerivatives price_Aad(
      double forward, 
      double alpha, 
      double beta, 
      double rho, 
      double nu, 
      double numeraire, 
      double strike, 
      double expiry, 
      boolean isCall) {
    DoubleDerivatives volatility = SabrVolatilityFormula.volatility_Aad(forward, alpha, beta, rho, nu, strike, expiry);
    DoubleDerivatives price = BlackFormula.price_Aad_Optimized(forward, volatility.value(), numeraire, strike, expiry, isCall);
    double priceBar = 1.0d;
    double volatilityBar = price.derivatives()[1];
    double[] inputBar = new double[8]; // forward, alpha, beta, rho, nu, numeraire, strike, expiry
    inputBar[7] += price.derivatives()[4] * priceBar;
    inputBar[7] += volatility.derivatives()[6] * volatilityBar;
    inputBar[6] += price.derivatives()[3] * priceBar;
    inputBar[6] += volatility.derivatives()[5] * volatilityBar;
    inputBar[5] += price.derivatives()[2] * priceBar;
    inputBar[4] += volatility.derivatives()[4] * volatilityBar;
    inputBar[3] += volatility.derivatives()[3] * volatilityBar;
    inputBar[2] += volatility.derivatives()[2] * volatilityBar;
    inputBar[1] += volatility.derivatives()[1] * volatilityBar;
    inputBar[0] += price.derivatives()[0] * priceBar;
    inputBar[0] += volatility.derivatives()[0] * volatilityBar;
    return new DoubleDerivatives(price.value(), inputBar);
  }
  
  /**
   * Computes the option price in the SABR model with implied volatility and record the operations in the Tape.
   * @param forward The forward price.
   * @param alpha SABR Alpha parameter.
   * @param beta SABR Beta parameter.
   * @param rho SABR Rho parameter. 
   * @param nu SABR Nu parameter.
   * @param numeraire The numeraire.
   * @param strike The option strike.
   * @param expiry The option expiry.
   * @param isCall The call (true) / put (false) flag.
   * @param tape The tape where the operations are recorded. The tape is modified by the method.
   * @return The price and its derivatives.
   */
  public static DoubleAad price_Aad_Automatic(
      DoubleAad forward, 
      DoubleAad alpha, 
      DoubleAad beta, 
      DoubleAad rho, 
      DoubleAad nu, 
      DoubleAad numeraire, 
      DoubleAad strike, 
      DoubleAad expiry, 
      boolean isCall, 
      TapeAad tape) {
    DoubleAad volatility = SabrVolatilityFormula
        .volatility_Aad_Automatic(forward, alpha, beta, rho, nu, strike, expiry, tape);
    DoubleAad price = BlackFormula.price_Aad_Automatic(forward, volatility, numeraire, strike, expiry, isCall, tape);
    return price;
  }
  
  /**
   * Computes the option price in the SABR model with implied volatility and record the operations in the Tape.
   * <p>
   * The SABR implied volatility is computed with the code is written manually but the object returned is the one 
   * used in Automatic AAD. 
   * @param forward The forward price.
   * @param alpha SABR Alpha parameter.
   * @param beta SABR Beta parameter.
   * @param rho SABR Rho parameter. 
   * @param nu SABR Nu parameter.
   * @param numeraire The numeraire.
   * @param strike The option strike.
   * @param expiry The option expiry.
   * @param isCall The call (true) / put (false) flag.
   * @param tape The tape where the operations are recorded. The tape is modified by the method.
   * @return The price and its derivatives.
   */
  public static DoubleAad price_Aad_Automatic2(
      DoubleAad forward, 
      DoubleAad alpha, 
      DoubleAad beta, 
      DoubleAad rho, 
      DoubleAad nu, 
      DoubleAad numeraire, 
      DoubleAad strike, 
      DoubleAad expiry, 
      boolean isCall, 
      TapeAad tape) {
    DoubleAad volatility = SabrVolatilityFormula
        .volatility_Aad_Automatic2(forward, alpha, beta, rho, nu, strike, expiry, tape);
    DoubleAad price = BlackFormula.price_Aad_Automatic(forward, volatility, numeraire, strike, expiry, isCall, tape);
    return price;
  }
  
  /**
   * Computes the option price in the SABR model with implied volatility and its derivatives with respect 
   * to the (8) inputs.
   * <p>
   * The signature of the method is a manual Aad one, but inside the implied volatility is computed with manual Aad
   * and the Black formula with automatic Aad.
   * @param forward The forward price.
   * @param alpha SABR Alpha parameter.
   * @param beta SABR Beta parameter.
   * @param rho SABR Rho parameter. 
   * @param nu SABR Nu parameter.
   * @param numeraire The numeraire.
   * @param strike The option strike.
   * @param expiry The option expiry.
   * @param isCall The call (true) / put (false) flag.
   * @return The price and its derivatives.
   */
  public static DoubleDerivatives price_Aad_Mixed1(
      double forward, 
      double alpha, 
      double beta, 
      double rho, 
      double nu, 
      double numeraire, 
      double strike, 
      double expiry, 
      boolean isCall) {
    DoubleDerivatives volatility = SabrVolatilityFormula.volatility_Aad(forward, alpha, beta, rho, nu, strike, expiry);
    TapeAad tape = new TapeAad();
    int iForward = tape.addEntry(new TapeEntryAad(OperationTypeAad.INPUT, forward));
    DoubleAad forwardAad = new DoubleAad(forward, iForward);
    int iVolatility = tape.addEntry(new TapeEntryAad(OperationTypeAad.INPUT, volatility.value()));
    DoubleAad volatilityAad = new DoubleAad(volatility.value(), iVolatility);
    int iNumeraire = tape.addEntry(new TapeEntryAad(OperationTypeAad.INPUT, numeraire));
    DoubleAad numeraireAad = new DoubleAad(numeraire, iNumeraire);
    int iStrike = tape.addEntry(new TapeEntryAad(OperationTypeAad.INPUT, strike));
    DoubleAad strikeAad = new DoubleAad(strike, iStrike);
    int iExpiry = tape.addEntry(new TapeEntryAad(OperationTypeAad.INPUT, expiry));
    DoubleAad expiryAad = new DoubleAad(expiry, iExpiry);
    DoubleAad priceAad = BlackFormula
        .price_Aad_Automatic(forwardAad, volatilityAad, numeraireAad, strikeAad, expiryAad, isCall, tape);
    TapeUtils.interpret(tape);
    double[] priceDerivatives = TapeUtils.extractDerivatives(tape);
    double priceBar = 1.0d;
    double volatilityBar = priceDerivatives[1];
    double[] inputBar = new double[8]; // forward, alpha, beta, rho, nu, numeraire, strike, expiry
    inputBar[7] += priceDerivatives[4] * priceBar;
    inputBar[7] += volatility.derivatives()[6] * volatilityBar;
    inputBar[6] += priceDerivatives[3] * priceBar;
    inputBar[6] += volatility.derivatives()[5] * volatilityBar;
    inputBar[5] += priceDerivatives[2] * priceBar;
    inputBar[4] += volatility.derivatives()[4] * volatilityBar;
    inputBar[3] += volatility.derivatives()[3] * volatilityBar;
    inputBar[2] += volatility.derivatives()[2] * volatilityBar;
    inputBar[1] += volatility.derivatives()[1] * volatilityBar;
    inputBar[0] += priceDerivatives[0] * priceBar;
    inputBar[0] += volatility.derivatives()[0] * volatilityBar;
    return new DoubleDerivatives(priceAad.value(), inputBar);
  }
  
  /**
   * Computes the option price in the SABR model with implied volatility and record the operations in the Tape.
   * <p>
   * The signature of the method is a automatic Aad one, but inside the implied volatility is computed with automatic Aad
   * and the Black formula with manual optimized Aad.
   * @param forward The forward price.
   * @param alpha SABR Alpha parameter.
   * @param beta SABR Beta parameter.
   * @param rho SABR Rho parameter. 
   * @param nu SABR Nu parameter.
   * @param numeraire The numeraire.
   * @param strike The option strike.
   * @param expiry The option expiry.
   * @param isCall The call (true) / put (false) flag.
   * @param tape The tape where the operations are recorded. The tape is modified by the method.
   * @return The price and its derivatives.
   */
  public static DoubleAad price_Aad_Mixed2(
      DoubleAad forward, 
      DoubleAad alpha, 
      DoubleAad beta, 
      DoubleAad rho, 
      DoubleAad nu, 
      DoubleAad numeraire, 
      DoubleAad strike, 
      DoubleAad expiry, 
      boolean isCall, 
      TapeAad tape) {
    DoubleAad volatility = SabrVolatilityFormula.volatility_Aad_Automatic(forward, alpha, beta, rho, nu, strike, expiry, tape);
    DoubleDerivatives price = BlackFormula.price_Aad_Optimized(forward.value(), volatility.value(), 
        numeraire.value(), strike.value(), expiry.value(), isCall);
    int indexForward = tape.addEntry(new TapeEntryAad(OperationTypeAad.MANUAL, 
        forward.tapeIndex(), price.value(), price.derivatives()[0]));
    int indexVolatility = tape.addEntry(new TapeEntryAad(OperationTypeAad.MANUAL, 
        volatility.tapeIndex(), indexForward, price.value(), price.derivatives()[1]));
    int indexNumeraire = tape.addEntry(new TapeEntryAad(OperationTypeAad.MANUAL, 
        numeraire.tapeIndex(), indexVolatility, price.value(), price.derivatives()[2]));
    int indexStrike = tape.addEntry(new TapeEntryAad(OperationTypeAad.MANUAL, 
        strike.tapeIndex(), indexNumeraire, price.value(), price.derivatives()[3]));
    int indexPrice = tape.addEntry(new TapeEntryAad(OperationTypeAad.MANUAL, 
        expiry.tapeIndex(), indexStrike, price.value(), price.derivatives()[4]));
    DoubleAad priceAad = new DoubleAad(price.value(), indexPrice);
    return priceAad;
  }
  
  /**
   * Computes the option price in the SABR model with implied volatility and record the operations in the Tape.
   * <p>
   * The signature of the method is a automatic Aad one, but inside the implied volatility is computed with manual Aad
   * and the Black formula with automatic Aad.
   * @param forward The forward price.
   * @param alpha SABR Alpha parameter.
   * @param beta SABR Beta parameter.
   * @param rho SABR Rho parameter. 
   * @param nu SABR Nu parameter.
   * @param numeraire The numeraire.
   * @param strike The option strike.
   * @param expiry The option expiry.
   * @param isCall The call (true) / put (false) flag.
   * @param tape The tape where the operations are recorded. The tape is modified by the method.
   * @return The price and its derivatives.
   */
  public static DoubleAad price_Aad_Mixed3(
      DoubleAad forward, 
      DoubleAad alpha, 
      DoubleAad beta, 
      DoubleAad rho, 
      DoubleAad nu, 
      DoubleAad numeraire, 
      DoubleAad strike, 
      DoubleAad expiry, 
      boolean isCall, 
      TapeAad tape) {
    DoubleDerivatives volatility = SabrVolatilityFormula.volatility_Aad(forward.value(), alpha.value(), beta.value(), 
        rho.value(), nu.value(), strike.value(), expiry.value());
    int indexForward = tape.addEntry(new TapeEntryAad(OperationTypeAad.MANUAL, 
        forward.tapeIndex(), volatility.value(), volatility.derivatives()[0]));
    int indexAlpha = tape.addEntry(new TapeEntryAad(OperationTypeAad.MANUAL, 
        alpha.tapeIndex(), indexForward, volatility.value(), volatility.derivatives()[1]));
    int indexBeta = tape.addEntry(new TapeEntryAad(OperationTypeAad.MANUAL, 
        beta.tapeIndex(), indexAlpha, volatility.value(), volatility.derivatives()[2]));
    int indexRho = tape.addEntry(new TapeEntryAad(OperationTypeAad.MANUAL, 
        rho.tapeIndex(), indexBeta, volatility.value(), volatility.derivatives()[3]));
    int indexNu = tape.addEntry(new TapeEntryAad(OperationTypeAad.MANUAL, 
        nu.tapeIndex(), indexRho, volatility.value(), volatility.derivatives()[4]));
    int indexStrike = tape.addEntry(new TapeEntryAad(OperationTypeAad.MANUAL, 
        strike.tapeIndex(), indexNu, volatility.value(), volatility.derivatives()[5]));
    int indexVolatility = tape.addEntry(new TapeEntryAad(OperationTypeAad.MANUAL, 
        expiry.tapeIndex(), indexStrike, volatility.value(), volatility.derivatives()[6]));
    DoubleAad volatilityAad = new DoubleAad(volatility.value(), indexVolatility);
    DoubleAad price = BlackFormula.price_Aad_Automatic(forward, volatilityAad, numeraire, strike, expiry, isCall, tape);
    return price;
  }

}
