package com.chand.railway_reservation_system.core.manager;

import com.chand.railway_reservation_system.core.constants.SeatStatus;
import com.chand.railway_reservation_system.core.datastructure.Seat;
import com.chand.railway_reservation_system.core.entity.Passenger;
import com.chand.railway_reservation_system.core.templates.Queuer;
import com.chand.railway_reservation_system.core.validator.Validator;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SeatsManager {

    private String coachName;

    // inclusive
    private final int seatStarts;

    // exclusive
    private final int seatsEnds;

    // actual seats
    private Seat<Passenger>[] seats;

    private final Queuer<Passenger> waitingQueue;

    // logger
    private final Logger logger = LoggerFactory.getLogger(SeatsManager.class);

    @Autowired
    public SeatsManager(@Value("${train.coach-name}") String coachName,
                        @Value("${train.seats.start}") int start,
                        @Value("${train.seats.end}") int end,
                        @Qualifier("singleQueue") Queuer<Passenger> queue) {
        this.coachName = coachName;
        this.seatStarts = start;
        this.seatsEnds = end;
        this.waitingQueue = queue;
    }

    @PostConstruct
    private void init() {
        this.seats = new Seat[this.seatsEnds - this.seatStarts + 1];
        Arrays.setAll(this.seats, index -> new Seat<>(this.seatStarts + index));
    }

    public List<Integer> getSeats(Passenger passenger) {

        List<Integer> seatsAllocation = new ArrayList<>();
        int requireSeats = passenger.getTravelersCount();

        for (int i = 0; i < this.seats.length && requireSeats > 0; i++) {
            if (Seat.preAddCheck(this.seats[i], passenger)) {
                seatsAllocation.add(this.seats[i].getId());
                --requireSeats;
            }
        }

        if (requireSeats > 0)
            passenger.setWaitingCount(requireSeats);

        this.logger.info("SEATS ARE ALLOCATED FOR PNR : '{}', SEATS NUMBERS ARE : '{}', AND WAITING COUNT : {}", passenger.getPNRId(), seatsAllocation, passenger.getWaitingCount());
        return seatsAllocation;
    }

    public void bookSeats(List<Integer> seatsAllocation, Passenger passenger) {
        seatsAllocation.forEach(seatNumber -> this.seats[currentSeat(seatNumber)].add(passenger));
        passenger.setSeatsAllocation(seatsAllocation);
//        this.logger.info("SEATS ARE BOOKED SUCCESSFULLY FOR : '{}'", passenger.getPNRId());
        if (passenger.getWaitingCount() > 0) {
            this.waitingQueue.add(passenger);
            this.logger.info("WAITING COUNT IS : '{}'", passenger.getWaitingCount());
        }
    }

    private int currentSeat(int seatNumber) {
        return seatNumber - this.seatStarts;
    }

    public SeatStatus cancelSeats(int countToBeDeleted, Passenger passenger, boolean waitingDeletionFirst) {

        // delete the all passengers that are traveled in the PNR -> so the object is not used any at all
        if (countToBeDeleted == passenger.getTravelersCount()) return cancelSeats(passenger);

        int cancelCount = countToBeDeleted;

        if (waitingDeletionFirst) {
            int waitingCount = passenger.getWaitingCount();
            countToBeDeleted -= waitingCount;
            if (countToBeDeleted >= 0) {
                if (countToBeDeleted != cancelCount) {
                    this.waitingQueue.remove(passenger, OptionalInt.of(passenger.getWaitingCount()), Optional.empty());
                    passenger.setTravelersCount(passenger.getTravelersCount() - passenger.getWaitingCount());
                }
                this.cancelHelper(passenger, countToBeDeleted);
            } else {
//                this.waitingQueue.remove(passenger, OptionalInt.of(cancelCount), Optional.empty());
                passenger.setTravelersCount(passenger.getTravelersCount() - cancelCount);
            }
            passenger.setWaitingCount(countToBeDeleted >= 0 ? 0 : -countToBeDeleted);
        } else {
            // pass
        }

        return SeatStatus.CANCEL;
    }

    public SeatStatus cancelSeats(Passenger passenger) {
        if (passenger.getWaitingCount() > 0) {
            this.waitingQueue.remove(passenger, OptionalInt.of(passenger.getWaitingCount()), Optional.empty());
            passenger.setTravelersCount(passenger.getTravelersCount() - passenger.getWaitingCount());
            passenger.setWaitingCount(0);
        }
        this.cancelHelper(passenger, passenger.getSeatsAllocation().size());
        return SeatStatus.BULK_CANCEL;
    }

    private void cancelHelper(Passenger passenger, int countToBeDeleted) {
        if (countToBeDeleted > 0) {
            List<Integer> seatsAllocation = passenger.getSeatsAllocation().subList(0, countToBeDeleted);
            seatsAllocation.forEach(seatNumber -> {
                Seat<Passenger> seat = this.seats[seatNumber - this.seatStarts];
                seat.remove(passenger);
                this.waitingQueue.checkAndAdd(
                        passengerElement -> Seat.preAddCheck(seat, passengerElement),
                        seat);
            });
            // remove the removed seats
            this.logger.info("REMOVED SEATS ARE : {}", seatsAllocation);
            passenger.getSeatsAllocation().removeAll(seatsAllocation);
            passenger.setTravelersCount(passenger.getTravelersCount() - countToBeDeleted);
        }
    }

    /**
     * To get the current state of the passenger we are given
     * POSSIBILITIES
     * 1. From the seats-allocation (if atleast on seat is allocated for provided passenger)
     * get the seats-allocation from the provided @param passenger,
     * and if it is not null we can get any one of the seat,
     * and fetch the current state of the passenger from the fetched seat object
     * 2. From the Queuer class (no seats are allocated for the provided passenger)
     * if the seats-allocation list is null then we can retrive the current state from the waiting queue
     *
     * @param passenger is provided by the provider, and we need to return the current state of this passenger
     * @return current state of the passenger
     */
    public Passenger getCurrentState(Passenger passenger) {
        Passenger wantedPassenger = null;
        if (Validator.genericValidator(passenger, e -> e.getSeatsAllocation() != null && !e.getSeatsAllocation().isEmpty(), Optional.empty()))
            wantedPassenger = this.seats[passenger.getSeatsAllocation().getFirst() - this.seatStarts].get(passenger);
        else
            wantedPassenger = this.waitingQueue.getCurrentState(passenger);
        return wantedPassenger;
    }

    // UTILS
    public int getWaitingCount(String source, String destination) {
        return this.waitingQueue.getWaitingCount(source, destination);
    }
}
