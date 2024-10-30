package com.chand.railway_reservation_system.web.response;

import com.chand.railway_reservation_system.core.entity.Passenger;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class PassengerResponse {
    // generic info about this request response
    private String message;
    private long timestamp;
    private int status;

    // specific to the after successful booking
    private int waitingCount;
    private boolean passengerAcceptance;
    private List<Integer> seatsAllocation;

    private Passenger passenger;

    public PassengerResponse (Passenger passenger, String message, long time, int status) {
        this.passenger = passenger;
        this.message = message;
        this.timestamp = time;
        this.status = status;
    }
}
