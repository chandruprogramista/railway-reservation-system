package com.chand.railway_reservation_system.web.service;

import com.chand.railway_reservation_system.core.constants.SeatStatus;
import com.chand.railway_reservation_system.core.entity.Passenger;
import com.chand.railway_reservation_system.core.util.Pair;
import com.chand.railway_reservation_system.core.validator.TicketUtils;
import com.chand.railway_reservation_system.web.repo.TicketRepo;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class PassengerService {

    // DB communication
    private final TicketRepo ticketRepo;

    // logging
    private final Logger logger = LoggerFactory.getLogger(PassengerService.class);

    @PostConstruct
    public void postConstruct() {
        this.ticketRepo
                .findAll()
                .forEach(currentPassenger -> this.bookSeat(currentPassenger, currentPassenger.getSeatsAllocation(), Optional.empty()));
    }

    @Autowired
    public PassengerService(TicketRepo ticketRepo) {
        this.ticketRepo = ticketRepo;
    }

    // DB COMMUNICATION
    public List<Passenger> getAllBooking() {
        return this.ticketRepo.findAll()
                .stream()
                .map(currentPassenger -> this.currentState(Optional.of(currentPassenger)).get())
                .collect(Collectors.toList());
    }

    public Optional<Passenger> getPassenger(String PNRId) {
        return this.getCurrentState(PNRId);
    }

    // NORMAL COMMUNICATION AND DB COMMUNICATION
    public Optional<Passenger> bookSeats(Passenger passenger) {
        // fetch the seats
        Optional<List<Integer>> seatsAllocation = TicketUtils.getSeats(passenger);
        seatsAllocation.ifPresentOrElse(seatAllocations -> {
            this.bookSeat(passenger, seatAllocations, Optional.of(UUID.randomUUID().toString()));
        }, () -> this.logger.error("VALIDATION ERROR WHILE FETCHING THE SEATS"));

        return seatsAllocation.map(e -> passenger);
    }

    private void bookSeat(Passenger passenger, List<Integer> seatsAllocation, Optional<String> pnrId) {
        TicketUtils.bookSeats(seatsAllocation, passenger);
        // if the pnr is present then it is called from the controller
        pnrId.ifPresent(pnr -> {
            passenger.setPNRId(pnr);
            passenger.setInitialTravelersCount(passenger.getTravelersCount());
            this.ticketRepo.save(passenger);
            this.logger.info("PASSENGERS DETAILS IS SUCCESSFULLY PERSISTED 'PNR : {}'", passenger.getPNRId());
        });
    }

    public Pair<Optional<Passenger>, SeatStatus> cancelSeats(String PNRId, int countToBeDeleted) {
        Pair<Optional<Passenger>, SeatStatus> pair = Pair.of(Optional.empty(), SeatStatus.EMPTY);
        pair.setFirst(this.getCurrentState(PNRId)
                .map(currentPassenger -> {
                    SeatStatus status = TicketUtils.cancelSeats(countToBeDeleted, currentPassenger);
                    pair.setSecond(status);
                    // if the validation error is not occurs
                    if (status != SeatStatus.VALIDATION_ERROR) {
                        this.dropPassengerIf(e -> e == SeatStatus.BULK_CANCEL, status, currentPassenger);
                        pair.setFirst(Optional.of(currentPassenger));
                        return currentPassenger;
                    }
                    return null;
                }));

        return pair;
    }

    public Optional<Passenger> cancelSeats(String PNRId) {
        return this.getCurrentState(PNRId)
                .map(currentPassenger -> {
                    ticketRepo.deleteById(PNRId);
                    return TicketUtils.cancelSeats(currentPassenger);
                });
    }

    /**
     * Returns empty is the PNRId is not valid orElse return the current state of an object to the caller
     *
     * @param pnrId to be searched in the db for the validation
     * @return returns the current state of the give pnr's passenger or else empty in an Optional
     */
    public Optional<Passenger> getCurrentState(String pnrId) {
        return currentState(this.ticketRepo.findById(pnrId));
    }

    private Optional<Passenger> currentState(Optional<Passenger> dbPassenger) {
        return dbPassenger.map(passenger -> {
            Passenger updatedPassenger = TicketUtils.getCurrentState(passenger);
            this.ticketRepo.save(updatedPassenger);
            return updatedPassenger;
        });
    }

    private void dropPassengerIf(Predicate<SeatStatus> predicate, SeatStatus seatStatus, Passenger passenger) {
        if (predicate.test(seatStatus))
            this.ticketRepo.deleteById(passenger.getPNRId());
        else
            this.ticketRepo.save(passenger);
    }

    public int getWaitingCount(String source, String destination) {
        return TicketUtils.getWaitingCount(source, destination);
    }
}
