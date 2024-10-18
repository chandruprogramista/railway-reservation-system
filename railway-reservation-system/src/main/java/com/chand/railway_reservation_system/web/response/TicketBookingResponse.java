package com.chand.railway_reservation_system.web.response;

import com.chand.railway_reservation_system.core.entity.Ticket;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class TicketBookingResponse {

    // basic ticket response
    private String name;
    private String source;
    private String destination;
    private int travelersCount;

    // generic info about this request response
    private String message;
    private long timestamp;
    private int status;

    // specific to the after successful booking
    private int ticketWaitingCount;
    private boolean ticketAcceptance;
    private List<Integer> seatsAllocation;

    public TicketBookingResponse(String name, String source, String destination, int travelersCount, int ticketWaitingCount) {
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.travelersCount = travelersCount;
        this.ticketWaitingCount = ticketWaitingCount;
    }

    public TicketBookingResponse(String name, String source, String destination, int travelersCount) {
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.travelersCount = travelersCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getTravelersCount() {
        return travelersCount;
    }

    public void setTravelersCount(int travelersCount) {
        this.travelersCount = travelersCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTicketWaitingCount() {
        return ticketWaitingCount;
    }

    public void setTicketWaitingCount(int ticketWaitingCount) {
        this.ticketWaitingCount = ticketWaitingCount;
    }

    public boolean isTicketAcceptance() {
        return ticketAcceptance;
    }

    public void setTicketAcceptance(boolean ticketAcceptance) {
        this.ticketAcceptance = ticketAcceptance;
    }

    public List<Integer> getSeatsAllocation() {
        return seatsAllocation;
    }

    public void setSeatsAllocation(List<Integer> seatsAllocation) {
        this.seatsAllocation = seatsAllocation;
    }

    public static TicketBuilder builder (Ticket ticket) {
        return new TicketBuilder(ticket);
    }

    public static TicketBookingResponseBuilder builder (TicketBookingResponse ticketBookingResponse) {
        return new TicketBookingResponseBuilder(ticketBookingResponse);
    }

    public static class TicketBuilder {

        private String name;
        private String source;
        private String destination;
        private int travelersCount;

        private final Ticket ticket;

        public TicketBuilder (Ticket ticket) {
            this.ticket = ticket;
        }

        public TicketBuilder setDestination() {
            this.destination = this.ticket.getDestination();
            return this;
        }

        public TicketBuilder setSource() {
            this.source = this.ticket.getSource();
            return this;
        }

        public TicketBuilder setName() {
            this.name = this.ticket.getName();
            return this;
        }

        public TicketBuilder setTravelersCount() {
            this.travelersCount = this.ticket.getTravelersCount();
            return this;
        }

        public TicketBookingResponse build () {
            return new TicketBookingResponse(this.name, this.source, this.destination, this.travelersCount);
        }
    }

    public static class TicketBookingResponseBuilder {
        // basic response
        private String message;
        private long timestamp;

        private int status;
        // ticket specific info after the successful booking
        private boolean ticketAcceptance;
        private int ticketWaitingCount;

        private List<Integer> seatsAllocation;

        private final TicketBookingResponse ticketBookingResponse;

        public TicketBookingResponseBuilder (TicketBookingResponse ticketBookingResponse) {
            this.ticketBookingResponse = ticketBookingResponse;
        }

        public TicketBookingResponseBuilder setMessage(String message) {
            this.ticketBookingResponse.setMessage(message);
            return this;
        }

        public TicketBookingResponseBuilder setAllocatedSeats(List<Integer> seatsAllocation) {
            this.ticketBookingResponse.setSeatsAllocation(seatsAllocation);
            return this;
        }

        public TicketBookingResponseBuilder setTicketWaitingCount(int ticketWaitingCount) {
            this.ticketBookingResponse.setTicketWaitingCount(ticketWaitingCount);
            return this;
        }

        public TicketBookingResponseBuilder setStatus(int status) {
            this.ticketBookingResponse.setStatus(status);
            return this;
        }

        public TicketBookingResponseBuilder setTicketAcceptance(boolean ticketAcceptance) {
            this.ticketBookingResponse.setTicketAcceptance(ticketAcceptance);
            return this;
        }

        public TicketBookingResponseBuilder setTimestamp(long timestamp) {
            this.ticketBookingResponse.setTimestamp(timestamp);
            return this;
        }

        public TicketBookingResponse build () {
            return this.ticketBookingResponse;
        }
    }
}
