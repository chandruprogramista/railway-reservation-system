package com.chand.railway_reservation_system.core.util;

public class Pair <S, T> {

    private S first;
    private T second;

    public Pair (S first, T second) {
        this.first = first;
        this.second = second;
    }

    public static <S, T> Pair<S, T> of (S first, T second) {
        return new Pair<>(first, second);
    }

    public S getFirst() {
        return first;
    }

    public void setFirst(S first) {
        this.first = first;
    }

    public T getSecond() {
        return second;
    }

    public void setSecond(T second) {
        this.second = second;
    }
}
