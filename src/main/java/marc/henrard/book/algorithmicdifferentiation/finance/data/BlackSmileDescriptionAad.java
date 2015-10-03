/**
 * Copyright (C) 2015 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.finance.data;

import marc.henrard.book.algorithmicdifferentiation.tape.TapeAad;
import marc.henrard.book.algorithmicdifferentiation.type.DoubleAad;

/**
 * Description of the smile related to an implied Black volatility.
 */
public interface BlackSmileDescriptionAad {
  
  /**
   * Returns the implied Black volatility.
   * @param strike The strike.
   * @param forward The forward.
   * @param tape The AAD tape.
   * @return The volatility.
   */
  DoubleAad volatility(DoubleAad strike, DoubleAad forward, TapeAad tape);

}
