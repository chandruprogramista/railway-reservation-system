package com.chand.railway_reservation_system.core.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "ticket")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Passenger implements Comparable<Passenger> {

    @Id
    @UuidGenerator
    @Column(name = "PNR_id")
    private String PNRId;

    @Column(name = "source")
    private String source;

    @Column(name = "destination")
    private String destination;

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

    @Transient
    private int[] sourceAndDestination = new int[2];

    public Passenger(String PNRId, int[] sourceAndDestination, String source, String destination, String name, int travelersCount, int waitingCount, int initialTravelersCount, List<Integer> seatsAllocation) {
        this.PNRId = PNRId;
        this.sourceAndDestination = sourceAndDestination;
        this.source = source;
        this.destination = destination;
        this.name = name;
        this.travelersCount = travelersCount;
        this.waitingCount = waitingCount;
        this.initialTravelersCount = initialTravelersCount;
        this.seatsAllocation = seatsAllocation;
    }

    public Passenger(String PNRId, int[] sourceAndDestination, String source, String destination, String name, int travelersCount, int waitingCount, int initialTravelersCount) {
        this.PNRId = PNRId;
        this.sourceAndDestination = sourceAndDestination;
        this.source = source;
        this.destination = destination;
        this.name = name;
        this.travelersCount = travelersCount;
        this.waitingCount = waitingCount;
        this.initialTravelersCount = initialTravelersCount;
    }

    public Passenger(String name, String source, String destination, int travelersCount) {
        this.source = source;
        this.destination = destination;
        this.name = name;
        this.travelersCount = travelersCount;
        this.initialTravelersCount = travelersCount;

        this.sourceAndDestination[0] = source.charAt(0) - 'A';
        this.sourceAndDestination[1] = source.charAt(1) - 'A';
    }

    // testing only
    public Passenger(String pnr, String name, String source, String destination, int travelersCount) {
        this.PNRId = pnr;
        this.source = source;
        this.destination = destination;
        this.name = name;
        this.travelersCount = travelersCount;
        this.initialTravelersCount = travelersCount;

        this.sourceAndDestination[0] = source.charAt(0) - 'A';
        this.sourceAndDestination[1] = destination.charAt(0) - 'A';
    }

    public int[] getSourceAndDestination() {
        return sourceAndDestination;
    }

    public void setSourceAndDestination(int[] sourceAndDestination) {
        this.sourceAndDestination = sourceAndDestination;
    }

    public List<Integer> getSeatsAllocation() {
        return seatsAllocation;
    }

    public void setSeatsAllocation(List<Integer> seatsAllocation) {
        this.seatsAllocation = seatsAllocation;
    }

    public int getWaitingCount() {
        return waitingCount;
    }

    public void setWaitingCount(int waitingCount) {
        this.waitingCount = waitingCount;
    }

    public int getInitialTravelersCount() {
        return initialTravelersCount;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passenger passenger = (Passenger) o;
        return Objects.equals(PNRId, passenger.PNRId) && Objects.equals(source, passenger.source) && Objects.equals(destination, passenger.destination) && Objects.equals(name, passenger.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(PNRId, source, destination, name);
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "name='" + name + '\'' +
                ", destination='" + destination + '\'' +
                ", source='" + source + '\'' +
                ", PNRId='" + PNRId + '\'' +
                '}';
    }

    @Override
    public int compareTo(Passenger that) {
        Objects.requireNonNull(that);
        return this.sourceAndDestination[1] <= that.sourceAndDestination[0] ? -1 : this.sourceAndDestination[0] >= that.sourceAndDestination[1] ? 1 : 0;
    }
}
