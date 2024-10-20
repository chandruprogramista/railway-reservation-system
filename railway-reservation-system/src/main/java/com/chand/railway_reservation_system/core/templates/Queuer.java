package com.chand.railway_reservation_system.core.templates;

import com.chand.railway_reservation_system.core.datastructure.Tree;
import com.chand.railway_reservation_system.core.entity.Passenger;

import java.util.Iterator;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;

public interface Queuer<T> {
    void add (T element);
    void remove (T element, OptionalInt waitingCount, Optional<Iterator<Passenger>> iterator);
    void checkAndAdd (Predicate<T> predicate, Tree<T> seat);
    int getWaitingCount (String source, String destination);
}
