package com.chand.railway_reservation_system.core.manager;

import com.chand.railway_reservation_system.core.entity.Passenger;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class SeatManagerTest {

    SeatManager seatManager = new SeatManager("UR", 1, 8, 8, new SingleQueue(5));

    Logger logger = LoggerFactory.getLogger(SeatManagerTest.class);

    List<Passenger> getSeatlist = List.of(
            new Passenger("1", "chandru", "C", "E", 2),
            new Passenger("2", "muns", "A", "C", 3),
            new Passenger("3", "naveen", "B", "D", 3),
            new Passenger("4", "dhinesh", "A", "D", 3),
            new Passenger("5", "abu", "D", "E", 9),
            new Passenger("6", "abubakkar", "D", "E", 8),
            new Passenger("7", "abridi", "A", "B", 4),
            new Passenger("8", "balaji", "A", "E", 5)
    );

    List<Optional<List<Integer>>> expectedResultGetSeats = List.of(
            Optional.of(List.of(1, 2)),
            Optional.of(List.of(1, 2, 3)),
            Optional.of(List.of(4, 5, 6)),
            Optional.of(List.of(7, 8)),
            Optional.empty(),
            Optional.of(List.of(3, 4, 5, 6, 7, 8)),
            Optional.of(List.of(4, 5, 6)),
            Optional.of(List.of())
    );

    int index = 0;

    @Test
    void getSeatsAndBook () {
        logger.info(" --- Get Seat And Book --- ");
        getSeatlist.forEach(passenger -> {
           Optional<List<Integer>> ls = seatManager.getSeats(passenger);
           ls.ifPresent(integers -> seatManager.bookSeats(integers, passenger));
           assertThat(ls).isEqualTo(expectedResultGetSeats.get(index++));
        });
    }

    @Test
    void cancelSeats () {
        logger.info(" --- Cancel Seat --- ");
//        this.basicCancelTest();
        this.getSeatsAndBook();
        // cancel the waiting only
//        this.seatManager.cancelSeats(2, this.getSeatlist.get(5), true);
//        assertThat(this.seatManager.getWaitingCount(this.getSeatlist.get(5).getSource(), this.getSeatlist.get(5).getDestination()))
//                .isEqualTo(0);

        // goes into internal
        this.seatManager.cancelSeats(2, this.getSeatlist.get(0), true);
        assertThat(this.getSeatlist.get(5).getWaitingCount()).isEqualTo(0);
    }

    void basicCancelTest () {
        this.seatManager.cancelSeats(2, this.getSeatlist.getFirst(), true);
    }

    @Test
    void getWaitingCount() {
        this.getSeatsAndBook();
        logger.info(" --- Get Waiting Count --- ");
        List<String[]> getWaits = List.of(
                new String[] {"A", "E"},
                new String[] {"D", "E"},
                new String[] {"A", "C"},
                new String[] {"A", "B"}
        );

        int expected[] = {5, 2, 0, 1};

        this.index = 0;

        getWaits.forEach(string -> {
            assertThat(this.seatManager.getWaitingCount(string[0], string[1])).isEqualTo(expected[index++]);
        });
    }
}