/**
 * Copyright (C) 2014 - present by Marc Henrard
 */
package marc.henrard.book.algorithmicdifferentiation.tape;

import java.util.ArrayList;
import java.util.List;

/**
 * Class describing a tape for Adjoint Algorithmic Differentiation.
 */
public class TapeAad {
  
  /** The initial capacity of the list holding the tape. */
  private final static int INITIAL_CAPACITY = 250;
  /** The list holding each entry in the tape.*/
  private final List<TapeEntryAad> tapeList;
  /** The current size of the tape. */
  private int size;
  
  /**
   * Construct an empty list with a default initial capacity.
   */
  public TapeAad() {
    this.tapeList = new ArrayList<TapeEntryAad>(INITIAL_CAPACITY);
    size = 0;
  }
  
  /**
   * Returns the current size of the tape.
   * @return The size.
   */
  public int size() {
    return size;
  }
  
  /**
   * Retrieve an entry in the tape by its index.
   * @param index The entry index.
   * @return The entry.
   */
  public TapeEntryAad getEntry(int index) {
    return tapeList.get(index);
  }

  /**
   * Add an entry in the tape and returns the index of the new entry.
   * @param entry The entry to add on the tape.
   * @return The index of the new entry.
   */
  public int addEntry(TapeEntryAad entry) {
    tapeList.add(entry);
    size++;
    return size - 1;
  }

}
