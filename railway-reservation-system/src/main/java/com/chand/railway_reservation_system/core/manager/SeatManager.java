package com.chand.railway_reservation_system.core.manager;

import com.chand.railway_reservation_system.core.datastructure.Tree;
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
import org.springframework.stereotype.Component;

import java.util.*;

import static com.chand.railway_reservation_system.core.Constants.MAX_PEOPLE_FOR_SINGLE_PNR;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SeatManager {

    private String coachName;

    // inclusive
    private final int seatStarts;

    // exclusive
    private final int seatsEnds;

    private final int MAX_PASSENGERS_FOR_SINGLE_BOOK;

    // actual seats
    private Tree<Passenger>[] seats;

    private final Queuer<Passenger> waitingQueue;

    // logger
    private final Logger logger = LoggerFactory.getLogger(SeatManager.class);

    @Autowired
    public SeatManager(@Value("${train.coach-name}") String coachName,
                       @Value("${train.seats.start}") int start,
                       @Value("${train.seats.end}") int end,
                       @Value("${train.max-passengers-for-single-booking}") int var,
                       @Qualifier("singleQueue") Queuer<Passenger> queue) {
        this.coachName = coachName;
        this.seatStarts = start;
        this.seatsEnds = end;
        this.MAX_PASSENGERS_FOR_SINGLE_BOOK = var;
        this.waitingQueue = queue;
        // TEST
        init();
    }

    @PostConstruct
    private void init() {
        this.seats = new Tree[this.seatsEnds - this.seatStarts + 1];
        Arrays.setAll(this.seats, index -> new Tree<>(this.seatStarts + index));
    }

    public Optional<List<Integer>> getSeats(Passenger passenger) {
        if (passenger.getTravelersCount() > this.MAX_PASSENGERS_FOR_SINGLE_BOOK) {
            this.logger.info("MAX PEOPLE FOR A SINGLE TICKET IS : {}, AND YOUR PASSENGERS COUNT IS EXCEEDED", MAX_PEOPLE_FOR_SINGLE_PNR);
            return Optional.empty();
        }

        List<Integer> seatsAllocation = new ArrayList<>();
        int requireSeats = passenger.getTravelersCount();

        for (int i = 0; i < this.seats.length && requireSeats > 0; i++) {
            if (Tree.preAddCheck(this.seats[i], passenger)) {
                seatsAllocation.add(this.seats[i].getId());
                --requireSeats;
            }
        }

        if (requireSeats > 0) {
            passenger.setWaitingCount(requireSeats);
            this.waitingQueue.add(passenger);
        }

        passenger.setSeatsAllocation(seatsAllocation);
        this.logger.info("SEATS ARE ALLOCATED FOR NAME : '{}', AND SEATS NUMBERS : '{}'", passenger.getName(), seatsAllocation);
        return Optional.of(seatsAllocation);
    }

    public void bookSeats(List<Integer> seatsAllocation, Passenger passenger) {
        seatsAllocation.forEach(seatNumber -> this.seats[seatNumber - this.seatStarts].add(passenger));
        this.logger.info("SEATS ARE BOOKED SUCCESSFULLY FOR : '{}'", passenger.getName());
    }

    public void cancelSeats(int countToBeDeleted, Passenger passenger, boolean waitingDeletionFirst) {

        if (countToBeDeleted > passenger.getTravelersCount() || countToBeDeleted < 0) {
            this.logger.info("PASSENGERS COUNT TOO SMALL TO CANCEL 'COUNT TO WANT CANCEL : {}', 'ACTUAL PASSENGERS COUNT : {}'", countToBeDeleted, passenger.getTravelersCount());
            return;
        }

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
                this.cancelHelper(passenger, countToBeDeleted);
            }
            else {
                this.waitingQueue.remove(passenger, OptionalInt.of(cancelCount), Optional.empty());
            }
            passenger.setWaitingCount(countToBeDeleted >= 0 ? 0 : -countToBeDeleted);
            passenger.setTravelersCount(passenger.getTravelersCount() - cancelCount);
        } else {
            // pass
        }
    }

    public void cancelSeats (Passenger passenger) {
        if (passenger.getWaitingCount() > 0)
            this.waitingQueue.remove(passenger, OptionalInt.of(passenger.getWaitingCount()), Optional.empty());

        this.cancelHelper(passenger, passenger.getSeatsAllocation().size());
    }

    public void cancelHelper (Passenger passenger, int countToBeDeleted) {
        if (countToBeDeleted > 0) {
            List<Integer> seatsAllocation = passenger.getSeatsAllocation().subList(0, countToBeDeleted);
            seatsAllocation.forEach(seatNumber -> {
                Tree<Passenger> seat = this.seats[seatNumber - this.seatStarts];
                seat.remove(passenger);
                this.waitingQueue.checkAndAdd(
                        passengerElement -> Tree.preAddCheck(seat, passengerElement),
                        seat);
            });
        }
    }

    // UTILS
    public int getWaitingCount (String source, String destination) {
        return this.waitingQueue.getWaitingCount(source, destination);
    }
}
