/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.finance;

import marc.henrard.book.algorithmicdifferentiation.tape.TapeAad;
import marc.henrard.book.algorithmicdifferentiation.tape.TapeEntryAad;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleAad;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleDerivatives;
import marc.henrard.book.algorithmicdifferentiation.type.OperationTypeAad;

/**
 * Implementation of the SABR implied volatility approximation formula for option pricing.
 * <p>
 * Implementation of the volatility formula and different algorithmic differentiation versions.
 * <p>
 * Reference: Hagan, P., Kumar, D., Lesniewski, A. and Woodward, D. (2002). Managing Smile Risk. Wilmott Magazine, September: 84-108
 */
public class SabrVolatilityFormula {
  
  /* Range around 0 for which z/x(z) is computed with a first order approximation. */
  private static final double Z_RANGE = 1.0E-6;
  
  /**
   * Approximated implied Black volatility for the SABR model.
   * @param forward The forward price.
   * @param alpha SABR Alpha parameter.
   * @param beta SABR Beta parameter.
   * @param rho SABR Rho parameter. 
   * @param nu SABR Nu parameter.
   * @param strike The option strike.
   * @param expiry The option expiry.
   * @return The volatility.
   */
  public static double volatility(
      double forward, 
      double alpha, 
      double beta, 
      double rho, 
      double nu, 
      double strike, 
      double expiry) {
    double beta1 = 1.0d - beta;
    double fKbeta = Math.pow(forward * strike, 0.5 * beta1);
    double logfK = Math.log(forward / strike);
    double z = nu / alpha * fKbeta * logfK;
    double zxz;
    double xz = 0.0d;
    double sqz = 0.0d;
    if(Math.abs(z) < Z_RANGE) { // z close to 0, first order approximation for z/x(z)
      zxz = 1.0d - 0.5 * z * rho;
    } else {
      sqz = Math.sqrt(1.0d - 2.0d * rho * z + z * z);
      xz = Math.log((sqz + z - rho) / ( 1.0d - rho));
      zxz = z / xz;
    }
    double beta24 = beta1 * beta1 / 24.0d;
    double beta1920 = beta1 * beta1 * beta1 * beta1 / 1920.0d;
    double logfK2 = logfK *logfK;
    double factor11 = beta24 * logfK2;
    double factor12 = beta1920 * logfK2 * logfK2;
    double num1 = (1 + factor11 + factor12);
    double factor1 = alpha / (fKbeta * num1);
    double factor31 = beta24 * alpha * alpha / (fKbeta * fKbeta);
    double factor32 = 0.25d * rho * beta * nu * alpha / fKbeta;
    double factor33 = (2.0d - 3.0d * rho * rho) / 24.0d * nu * nu;
    double factor3 = 1 + (factor31 + factor32 + factor33) * expiry;
    return factor1 * zxz * factor3;
  }
  
