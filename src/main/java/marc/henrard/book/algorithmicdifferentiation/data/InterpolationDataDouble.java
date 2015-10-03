/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.data;

/** 
 * Data used in interpolation. Node values represented by an array of doubles.
 */
public class InterpolationDataDouble {
  
  /* The different nodes or vertices. */
  private final double[] nodes;
  /* The values at the different nodes. */
  private final double[] nodesValue;
  
  /**
   * Constructor of an interpolation data set.
   * @param nodes The nodes. In increasing order.
   * @param nodesValue The values at the nodes. In the same order as the nodes.
   */
  public InterpolationDataDouble(double[] nodes, double[] nodesValue) {
    this.nodes = nodes;
    this.nodesValue = nodesValue;
  }

  /**
   * Returns the nodes.
   * @return The nodes.
   */
  public double[] nodes() {
    return nodes;
  }

  /**
   * Returns the values at the different nodes.
   * @return The values.
   */
  public double[] nodesValue() {
    return nodesValue;
  }

}
