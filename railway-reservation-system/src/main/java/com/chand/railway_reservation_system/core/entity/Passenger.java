package com.chand.railway_reservation_system.core.entity;

import com.chand.railway_reservation_system.core.constants.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ticket")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Passenger implements Comparable<Passenger> {

    @Id
    @Column(name = "PNR_id")
    private String PNRId;

    @Column(name = "source")
    private String source;

    @Column(name = "destination")
    private String destination;

    @Column(name = "name")
    private String name;

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
    @JsonIgnore
    private int START = -1;

    @Transient
    @JsonIgnore
    private int END = -1;

    public Passenger(String PNRId, String source, String destination, String name, int travelersCount, int waitingCount, int initialTravelersCount, List<Integer> seatsAllocation) {
        this.PNRId = PNRId;
        this.source = source;
        this.destination = destination;
        this.name = name;
        this.travelersCount = travelersCount;
        this.waitingCount = waitingCount;
        this.initialTravelersCount = initialTravelersCount;
        this.seatsAllocation = seatsAllocation;
    }

    public Passenger(String PNRId, String source, String destination, String name, int travelersCount, int waitingCount, int initialTravelersCount) {
        this.PNRId = PNRId;
        this.source = source;
        this.destination = destination;
        this.name = name;
        this.travelersCount = travelersCount;
        this.waitingCount = waitingCount;
        this.initialTravelersCount = initialTravelersCount;
    }

    // REST CONTROLLER is call this one only
    public Passenger(String name, String source, String destination, int travelersCount) {
        this.source = source;
        this.destination = destination;
        this.name = name;
        this.travelersCount = travelersCount;
        this.initialTravelersCount = travelersCount;

        // generate the UUID
        this.PNRId = UUID.randomUUID().toString();
    }
    // testing only
    public Passenger(String pnr, String name, String source, String destination, int travelersCount) {
        this.PNRId = pnr;
        this.source = source;
        this.destination = destination;
        this.name = name;
        this.travelersCount = travelersCount;
        this.initialTravelersCount = travelersCount;
    }

    @JsonIgnore
    public int getSourceAsInt () {
        return this.START == -1 ? (this.START = this.source.compareTo(Constants.START)) : this.START;
    }

    @JsonIgnore
    public int getDestinationAsInt () {
        return this.END == -1 ? (this.destination.compareTo(Constants.START)) : this.END;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passenger passenger = (Passenger) o;
        return Objects.equals(PNRId, passenger.PNRId) && Objects.equals(name, passenger.getName());
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "PNRId='" + PNRId + '\'' +
                ", source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", name='" + name + '\'' +
                ", travelersCount=" + travelersCount +
                ", waitingCount=" + waitingCount +
                ", initialTravelersCount=" + initialTravelersCount +
                ", seatsAllocation=" + seatsAllocation +
                '}';
    }

    @Override
    public int compareTo(Passenger o) {
        System.out.println("*** From the entity ***");
        if (o == null)
            return 0;
        return this.getSource().compareTo(o.getDestination()) >= 0 ? 1 : this.getDestination().compareTo(o.getSource()) <= 0 ? -1 : 0;
    }
}
