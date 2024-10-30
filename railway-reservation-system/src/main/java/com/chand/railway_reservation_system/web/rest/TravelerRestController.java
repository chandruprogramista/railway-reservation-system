package com.chand.railway_reservation_system.web.rest;

import com.chand.railway_reservation_system.core.constants.SeatStatus;
import com.chand.railway_reservation_system.core.entity.Passenger;
import com.chand.railway_reservation_system.core.util.Pair;
import com.chand.railway_reservation_system.web.response.PassengerResponse;
import com.chand.railway_reservation_system.web.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

// Need to load the mysql table to the actual SeatManager component for every refershing

@RestController
@RequestMapping("railway-reservation")
public class TravelerRestController {

    PassengerService passengerService;

    @Autowired
    public TravelerRestController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @GetMapping(value = "tickets")
    public List<Passenger> getAllTickets() {
        return this.passengerService.getAllBooking();
    }

    @GetMapping(value = "tickets/{pnrId}")
    public Passenger getTicket(@PathVariable(name = "pnrId") String pnrId) {
        return this.passengerService.getPassenger(pnrId).orElse(null);
    }

    @PostMapping(value = "book", produces = "application/json")
    public ResponseEntity<PassengerResponse> bookTicket(@RequestBody(required = true) Passenger ticket) {

        AtomicReference<PassengerResponse> response = new AtomicReference<>();

        this.passengerService
                .bookSeats(ticket)
                .ifPresentOrElse(passenger -> {
                    System.out.println(passenger);
                    response.set(new PassengerResponse(passenger, "SEAT IS BOOKED SUCCESSFULLY", System.currentTimeMillis(), HttpStatus.OK.value()));
                }, () -> {
                    response.set(PassengerResponse.builder()
                            .message("SOME VALIDATION ERROR IS HAPPENED")
                            .timestamp(System.currentTimeMillis())
                            .status(HttpStatus.NOT_ACCEPTABLE.value())
                            .build());
                });

        return ResponseEntity.ok(response.get());
    }

    @PutMapping("cancel")
    public ResponseEntity<PassengerResponse> cancelTicket(@RequestParam(value = "pnrId") String pnrId, @RequestParam(value = "cancelCount") Integer countToBeDeleted) {
        AtomicReference<PassengerResponse> response = new AtomicReference<>();
        Pair<Optional<Passenger>, SeatStatus> serviceCall = this.passengerService.cancelSeats(pnrId, countToBeDeleted);
        serviceCall.getFirst()
                .ifPresentOrElse(passenger -> {
                    response.set(PassengerResponse.builder()
                            .passenger(passenger)
                            .message(serviceCall.getSecond() == SeatStatus.CANCEL
                                    ? String.format("SUCCESSFULLY CANCEL SOME '%s' SEATS", countToBeDeleted)
                                    : "ERADICATE ALL THE SEATS")
                            .timestamp(System.currentTimeMillis())
                            .status(HttpStatus.OK.value())
                            .build()
                    );
                }, () -> {
                    if (serviceCall.getSecond() == SeatStatus.VALIDATION_ERROR)
                        response.set(PassengerResponse.builder()
                                .message("VALIDATION ERROR")
                                .timestamp(System.currentTimeMillis())
                                .status(HttpStatus.OK.value())
                                .build());
                    else
                        response.set(PassengerResponse.builder()
                                .message("PASSENGER IS NOT FOUND, CHECK YOUR PNR")
                                .timestamp(System.currentTimeMillis())
                                .status(HttpStatus.OK.value())
                                .build());
                });

        return new ResponseEntity<>(response.get(), HttpStatus.OK);
    }

    @DeleteMapping("cancel/{pnrId}")
    public ResponseEntity<PassengerResponse> cancel(@PathVariable(value = "pnrId") String pnrId) {
        AtomicReference<PassengerResponse> response = new AtomicReference<>();
        this.passengerService.cancelSeats(pnrId)
                .ifPresentOrElse(currentPassenger -> {
                    response.set(PassengerResponse.builder()
                            .passenger(currentPassenger)
                            .message("TICKET IS FULLY DROPED SUCCESSFULLY")
                            .timestamp(System.currentTimeMillis())
                            .build());
                }, () -> {
                    response.set(PassengerResponse.builder()
                            .message("PASSENGER NOT FOUND")
                            .timestamp(System.currentTimeMillis())
                            .build());
                });

        return new ResponseEntity<>(response.get(), HttpStatus.OK);
    }

    @PostMapping("waitingStatus")
    public int getWaitingStatus(@RequestParam String source, @RequestParam String destination) {
        return this.passengerService.getWaitingCount(source, destination);
    }
    // completed

    @PostMapping("status")
    public ResponseEntity<PassengerResponse> status(@RequestParam(name = "pnrId") String pnrId) {
        Optional<Passenger> optionalPassenger = this.passengerService.getCurrentState(pnrId);
        AtomicReference<PassengerResponse> response = new AtomicReference<>();
        optionalPassenger.ifPresentOrElse(passenger -> {
            response.set(PassengerResponse.builder()
                    .message("YOUR TICKET'S CURRENT STATUS IS FETCHED SUCCESSFULLY")
                    .timestamp(System.currentTimeMillis())
                    .passenger(passenger)
                    .build());
        }, () -> {
            response.set(PassengerResponse.builder()
                    .message("YOUR PNR IS NOT VALID, TRY AGAIN!")
                    .timestamp(System.currentTimeMillis())
                    .build());
        });

        return new ResponseEntity<>(response.get(), HttpStatus.OK);
    }
}
