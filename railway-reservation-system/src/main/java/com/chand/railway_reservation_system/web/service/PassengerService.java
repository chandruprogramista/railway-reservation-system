package com.chand.railway_reservation_system.web.service;

import com.chand.railway_reservation_system.core.entity.Passenger;
import com.chand.railway_reservation_system.core.manager.SeatsManager;
import com.chand.railway_reservation_system.core.validator.TicketUtils;
import com.chand.railway_reservation_system.web.repo.TicketRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PassengerService {

    // DB communication
    private TicketRepo ticketRepo;

    // logging
    private final Logger logger = LoggerFactory.getLogger(PassengerService.class);

    // DB COMMUNICATION

    // NORMAL COMMUNICATION
    public Passenger bookSeats (Passenger passenger) {
        TicketUtils.getSeats(passenger).ifPresent(
                seatsAllocation -> TicketUtils.bookSeats(seatsAllocation, passenger)
        );
        // stored in the DB
        this.ticketRepo.save(passenger);
        return passenger;
    }

    public void cancelSeats (String PNRId, int countToBeDeleted) {
        // get the passenger from DB
        Optional<Passenger> passenger = ticketRepo.findById(PNRId);
        passenger.ifPresent(currentPassenger -> {
            TicketUtils.cancelSeats(countToBeDeleted, currentPassenger);
            // safely delete from the DB
            if (currentPassenger.getTravelersCount() == 0) {
                ticketRepo.deleteById(PNRId);
            }
        });
    }

    public void cancelSeats (String PNRId) {
        Optional<Passenger> passenger = ticketRepo.findById(PNRId);
        passenger.ifPresent(TicketUtils::cancelSeats);
        // delete from the DB as-well
        ticketRepo.deleteById(PNRId);
    }

    public int getWaitingCount (String source, String destination) {
        return TicketUtils.getWaitingCount(source, destination);
    }
}
