package marc.henrard.book.algorithmicdifferentiation.representation;

public class FloatRepresentation {
  
  /* The representation sign. 1 bit */
  private final boolean sign;
  /* The representation exponent. 8 bits */
  private final boolean[] exponent;
  /* The representation fraction or mantissa. 23 bits*/
  private final boolean[] fraction;
  /* String representation. */
  private final String toString;

  private static final int EXPONENT_LENGTH = 8;
  private static final int FRACTION_LENGTH = 23;
  
  public FloatRepresentation(boolean sign, boolean[] exponent, boolean[] fraction) {
    this.sign = sign;
    this.exponent = exponent;
    this.fraction = fraction;
    toString = toStr();
  }
  
  public FloatRepresentation(float x) {
    sign = (x < 0);
    int xInteger = Float.floatToRawIntBits(Math.abs(x));
    String xString1 = Long.toBinaryString(xInteger);
    int xStrLength = xString1.length();
    String xString2 = "";
    for (int i = 0; i < 31 - xStrLength; i++) {
      xString2 = xString2 + "0";
    }
    xString2 = xString2 + xString1;
    exponent = new boolean[EXPONENT_LENGTH];
    fraction = new boolean[FRACTION_LENGTH];
    for (int i = 0; i < EXPONENT_LENGTH; i++) {
      exponent[i] = (xString2.charAt(i) == '1');
    }
    for (int i = 0; i < FRACTION_LENGTH; i++) {
      fraction[i] = (xString2.charAt(EXPONENT_LENGTH + i) == '1');
    }
    toString = toStr();
  }

  public boolean isSign() {
    return sign;
  }

  public boolean[] getExponent() {
    return exponent;
  }

  public boolean[] getFraction() {
    return fraction;
  }
  
  @Override
  public String toString() {
    return toString;
  }
  
  public float toDouble() {
    float f = 1.0f;
    for (int i = 0; i < FRACTION_LENGTH; i++) { // fraction
      f += twoexp(-(i+1));
    }
    return f;
  }
  
  private String toStr() {
    String tmpString = sign ? "1" : "0";
    tmpString = tmpString + "-";
    for (int i = 0; i < EXPONENT_LENGTH; i++) {
      tmpString = tmpString + (exponent[i] ? "1" : "0");
    }
    tmpString = tmpString + "-";
    for (int i = 0; i < FRACTION_LENGTH; i++) {
      tmpString = tmpString + (fraction[i] ? "1" : "0");
    }
    return tmpString;
  }
  
  float twoexp(int n) {
    return (float) Math.pow(2, n);
  }

  
}
