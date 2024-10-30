package com.chand.railway_reservation_system.core.manager;

import com.chand.railway_reservation_system.core.entity.Passenger;
import com.chand.railway_reservation_system.core.entity.Ticket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SeatsManagerTest {

    SeatsManager seatsManager;

    @Autowired
    public SeatsManagerTest (SeatsManager seatsManager) {
        this.seatsManager = seatsManager;
    }

    @Test
    void Test () {
        Passenger passenger = new Passenger("chandru", "A", "C", 3);
        seatsManager.getSeats(passenger);

        assertThat(passenger.getTravelersCount()).isEqualTo(3);

        Ticket t = Ticket.builder().name("chandru").build();
    }
}