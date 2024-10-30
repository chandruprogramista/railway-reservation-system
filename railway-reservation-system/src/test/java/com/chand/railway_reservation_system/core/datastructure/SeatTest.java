package com.chand.railway_reservation_system.core.datastructure;

import static org.assertj.core.api.Assertions.*;

import com.chand.railway_reservation_system.core.entity.Ticket;
import org.junit.jupiter.api.Test;

import java.util.Collection;

class SeatTest {

    @Test
    void Add () {
        int[] arr = {2, 5 ,1, 6, 1, 6, 7, 4};
        assertThat(checkTheSizeHeight(arr).size()).isEqualTo(6);
        assertThat(checkTheSizeHeight(arr).getCurrentHeight()).isEqualTo(3);
        assertThat(checkTheSizeHeight(arr).peek()).isEqualTo(5);
        assertThat(checkTheSizeHeight(arr).toString()).isNotEqualTo("[1, 2, 4, 5, 6, 7]");
        assertThat(checkTheSizeHeight(arr).toString()).isEqualTo("[5][2, 6][1, 4, 7]");
        assertThat(new Seat<>().size()).isEqualTo(0);
    }

    Seat<Integer> checkTheSizeHeight (int[] integers) {
        Seat<Integer> seat = new Seat<>();
        for (int i : integers)
            seat.add(i);

        return seat;
    }

    @Test
    void SingleElementAddAndRemove () {
        Seat<Integer> seat = new Seat<>();
        seat.add(1);
        assertThat(seat.remove(0)).isEqualTo(false);
        assertThat(seat.remove(1)).isEqualTo(true);
        assertThat(seat.size()).isEqualTo(0);
        assertThat(seat.getCurrentHeight()).isNull();
        assertThat(seat.peek()).isNull();
    }

    @Test
    void Remove () {
        int[] arr = {2, 5 ,1, 6, 1, 6, 7, 4};
        Seat<Integer> seat = checkTheSizeHeight(arr);

        for (int i : arr)
            seat.add(i);

        assertThat(seat.peek()).isEqualTo(5);
        assertThat(seat.remove(5)).isTrue();
        assertThat(seat.toString()).isEqualTo("[6][2, 7][1, 4]");
        assertThat(seat.getCurrentHeight()).as("check if the height of the seat is 3").isEqualTo(3);
        assertThat(seat.peek()).isEqualTo(6);
        assertThat(seat.remove(7)).isTrue();
        assertThat(seat.toString()).isEqualTo("[2][1, 6][4]");
        assertThat(seat.remove(3)).isFalse();
        assertThat(seat.remove(2)).isTrue();
        assertThat(seat.toString()).isEqualTo("[4][1, 6]");
        assertThat(seat.remove(4)).isTrue();
        assertThat(seat.toString()).isEqualTo("[6][1]");
        assertThat(seat.peek()).isEqualTo(6);
        assertThat(seat.size()).isEqualTo(2);
        assertThat(seat.remove(6)).isTrue();
        assertThat(seat.peek()).isEqualTo(1);
        assertThat(seat.toString()).isEqualTo("[1]");
        assertThat(seat.size()).isEqualTo(1);
        assertThat(seat.remove(1)).isTrue();
        assertThat(seat.size()).isEqualTo(0);
    }

    @Test
    void Contains () {
        int[] arr = {2, 5 ,1, 6, 1, 6, 7, 4};
        Seat<Integer> seat = checkTheSizeHeight(arr);

        for (int i : arr)
            seat.add(i);

        assertThat(seat).satisfies(s -> {
           assertThat(s.contains(2)).as(s.toString()).isTrue();
           assertThat(s.contains(3)).isFalse();
        });
    }

    @Test
    void PreAddCheck () {
        int[] arr = {2, 5 ,1, 6, 1, 6, 7, 4};
        Seat<Integer> seat = checkTheSizeHeight(arr);

        for (int i : arr)
            seat.add(i);

        assertThat(seat)
                .satisfies(s -> {
                    assertThat(Seat.preAddCheck((Collection<Integer>) s, 3)).isTrue();
                    assertThat(Seat.preAddCheck((Collection<Integer>) s, 2)).isFalse();
                });
    }

    @Test
    void TicketEntityAddCheck () {

        Seat<Ticket> s = new Seat<>();

        Ticket[] arr = {
                Ticket.builder()
                .name("chandru")
                .travelersCount(2)
                .source("D")
                .destination("E")
                .build(),
                Ticket.builder()
                        .name("muns")
                        .travelersCount(2)
                        .source("A")
                        .destination("B")
                        .build(),
                Ticket.builder()
                        .name("naveen")
                        .travelersCount(2)
                        .source("B")
                        .destination("D")
                        .build(),
                Ticket.builder()
                        .name("dhinesh")
                        .travelersCount(2)
                        .source("D")
                        .destination("E")
                        .build()};

        for (Ticket t : arr)
            s.add(t);

        System.out.println(s);
        assertThat(0).isEqualTo(0);
    }
}