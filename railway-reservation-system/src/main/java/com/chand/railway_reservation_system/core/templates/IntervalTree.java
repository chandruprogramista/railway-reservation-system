package com.chand.railway_reservation_system.core.templates;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface IntervalTree<T> {

    boolean add (T element);

    boolean remove (T element);

    boolean contains (T element);

    List<T> getAll ();

    int size ();
}
