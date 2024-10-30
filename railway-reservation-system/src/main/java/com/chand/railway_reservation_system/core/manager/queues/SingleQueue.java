package com.chand.railway_reservation_system.core.manager.queues;

import com.chand.railway_reservation_system.core.datastructure.Seat;
import com.chand.railway_reservation_system.core.entity.Passenger;
import com.chand.railway_reservation_system.core.templates.Queuer;
import com.chand.railway_reservation_system.web.repo.TicketRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

@Component
public class SingleQueue implements Queuer<Passenger> {

    private int[][] waitingTracker;

    private Queue<Passenger> queue;

    private final int totalStations;

    private final TicketRepo ticketRepo;

    @Autowired
    public SingleQueue(@Value("${train.total-stations}") int totalStations, TicketRepo ticketRepo) {
        this.totalStations = totalStations;
        this.ticketRepo = ticketRepo;
    }

    @PostConstruct
    public void init() {
        this.queue = new LinkedList<>();
        this.waitingTracker = new int[this.totalStations - 1][];
        for (int i = 0; i < this.totalStations - 1; i++) this.waitingTracker[i] = new int[this.totalStations - i - 1];
    }

    @Override
    public void add(Passenger element) {
        this.queue.add(element);
        this.waitingTracker[element.getSourceAsInt()][element.getDestinationAsInt() - element.getSourceAsInt() - 1] += element.getWaitingCount();
//        DEBUG
//        System.out.println(Arrays.deepToString(this.waitingTracker));
    }

    @Override
    public void remove(Passenger element, OptionalInt waitingCount, Optional<Iterator<Passenger>> iterator) {

        waitingCount.ifPresent(count -> {
            if (count == element.getWaitingCount()) {
                iterator.ifPresentOrElse(
                        Iterator::remove,
                        () -> this.queue.remove(element)
                );
            }
            this.waitingTracker[element.getSourceAsInt()][element.getDestinationAsInt() - element.getSourceAsInt() - 1] -= count;
        });
    }

    @Override
    public void checkAndAdd(Predicate<Passenger> predicate, Seat<Passenger> seat) {

        Iterator<Passenger> iterator = this.queue.iterator();

        while (iterator.hasNext()) {
            Passenger passenger = null;
            if (predicate.test(passenger = iterator.next())) {
                seat.add(passenger);
                // add the seat number to the seatsAllocation list
                if (passenger.getSeatsAllocation() == null) {
                    passenger.setSeatsAllocation(new ArrayList<>());
                }
                passenger.getSeatsAllocation().add(seat.getId());
                int waitingCount = passenger.getWaitingCount();

                if (--waitingCount == 0) {
                    // remove the passenger from the queue because there is no queueing anymore (waiting count is to be ZERO)
                    this.remove(passenger, OptionalInt.of(1), Optional.of(iterator));
                    // merge the result to the DB
                    this.ticketRepo.save(passenger);
                }
                passenger.setWaitingCount(waitingCount);
            }
        }
    }

    @Override
    public int getWaitingCount(String source, String destination) {
        int s = source.charAt(0) - 'A';
        int d = destination.charAt(0) - 'A';
        return this.waitingTracker[s][d - 1 - s];
    }

    @Override
    public Passenger getCurrentState(Passenger element) {
        AtomicReference<Passenger> wantedPassenger = new AtomicReference<>(element);
        this.queue.forEach(currentPassenger -> {
            if (currentPassenger.equals(element)) {
                wantedPassenger.set(currentPassenger);
                return;
            }
        });
        return wantedPassenger.get();
    }
}
