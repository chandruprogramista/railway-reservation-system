package com.chand.railway_reservation_system.core.manager;

import com.chand.railway_reservation_system.core.datastructure.Seat;
import com.chand.railway_reservation_system.core.entity.Passenger;
import com.chand.railway_reservation_system.core.templates.Queuer;
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

        if (requireSeats > 0) {
            passenger.setWaitingCount(requireSeats);
            this.waitingQueue.add(passenger);
        }

        this.logger.info("SEATS ARE ALLOCATED FOR PNR : '{}', SEATS NUMBERS ARE : '{}', AND WAITING COUNT : {}", passenger.getPNRId(), seatsAllocation, passenger.getWaitingCount());
        return seatsAllocation;
    }

    public void bookSeats(List<Integer> seatsAllocation, Passenger passenger) {
        seatsAllocation.forEach(seatNumber -> this.seats[seatNumber - this.seatStarts].add(passenger));
        passenger.setSeatsAllocation(seatsAllocation);
        this.logger.info("SEATS ARE BOOKED SUCCESSFULLY FOR : '{}'", passenger.getPNRId());
    }

    public void cancelSeats(int countToBeDeleted, Passenger passenger, boolean waitingDeletionFirst) {

        if (countToBeDeleted == passenger.getTravelersCount()) {
            cancelSeats(passenger);
            return;
        }

        int cancelCount = countToBeDeleted;

        if (waitingDeletionFirst) {
            int waitingCount = passenger.getWaitingCount();
            countToBeDeleted -= waitingCount;
            if (countToBeDeleted >= 0) {
                this.waitingQueue.remove(passenger, OptionalInt.of(passenger.getWaitingCount()), Optional.empty());
                passenger.setTravelersCount(passenger.getTravelersCount() - passenger.getWaitingCount());
                this.cancelHelper(passenger, countToBeDeleted);
            }
            else {
                this.waitingQueue.remove(passenger, OptionalInt.of(cancelCount), Optional.empty());
                passenger.setTravelersCount(passenger.getTravelersCount() - cancelCount);
            }
            passenger.setWaitingCount(countToBeDeleted >= 0 ? 0 : -countToBeDeleted);
        } else {
            // pass
        }
    }

    public void cancelSeats (Passenger passenger) {
        if (passenger.getWaitingCount() > 0)
            this.waitingQueue.remove(passenger, OptionalInt.of(passenger.getWaitingCount()), Optional.empty());

        this.cancelHelper(passenger, passenger.getSeatsAllocation().size());
    }

    private void cancelHelper (Passenger passenger, int countToBeDeleted) {
        if (countToBeDeleted > 0) {
            List<Integer> seatsAllocation = passenger.getSeatsAllocation().subList(0, countToBeDeleted);
            seatsAllocation.forEach(seatNumber -> {
                Seat<Passenger> seat = this.seats[seatNumber - this.seatStarts];
                seat.remove(passenger);
                this.waitingQueue.checkAndAdd(
                        passengerElement -> Seat.preAddCheck(seat, passengerElement),
                        seat);
            });

            passenger.setTravelersCount(passenger.getTravelersCount() - countToBeDeleted);
        }
    }

    // UTILS
    public int getWaitingCount (String source, String destination) {
        return this.waitingQueue.getWaitingCount(source, destination);
    }
}
