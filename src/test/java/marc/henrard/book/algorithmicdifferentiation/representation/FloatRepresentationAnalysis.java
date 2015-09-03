package marc.henrard.book.algorithmicdifferentiation.representation;

import org.testng.annotations.Test;

public class FloatRepresentationAnalysis {
  
  @Test
  public void bookExample() {
    float notional = 1_000_000f;
    float epsilon = 0.1f;
    float xMinusEpsilon = notional - epsilon;
    float xMinusEpsilonMinusX = xMinusEpsilon - notional;
    float derivative = xMinusEpsilonMinusX / epsilon;
    FloatRepresentation xRep = new FloatRepresentation(notional);
    System.out.printf("%6.4f", notional);
    System.out.println();
    System.out.println(xRep.toString());
    FloatRepresentation epsilonRep = new FloatRepresentation(epsilon);
    System.out.printf("%6.4f", epsilon);
    System.out.println();
    System.out.println(epsilonRep.toString());
    FloatRepresentation xPlusEpsilonRep = new FloatRepresentation(xMinusEpsilon);
    System.out.printf("%6.4f", xMinusEpsilon);
    System.out.println();
    System.out.println(xPlusEpsilonRep.toString());
    FloatRepresentation xPlusEpsilonMinusXRep = new FloatRepresentation(xMinusEpsilonMinusX);
    System.out.printf("%6.4f", xMinusEpsilonMinusX);
    System.out.println();
    System.out.println(xPlusEpsilonMinusXRep.toString());
    FloatRepresentation derivativeRep = new FloatRepresentation(derivative);
    System.out.printf("%6.4f", derivative);
    System.out.println();
    System.out.println(derivativeRep.toString());
    @SuppressWarnings("unused")
    int t = 0;
  }
  

  @Test
  public void multiShift() {
    float x = 1_000_000f;
    float[] epsilon = new float[] {1.0f, 0.5f, 0.2f, 0.125f, 0.1f, 0.07f, 0.0625f, 0.06f, 0.05f, 0.02f};
    int nbShifts = epsilon.length;
    float[] derivative = new float[nbShifts];
    for (int i = 0; i < nbShifts; i++) {
      System.out.printf("%2.8f%s", epsilon[i], " / ");
      derivative[i] = ((x - epsilon[i]) - x) / epsilon[i];
      System.out.printf("%2.8f%s", derivative[i], "\n");
    }
  }
  
}
