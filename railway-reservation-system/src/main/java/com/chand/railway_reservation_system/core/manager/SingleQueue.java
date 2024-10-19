package com.chand.railway_reservation_system.core.manager;

import com.chand.railway_reservation_system.core.datastructure.Tree;
import com.chand.railway_reservation_system.core.entity.Passenger;
import com.chand.railway_reservation_system.core.templates.Queuer;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Predicate;

@Component
public class SingleQueue implements Queuer<Passenger> {

    private int[][] waitingTracker;

    private Queue<Passenger> queue;

    private final int totalStations;

    @Autowired
    public SingleQueue (@Value("${train.total-stations}") int totalStations) {
        this.totalStations = totalStations;
    }

    @PostConstruct
    public void init () {
        this.queue = new LinkedList<>();
        this.waitingTracker = new int[this.totalStations - 1][];
        for (int i = 0; i < this.totalStations - 1; i++) this.waitingTracker[i] = new int[this.totalStations - i - 1];
    }

    @Override
    public void add(Passenger element) {
        this.queue.add(element);
        int[] temp = element.getSourceAndDestination();
        ++this.waitingTracker[temp[0]][temp[1]];
    }

    @Override
    public void remove(Passenger element, boolean partial) {
        this.queue.remove(element);
    }

    @Override
    public void checkAndAdd(Predicate<Passenger> predicate, Tree<Passenger> seat) {
        this.queue.forEach(passenger -> {
            if (predicate.test(passenger)) {
                seat.add(passenger);
                int waitingCount = passenger.getWaitingCount();
                if (--waitingCount <= 0) {
                    this.remove(passenger, false);
                    passenger.setWaitingCount(waitingCount);
                    int[] temp = passenger.getSourceAndDestination();
                    --this.waitingTracker[temp[0]][temp[1]];
                }
            }
        });
    }
}
