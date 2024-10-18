package com.chand.railway_reservation_system.core.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "ticket")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Ticket extends PNRPair {

    @Column(name = "name")
    private String name;

    // also used as a waiting count
    @Column(name = "travelers_count")
    private int travelersCount;

    @Column(name = "waiting_count")
    private int waitingCount;

    @Column(name = "initial_travelers_count")
    private int initialTravelersCount;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "seats_allocation", joinColumns = @JoinColumn(name = "PNR_id"))
    @Column(name = "seat_number")
    private List<Integer> seatsAllocation;

    public List<Integer> getSeatsAllocation() {
        return seatsAllocation;
    }

    public void setSeatsAllocation(List<Integer> seatsAllocation) {
        this.seatsAllocation = seatsAllocation;
    }

    public Ticket () {}

    public Ticket(String PNRId, String source, String destination, String name, int travelersCount, int initialTravelersCount) {
        super(PNRId, source, destination);
        this.name = name;
        this.travelersCount = travelersCount;
        this.initialTravelersCount = initialTravelersCount;
    }

    public Ticket(String name, int travelersCount, int initialTravelersCount) {
        this.name = name;
        this.travelersCount = travelersCount;
        this.initialTravelersCount = initialTravelersCount;
    }

    public int getWaitingCount() {
        return waitingCount;
    }

    public void setWaitingCount(int waitingCount) {
        this.waitingCount = waitingCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInitialTravelersCount(int initialTravelersCount) {
        this.initialTravelersCount = initialTravelersCount;
    }

    public int getTravelersCount() {
        return travelersCount;
    }

    public void setTravelersCount(int travelersCount) {
        this.travelersCount = travelersCount;
    }

    @Override
    public String toString() {
        return super.toString() + "Ticket{" +
                "name='" + name + '\'' +
                ", travelersCount=" + travelersCount +
                ", waitingCount=" + waitingCount +
                ", initialTravelersCount=" + initialTravelersCount +
                '}';
    }

    public int getInitialTravelersCount() {
        return initialTravelersCount;
    }
}
