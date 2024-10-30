package com.chand.railway_reservation_system.core.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "ticket")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Ticket implements Comparable<Ticket> {

    @Id
    @Column(name = "PNR_id")
    public String PNRId;

    @Column(name = "source")
    public String source;

    @Column(name = "destination")
    public String destination;

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
    private int ticketAcceptance;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(PNRId, ticket.PNRId);
    }

    @Override
    public int compareTo(Ticket o) {
        return this.getSource().compareTo(o.getDestination()) >= 0 ? 1 : this.getDestination().compareTo(o.getSource()) <= 0 ? -1 : 0;
    }
}