  /**
   * Approximated implied Black volatility for the SABR model and the volatility derivatives with respect to the 
   * different inputs. Manual coding of the AD.
   * @param forward The forward price.
   * @param alpha SABR Alpha parameter.
   * @param beta SABR Beta parameter.
   * @param rho SABR Rho parameter. 
   * @param nu SABR Nu parameter.
   * @param strike The option strike.
   * @param expiry The option expiry.
   * @return The volatility and its derivatives.
   */
  public static DoubleDerivatives volatility_Aad(
      double forward, 
      double alpha, 
      double beta, 
      double rho, 
      double nu, 
      double strike, 
      double expiry) {
    // Forward sweep - function
    double beta1 = 1.0d - beta;
    double fKbeta = Math.pow(forward * strike, 0.5 * beta1);
    double logfK = Math.log(forward / strike);
    double z = nu / alpha * fKbeta * logfK;
    double zxz;
    double xz = 0.0d;
    double sqz = 0.0d;
    if(Math.abs(z) < Z_RANGE) { // z close to 0, first order approximation for x/x(z)
      zxz = 1.0d - 0.5 * z * rho;
    } else {
      sqz = Math.sqrt(1.0d - 2.0d * rho * z + z * z);
      xz = Math.log((sqz + z - rho) / ( 1.0d - rho));
      zxz = z / xz;
    }
    double beta24 = beta1 * beta1 / 24.0d;
    double beta1920 = beta1 * beta1 * beta1 * beta1 / 1920.0d;
    double logfK2 = logfK *logfK;
    double factor11 = beta24 * logfK2;
    double factor12 = beta1920 * logfK2 * logfK2;
    double num1 = (1 + factor11 + factor12);
    double factor1 = alpha / (fKbeta * num1);
    double factor31 = beta24 * alpha * alpha / (fKbeta * fKbeta);
    double factor32 = 0.25d * rho * beta * nu * alpha / fKbeta;
    double factor33 = (2.0d - 3.0d * rho * rho) / 24.0d * nu * nu;
    double factor3 = 1 + (factor31 + factor32 + factor33) * expiry;
    double volatility = factor1 * zxz * factor3;
    // Backward sweep - derivatives
    double volatilityBar = 1.0d;
    double factor3Bar = factor1 * zxz * volatilityBar;
    double factor33Bar = expiry * factor3Bar;
    double factor32Bar = expiry * factor3Bar;
    double factor31Bar = expiry * factor3Bar;
    double factor1Bar = zxz * factor3 * volatilityBar;
    double num1Bar = -alpha / (fKbeta * num1 * num1) * factor1Bar;
    double factor12Bar = num1Bar;
    double factor11Bar = num1Bar;
    double logfK2Bar = beta24 * factor11Bar;
    logfK2Bar += 2.0 * beta1920 * logfK2 * factor12Bar;
    double beta1920Bar = logfK2 * logfK2 * factor12Bar;
    double beta24Bar = logfK2 * factor11Bar;
    beta24Bar += alpha * alpha / (fKbeta * fKbeta) * factor31Bar;
    double zxzBar = factor1 * factor3 * volatilityBar;
    double zBar;
    double xzBar = 0.0d;
    double sqzBar = 0.0d;
    if(Math.abs(z) < Z_RANGE) { // z close to 0, first order approximation for x/x(z)
      zBar = 0.5 * rho * zxzBar;
    } else {
      xzBar = -z / (xz * xz) * zxzBar;
      sqzBar = xzBar / (sqz + z - rho);
      zBar = zxzBar / xz;
      zBar += xzBar / (sqz + z - rho);
      zBar += (-rho + z) / sqz * sqzBar;
    }
    double logfKBar = nu / alpha * fKbeta * zBar;
    logfKBar += 2.0d * logfK * logfK2Bar;
    double fKbetaBar = nu / alpha * logfK * zBar;
    fKbetaBar += - alpha / (fKbeta * fKbeta * num1) * factor1Bar;
    fKbetaBar += -2.0d * beta24 * alpha * alpha / (fKbeta * fKbeta * fKbeta) * factor31Bar;
    fKbetaBar += -0.25d * rho * beta * nu * alpha / (fKbeta * fKbeta) * factor32Bar;
    double beta1Bar = fKbeta * 0.5 * Math.log(forward * strike) * fKbetaBar;
    beta1Bar += beta1 / 12.0d * beta24Bar;
    beta1Bar += beta1 * beta1 * beta1 / 480.0d * beta1920Bar;
    double[] inputBar = new double[7]; // forward, alpha, beta, rho, nu, strike, expiry
    inputBar[0] += logfKBar / forward;
    inputBar[0] += 0.5 * beta1 * fKbeta / forward * fKbetaBar;
    inputBar[1] += -nu / (alpha * alpha) * fKbeta * logfK * zBar;
    inputBar[1] += factor1Bar / (fKbeta * num1);
    inputBar[1] += 2.0d * beta24 * alpha / (fKbeta * fKbeta) * factor31Bar;
    inputBar[1] += 0.25d * rho * beta * nu / fKbeta * factor32Bar;
    inputBar[2] += -beta1Bar;
    inputBar[2] += 0.25d * rho * nu * alpha / fKbeta * factor32Bar;
    if (Math.abs(z) < Z_RANGE) { // z close to 0, first order approximation for x/x(z)
      inputBar[3] += -0.5 * z * zxzBar;
    } else {
      inputBar[3] += -z / sqz * sqzBar;
      inputBar[3] += (-1.0d / (sqz + z - rho) + 1.0d / (1.0d - rho)) * xzBar;
    }
    inputBar[3] += 0.25d * beta * nu * alpha / fKbeta * factor32Bar;
    inputBar[3] += - 0.25d * rho * nu * nu * factor33Bar;
    inputBar[4] += fKbeta / alpha * logfK * zBar;
    inputBar[4] += 0.25d * rho * beta * alpha / fKbeta * factor32Bar;
    inputBar[4] +=  (2.0d - 3.0d * rho * rho) / 12.0d * nu * factor33Bar;
    inputBar[5] += 0.5 * beta1 * fKbeta / strike * fKbetaBar;
    inputBar[5] += -logfKBar / strike;
    inputBar[6] += (factor31 + factor32 + factor33) * factor3Bar;
    return new DoubleDerivatives(volatility, inputBar);
  }

