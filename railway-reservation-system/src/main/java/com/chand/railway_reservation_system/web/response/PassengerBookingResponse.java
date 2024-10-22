package com.chand.railway_reservation_system.web.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.chand.railway_reservation_system.core.entity.Passenger;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class PassengerBookingResponse {

    // basic Passenger response
    private String name;
    private String source;
    private String destination;
    private int travelersCount;

    // generic info about this request response
    private String message;
    private long timestamp;
    private int status;

    // specific to the after successful booking
    private String PNRId;
    private int PassengerWaitingCount;
    private boolean PassengerAcceptance;
    private List<Integer> seatsAllocation;

    public PassengerBookingResponse(String name, String source, String destination, int travelersCount, int PassengerWaitingCount) {
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.travelersCount = travelersCount;
        this.PassengerWaitingCount = PassengerWaitingCount;
    }

    public PassengerBookingResponse(String name, String source, String destination, int travelersCount) {
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.travelersCount = travelersCount;
    }

    public String getPNRId() {
        return PNRId;
    }

    public void setPNRId(String PNRId) {
        this.PNRId = PNRId;
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

    public int getPassengerWaitingCount() {
        return PassengerWaitingCount;
    }

    public void setPassengerWaitingCount(int PassengerWaitingCount) {
        this.PassengerWaitingCount = PassengerWaitingCount;
    }

    public boolean isPassengerAcceptance() {
        return PassengerAcceptance;
    }

    public void setPassengerAcceptance(boolean PassengerAcceptance) {
        this.PassengerAcceptance = PassengerAcceptance;
    }

    public List<Integer> getSeatsAllocation() {
        return seatsAllocation;
    }

    public void setSeatsAllocation(List<Integer> seatsAllocation) {
        this.seatsAllocation = seatsAllocation;
    }

    public static PassengerBuilder builder (Passenger Passenger) {
        return new PassengerBuilder(Passenger);
    }

    public static PassengerBookingResponseBuilder builder (PassengerBookingResponse PassengerBookingResponse) {
        return new PassengerBookingResponseBuilder(PassengerBookingResponse);
    }

    public static class PassengerBuilder {

        private String name;
        private String source;
        private String destination;
        private int travelersCount;

        private final Passenger Passenger;

        public PassengerBuilder (Passenger Passenger) {
            this.Passenger = Passenger;
        }

        public PassengerBuilder setDestination() {
            this.destination = this.Passenger.getDestination();
            return this;
        }

        public PassengerBuilder setSource() {
            this.source = this.Passenger.getSource();
            return this;
        }

        public PassengerBuilder setName() {
            this.name = this.Passenger.getName();
            return this;
        }

        public PassengerBuilder setTravelersCount() {
            this.travelersCount = this.Passenger.getTravelersCount();
            return this;
        }

        public PassengerBookingResponse build () {
            return new PassengerBookingResponse(this.name, this.source, this.destination, this.travelersCount);
        }
    }

    public static class PassengerBookingResponseBuilder {
        // basic response
        private String message;
        private long timestamp;

        private int status;
        // Passenger specific info after the successful booking
        private boolean PassengerAcceptance;
        private int PassengerWaitingCount;
        private String PNRId;

        private List<Integer> seatsAllocation;

        private final PassengerBookingResponse PassengerBookingResponse;

        public PassengerBookingResponseBuilder (PassengerBookingResponse PassengerBookingResponse) {
            this.PassengerBookingResponse = PassengerBookingResponse;
        }

        public PassengerBookingResponseBuilder setPNRId (String pnr) {
            this.PassengerBookingResponse.setPNRId(pnr);
            return this;
        }

        public PassengerBookingResponseBuilder setMessage(String message) {
            this.PassengerBookingResponse.setMessage(message);
            return this;
        }

        public PassengerBookingResponseBuilder setAllocatedSeats(List<Integer> seatsAllocation) {
            this.PassengerBookingResponse.setSeatsAllocation(seatsAllocation);
            return this;
        }

        public PassengerBookingResponseBuilder setPassengerWaitingCount(int PassengerWaitingCount) {
            this.PassengerBookingResponse.setPassengerWaitingCount(PassengerWaitingCount);
            return this;
        }

        public PassengerBookingResponseBuilder setStatus(int status) {
            this.PassengerBookingResponse.setStatus(status);
            return this;
        }

        public PassengerBookingResponseBuilder setTimestamp(long timestamp) {
            this.PassengerBookingResponse.setTimestamp(timestamp);
            return this;
        }

        public PassengerBookingResponseBuilder setTicketAcceptance(boolean b) {
            this.PassengerBookingResponse.setPassengerAcceptance(b);
            return this;
        }

        public PassengerBookingResponse build () {
            return this.PassengerBookingResponse;
        }
    }
}
