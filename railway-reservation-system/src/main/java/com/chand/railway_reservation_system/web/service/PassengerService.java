package com.chand.railway_reservation_system.web.service;

import com.chand.railway_reservation_system.core.entity.Passenger;
import com.chand.railway_reservation_system.core.validator.TicketUtils;
import com.chand.railway_reservation_system.web.repo.TicketRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class PassengerService {

    // DB communication
    private final TicketRepo ticketRepo;

    // logging
    private final Logger logger = LoggerFactory.getLogger(PassengerService.class);

    @Autowired
    public PassengerService (TicketRepo ticketRepo) {
        this.ticketRepo = ticketRepo;
    }

    // DB COMMUNICATION
    public List<Passenger> getAllBooking () {
        return this.ticketRepo.findAll();
    }

    public Optional<Passenger> getPassenger (String PNRId) {
        return this.ticketRepo.findById(PNRId);
    }

    // NORMAL COMMUNICATION AND DB COMMUNICATION
    public Optional<Passenger> bookSeats(Passenger passenger) {
        passenger.buildSourceAndDestination();
        Optional<List<Integer>> seatsAllocation = TicketUtils.getSeats(passenger);
        seatsAllocation.ifPresent(currentSeatAllocation -> {
            // set the PNR id
            passenger.setPNRId(UUID.randomUUID().toString());
            TicketUtils.bookSeats(currentSeatAllocation, passenger);
            // stored in the DB
            this.ticketRepo.save(passenger);
            this.logger.info("PASSENGERS DETAILS IS SUCCESSFULLY PERSISTED");
        });

        return seatsAllocation.isPresent() ? Optional.of(passenger) : Optional.empty();
    }

    public Optional<Passenger> cancelSeats(String PNRId, int countToBeDeleted) {
        // get the passenger from DB
        Optional<Passenger> passenger = ticketRepo.findById(PNRId);
        // only I used for the element is accessible inside the lambda
        AtomicBoolean flag = new AtomicBoolean(true);
        passenger.ifPresent(currentPassenger -> {
            flag.set(TicketUtils.cancelSeats(countToBeDeleted, currentPassenger));
            // safely delete from the DB
            if (flag.get() && currentPassenger.getTravelersCount() == 0) {
                ticketRepo.deleteById(PNRId);
            }
        });

        return flag.get() ? passenger : Optional.empty();
    }

    public Optional<Passenger> cancelSeats(String PNRId) {
        Optional<Passenger> passenger = ticketRepo.findById(PNRId);
        passenger.ifPresent(currentPassenger -> {
            TicketUtils.cancelSeats(currentPassenger);
            // delete from the DB as-well
            ticketRepo.deleteById(PNRId);
        });

        return passenger;
    }

    public int getWaitingCount(String source, String destination) {
        return TicketUtils.getWaitingCount(source, destination);
    }
}