  /**
   * Approximated implied Black volatility for the SABR model and the volatility derivatives in the automatic
   * adjoint algorithmic differentiation by "operator overloading".
   * @param forward The forward price.
   * @param alpha SABR Alpha parameter.
   * @param beta SABR Beta parameter.
   * @param rho SABR Rho parameter. 
   * @param nu SABR Nu parameter.
   * @param strike The option strike.
   * @param expiry The option expiry.
   * @param tape The tape where the operations are recorded. The tape is modified by the method.
   * @return The volatility and its derivatives.
   */
  public static DoubleAad volatility_Aad_Automatic(
      DoubleAad forward, 
      DoubleAad alpha, 
      DoubleAad beta, 
      DoubleAad rho, 
      DoubleAad nu, 
      DoubleAad strike, 
      DoubleAad expiry, 
      TapeAad tape) {
    DoubleAad beta1 = beta.multipliedBy(-1.0d, tape).plus(1.0d, tape);
    DoubleAad fKbeta =  forward.multipliedBy(strike, tape).pow(beta1.multipliedBy(0.5d, tape), tape);
    DoubleAad logfK = forward.dividedBy(strike, tape).log(tape);
    DoubleAad z = nu.dividedBy(alpha, tape).multipliedBy(fKbeta, tape).multipliedBy(logfK, tape);
    DoubleAad zxz;
    if(Math.abs(z.value()) < Z_RANGE) { // z close to 0, first order approximation for x/x(z)
      zxz =  z.multipliedBy(rho, tape).multipliedBy(-0.5d, tape).plus(1.0d, tape);
    } else {
      DoubleAad sqz =  rho.multipliedBy(z, tape).multipliedBy(-2.0d, tape).plus(1.0d, tape)
          .plus(z.multipliedBy(z, tape), tape).sqrt(tape);
      DoubleAad xz =  sqz.plus(z, tape).minus(rho, tape).dividedBy(rho.multipliedBy(-1.0d, tape)
          .plus(1.0d, tape), tape).log(tape);
      zxz = z.dividedBy(xz, tape);
    }
    DoubleAad beta24 = beta1.multipliedBy(beta1, tape).multipliedBy(1.0d/24.0d, tape);
    DoubleAad beta1920 = beta1.multipliedBy(beta1, tape).multipliedBy(beta1, tape).multipliedBy(beta1, tape)
        .multipliedBy(1.0d/1920d, tape);
    DoubleAad logfK2 = logfK.multipliedBy(logfK, tape);
    DoubleAad factor11 = beta24.multipliedBy(logfK2, tape);
    DoubleAad factor12 = beta1920.multipliedBy(logfK2, tape).multipliedBy(logfK2, tape);
    DoubleAad num1 =  factor11.plus(factor12, tape).plus(1.0d, tape);
    DoubleAad factor1 = alpha.dividedBy(fKbeta.multipliedBy(num1, tape), tape);
    DoubleAad factor31 = beta24.multipliedBy(alpha, tape).multipliedBy(alpha, tape)
        .dividedBy(fKbeta.multipliedBy(fKbeta, tape), tape);
    DoubleAad factor32 =  rho.multipliedBy(0.25d, tape).multipliedBy(beta, tape).multipliedBy(nu, tape)
        .multipliedBy(alpha, tape).dividedBy(fKbeta, tape);
    DoubleAad factor33 =  rho.multipliedBy(rho, tape).multipliedBy(-3.0d, tape).plus(2.0d, tape)
        .multipliedBy(1.0d/24.0d, tape).multipliedBy(nu, tape).multipliedBy(nu, tape);
    DoubleAad factor3 = factor31.plus(factor32, tape).plus(factor33, tape).multipliedBy(expiry, tape).plus(1.0d, tape);
    return factor1.multipliedBy(zxz, tape).multipliedBy(factor3, tape);
  }


