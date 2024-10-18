package com.chand.railway_reservation_system.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import org.hibernate.annotations.UuidGenerator;

import java.util.Objects;

// mapped-super-class is used to separate the table creation
// in this we can create a entity by it's sub-class
@MappedSuperclass
public class PNRPair implements Comparable<PNRPair> {

    @Id
    @UuidGenerator
    @Column(name = "PNR_id")
    private String PNRId;

    @Override
    public String toString() {
        return "PNRPair{" +
                "PNRId='" + PNRId + '\'' +
                ", source='" + source + '\'' +
                ", start=" + start +
                ", destination='" + destination + '\'' +
                ", end=" + end +
                '}';
    }

    @Column(name = "source")
    private String source;

    @Transient
    private int start;

    @Column(name = "destination")
    private String destination;

    @Transient
    private int end;

    public PNRPair(String PNRId, String source, String destination) {
        this.PNRId = PNRId;
        this.source = source;
        this.destination = destination;
    }

    public PNRPair () {}

    public int getStart() {
        return start;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setStart(int start) {
        this.start = start;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PNRPair pnrPair = (PNRPair) o;
        return Objects.equals(PNRId, pnrPair.PNRId) && Objects.equals(source, pnrPair.source) && Objects.equals(destination, pnrPair.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(PNRId, start, end);
    }

    public String getPNRId() {
        return PNRId;
    }

    public void setPNRId(String PNRId) {
        this.PNRId = PNRId;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @Override
    public int compareTo(PNRPair that) {
        return this.getStart() >= that.getEnd() ? 1 : (this.getEnd() <= that.getStart() ? -1 : 0);
    }
}
