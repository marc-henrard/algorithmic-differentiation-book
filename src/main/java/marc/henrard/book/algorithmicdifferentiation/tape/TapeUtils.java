/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.tape;

import java.util.ArrayList;
import java.util.List;

import cern.jet.random.Normal;
import marc.henrard.book.algorithmicdifferentiation.type.OperationTypeAad;

/**
 * Utilities functions to manipulate {@link TapeAad}.
 */
public class TapeUtils {
  
  /** The normal distribution implementation. */
  private static final Normal NORMAL = new Normal(0.0d, 1.0d, null);
  
  /**
   * Interpret a tape. 
   * <p>
   * The exact description of the interpretation can be found in the book
   * M. Henrard, Algorithmic Differentiation in Finance Explained, to appear. Section 4.2.
   * @param tape The tape.
   */
  public static void interpret(TapeAad tape) {
    int nbEntries = tape.size();
    tape.getEntry(nbEntries-1).addValueBar(1.0d);
    for(int loope = nbEntries-1; loope>=0; loope--  ) {
      TapeEntryAad entry = tape.getEntry(loope);
      switch (entry.getOperationType()) {
        case INPUT:
          break;
        case MANUAL:
          tape.getEntry(entry.getIndexArg1()).addValueBar(entry.getExtraValue() * entry.getValueBar());
          if(entry.getIndexArg2() != -1) {
            tape.getEntry(entry.getIndexArg2()).addValueBar(entry.getValueBar());
          }
          break;
        case ADDITION: // Addition of two AAD doubles.
          tape.getEntry(entry.getIndexArg1()).addValueBar(entry.getValueBar());
          tape.getEntry(entry.getIndexArg2()).addValueBar(entry.getValueBar());
          break;
        case ADDITION1: // Addition with a simple double.
          tape.getEntry(entry.getIndexArg1()).addValueBar(entry.getValueBar());
          break;
        case SUBTRACTION:
          tape.getEntry(entry.getIndexArg1()).addValueBar(entry.getValueBar());
          tape.getEntry(entry.getIndexArg2()).addValueBar(-entry.getValueBar());
          break;
        case MULTIPLICATION: // Multiplication of two AAD doubles.
          tape.getEntry(entry.getIndexArg1()).addValueBar(
              tape.getEntry(entry.getIndexArg2()).getValue() * entry.getValueBar());
          tape.getEntry(entry.getIndexArg2()).addValueBar(
              tape.getEntry(entry.getIndexArg1()).getValue() * entry.getValueBar());
          break;
        case MULTIPLICATION1: // Multiplication by a simple double.
          tape.getEntry(entry.getIndexArg1()).addValueBar(
              entry.getExtraValue() * entry.getValueBar());
          break;
        case DIVISION:
          tape.getEntry(entry.getIndexArg1()).addValueBar(
              entry.getValueBar() / tape.getEntry(entry.getIndexArg2()).getValue());
          tape.getEntry(entry.getIndexArg2()).addValueBar(
              -tape.getEntry(entry.getIndexArg1()).getValue() /
              (tape.getEntry(entry.getIndexArg2()).getValue() 
                  * tape.getEntry(entry.getIndexArg2()).getValue() ) *
              entry.getValueBar());
          break;
        case SIN:
          tape.getEntry(entry.getIndexArg1()).addValueBar(
              Math.cos(tape.getEntry(entry.getIndexArg1()).getValue()) * entry.getValueBar());
          break;
        case COS:
          tape.getEntry(entry.getIndexArg1()).addValueBar(
              - Math.sin(tape.getEntry(entry.getIndexArg1()).getValue()) * entry.getValueBar());
          break;
        case EXP:
          tape.getEntry(entry.getIndexArg1()).addValueBar(entry.getValue() * entry.getValueBar());
          break;
        case LOG:
          tape.getEntry(entry.getIndexArg1()).addValueBar(
              entry.getValueBar() / tape.getEntry(entry.getIndexArg1()).getValue());
          break;
        case SQRT:
          tape.getEntry(entry.getIndexArg1()).addValueBar(0.5 / entry.getValue() * entry.getValueBar());
          break;
        case POW:
          double x = tape.getEntry(entry.getIndexArg1()).getValue(); // x^y
          double y = tape.getEntry(entry.getIndexArg2()).getValue();
          tape.getEntry(entry.getIndexArg1()).addValueBar(y * entry.getValue() / x * entry.getValueBar());
          tape.getEntry(entry.getIndexArg2()).addValueBar(entry.getValue() * Math.log(x) * entry.getValueBar());
          break;
        case POW1:
          tape.getEntry(entry.getIndexArg1()).addValueBar(
              entry.getExtraValue() * entry.getValue() / tape.getEntry(entry.getIndexArg1()).getValue() * 
              entry.getValueBar());
          break;
        case NORMALCDF:
          tape.getEntry(entry.getIndexArg1()).addValueBar(
              NORMAL.pdf(tape.getEntry(entry.getIndexArg1()).getValue()) * entry.getValueBar());
          break;
        default:
          break;
      }
    }
  }
  
  /**
   * Extract the derivative of the final value with respect to the inputs in a {@link TapeAad}.
   * @param tape The tape.
   * @return The derivatives.
   */
  public static double[] extractDerivatives(TapeAad tape) {
    int nbEntries = tape.size();
    List<Double> derivativesList = new ArrayList<Double>();
    for(int loope = 0; loope < nbEntries; loope++) {
      TapeEntryAad entry = tape.getEntry(loope);
      if (entry.getOperationType() == OperationTypeAad.INPUT) {
        derivativesList.add(entry.getValueBar());
      }
    }
    int nbDerivatives = derivativesList.size();
    double[] derivatives = new double[nbDerivatives];
    for(int loopd=0; loopd<nbDerivatives; loopd++) {
      derivatives[loopd] = derivativesList.get(loopd);
    }
    return derivatives;
  }

}