  /**
   * Approximated implied Black volatility for the SABR model and the volatility derivatives.
   * The code is written manually but the object returned is the one used in Automatic AAD. 
   * This code demonstrate that it is possible to combine manual and automatic AD.
   * @param forward The forward price.
   * @param alpha SABR Alpha parameter.
   * @param beta SABR Beta parameter.
   * @param rho SABR Rho parameter. 
   * @param nu SABR Nu parameter.
   * @param strike The option strike.
   * @param expiry The option expiry.
   * @param tape The tape where the operations are recorded. The tape is modified by the method.
   * @return The volatility and its derivatives.
   */
  public static DoubleAad volatility_Aad_Automatic2(
      DoubleAad forwardAad, 
      DoubleAad alphaAad, 
      DoubleAad betaAad, 
      DoubleAad rhoAad, 
      DoubleAad nuAad, 
      DoubleAad strikeAad, 
      DoubleAad expiryAad, 
      TapeAad tape) {
    double forward = forwardAad.value();
    double alpha = alphaAad.value();
    double beta = betaAad.value();
    double rho = rhoAad.value();
    double nu = nuAad.value();
    double strike = strikeAad.value();
    double expiry = expiryAad.value();
    // Forward sweep - function
    double beta1 = 1.0d - beta;
    double fKbeta = Math.pow(forward * strike, 0.5 * beta1);
    double logfK = Math.log(forward / strike);
    double z = nu / alpha * fKbeta * logfK;
    double zxz;
    double xz = 0.0d;
    double sqz = 0.0d;
    if(Math.abs(z) < Z_RANGE) { // z close to 0, first order approximation for x/x(z)
      zxz = 1.0d - 0.5 * z * rho;
    } else {
      sqz = Math.sqrt(1.0d - 2.0d * rho * z + z * z);
      xz = Math.log((sqz + z - rho) / ( 1.0d - rho));
      zxz = z / xz;
    }
    double beta24 = beta1 * beta1 / 24.0d;
    double beta1920 = beta1 * beta1 * beta1 * beta1 / 1920.0d;
    double logfK2 = logfK *logfK;
    double factor11 = beta24 * logfK2;
    double factor12 = beta1920 * logfK2 * logfK2;
    double num1 = (1 + factor11 + factor12);
    double factor1 = alpha / (fKbeta * num1);
    double factor31 = beta24 * alpha * alpha / (fKbeta * fKbeta);
    double factor32 = 0.25d * rho * beta * nu * alpha / fKbeta;
    double factor33 = (2.0d - 3.0d * rho * rho) / 24.0d * nu * nu;
    double factor3 = 1 + (factor31 + factor32 + factor33) * expiry;
    double volatility = factor1 * zxz * factor3;
    // Backward sweep - derivatives
    double volatilityBar = 1.0d;
    double factor3Bar = factor1 * zxz * volatilityBar;
    double factor33Bar = expiry * factor3Bar;
    double factor32Bar = expiry * factor3Bar;
    double factor31Bar = expiry * factor3Bar;
    double factor1Bar = zxz * factor3 * volatilityBar;
    double num1Bar = -alpha / (fKbeta * num1 * num1) * factor1Bar;
    double factor12Bar = num1Bar;
    double factor11Bar = num1Bar;
    double logfK2Bar = beta24 * factor11Bar;
    logfK2Bar += 2.0 * beta1920 * logfK2 * factor12Bar;
    double beta1920Bar = logfK2 * logfK2 * factor12Bar;
    double beta24Bar = logfK2 * factor11Bar;
    beta24Bar += alpha * alpha / (fKbeta * fKbeta) * factor31Bar;
    double zxzBar = factor1 * factor3 * volatilityBar;
    double zBar;
    double xzBar = 0.0d;
    double sqzBar = 0.0d;
    if(Math.abs(z) < Z_RANGE) { // z close to 0, first order approximation for x/x(z)
      zBar = 0.5 * rho * zxzBar;
    } else {
      xzBar = -z / (xz * xz) * zxzBar;
      sqzBar = xzBar / (sqz + z - rho);
      zBar = zxzBar / xz;
      zBar += xzBar / (sqz + z - rho);
      zBar += (-rho + z) / sqz * sqzBar;
    }
    double logfKBar = nu / alpha * fKbeta * zBar;
    logfKBar += 2.0d * logfK * logfK2Bar;
    double fKbetaBar = nu / alpha * logfK * zBar;
    fKbetaBar += - alpha / (fKbeta * fKbeta * num1) * factor1Bar;
    fKbetaBar += -2.0d * beta24 * alpha * alpha / (fKbeta * fKbeta * fKbeta) * factor31Bar;
    fKbetaBar += -0.25d * rho * beta * nu * alpha / (fKbeta * fKbeta) * factor32Bar;
    double beta1Bar = fKbeta * 0.5 * Math.log(forward * strike) * fKbetaBar;
    beta1Bar += beta1 / 12.0d * beta24Bar;
    beta1Bar += beta1 * beta1 * beta1 / 480.0d * beta1920Bar;
    double[] inputBar = new double[7]; // forward, alpha, beta, rho, nu, strike, expiry
    inputBar[0] += logfKBar / forward;
    inputBar[0] += 0.5 * beta1 * fKbeta / forward * fKbetaBar;
    inputBar[1] += -nu / (alpha * alpha) * fKbeta * logfK * zBar;
    inputBar[1] += factor1Bar / (fKbeta * num1);
    inputBar[1] += 2.0d * beta24 * alpha / (fKbeta * fKbeta) * factor31Bar;
    inputBar[1] += 0.25d * rho * beta * nu / fKbeta * factor32Bar;
    inputBar[2] += -beta1Bar;
    inputBar[2] += 0.25d * rho * nu * alpha / fKbeta * factor32Bar;
    if (Math.abs(z) < Z_RANGE) { // z close to 0, first order approximation for x/x(z)
      inputBar[3] += -0.5 * z * zxzBar;
    } else {
      inputBar[3] += -z / sqz * sqzBar;
      inputBar[3] += (-1.0d / (sqz + z - rho) + 1.0d / (1.0d - rho)) * xzBar;
    }
    inputBar[3] += 0.25d * beta * nu * alpha / fKbeta * factor32Bar;
    inputBar[3] += - 0.25d * rho * nu * nu * factor33Bar;
    inputBar[4] += fKbeta / alpha * logfK * zBar;
    inputBar[4] += 0.25d * rho * beta * alpha / fKbeta * factor32Bar;
    inputBar[4] +=  (2.0d - 3.0d * rho * rho) / 12.0d * nu * factor33Bar;
    inputBar[5] += 0.5 * beta1 * fKbeta / strike * fKbetaBar;
    inputBar[5] += -logfKBar / strike;
    inputBar[6] += (factor31 + factor32 + factor33) * factor3Bar;
    int indexForward = tape.addEntry(new TapeEntryAad(OperationTypeAad.MANUAL, 
        forwardAad.tapeIndex(), volatility, inputBar[0]));
    int indexAlpha = tape.addEntry(new TapeEntryAad(OperationTypeAad.MANUAL, 
        alphaAad.tapeIndex(), indexForward, volatility, inputBar[1]));
    int indexBeta = tape.addEntry(new TapeEntryAad(OperationTypeAad.MANUAL, 
        betaAad.tapeIndex(), indexAlpha, volatility, inputBar[2]));
    int indexRho = tape.addEntry(new TapeEntryAad(OperationTypeAad.MANUAL, 
        rhoAad.tapeIndex(), indexBeta, volatility, inputBar[3]));
    int indexNu = tape.addEntry(new TapeEntryAad(OperationTypeAad.MANUAL, 
        nuAad.tapeIndex(), indexRho, volatility, inputBar[4]));
    int indexStrike = tape.addEntry(new TapeEntryAad(OperationTypeAad.MANUAL, 
        strikeAad.tapeIndex(), indexNu, volatility, inputBar[5]));
    int indexVolatility = tape.addEntry(new TapeEntryAad(OperationTypeAad.MANUAL, 
        expiryAad.tapeIndex(), indexStrike, volatility, inputBar[6]));
    DoubleAad volatilityAad = new DoubleAad(volatility, indexVolatility);
    return volatilityAad;
  }

}
