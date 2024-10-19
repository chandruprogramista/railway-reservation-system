package com.chand.railway_reservation_system.core.templates;

import com.chand.railway_reservation_system.core.datastructure.Tree;

import java.util.function.Predicate;

public interface Queuer<T> {
    void add (T element);
    void remove (T element, boolean partial);
    void checkAndAdd (Predicate<T> predicate, Tree<T> seat);
}
