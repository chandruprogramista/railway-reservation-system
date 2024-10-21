package com.chand.railway_reservation_system.web.rest;

import com.chand.railway_reservation_system.core.entity.Passenger;
import com.chand.railway_reservation_system.web.response.PassengerBookingResponse;
import com.chand.railway_reservation_system.web.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Need to load the mysql table to the actual SeatManager component for every refershing

@RestController
@RequestMapping("book")
public class TravelerRestController {

    PassengerService passengerService;

    @Autowired
    public TravelerRestController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @GetMapping(value = "tickets")
    public List<Passenger> getAllTickets() {
//        return this.ticketService.getAllTickets();
        return null;
    }

    @GetMapping(value = "tickets/{pnrId}")
    public Passenger getTicket(@PathVariable(name = "pnrId") String pnrId) {
//        return this.ticketService.getTicket(pnrId);
        return null;
    }

    @PostMapping(value = "ticket", produces = "application/json")
    public ResponseEntity<PassengerBookingResponse> bookTicket(@RequestBody(required = true) Passenger ticket) {

        PassengerBookingResponse ticketBookingResponse = PassengerBookingResponse.builder(ticket)
                .setName()
                .setSource()
                .setDestination()
                .setTravelersCount()
                .build();

        // call the service layer
        Passenger passenger = passengerService.bookSeats(ticket);
        // add the seats that are allocated to this ticket
        return new ResponseEntity<>(PassengerBookingResponse.builder(ticketBookingResponse)
                .setTicketAcceptance(true)
                .setPassengerWaitingCount(passenger.getWaitingCount())
                .setAllocatedSeats(passenger.getSeatsAllocation())
                .setMessage("The ticket is successfully booked")
                .setStatus(HttpStatus.OK.value())
                .setTimestamp(System.currentTimeMillis())
                .build(), HttpStatus.OK);
    }

    @PutMapping("cancel")
    public Passenger cancel(@RequestParam String pnrId, @RequestParam Integer cancelCount) {
//        return this.ticketService.cancelTicket(pnrId, cancelCount).orElse(null);
        return null;
    }

    @DeleteMapping("cancel/{pnrId}")
    public Passenger cancel(@PathVariable(value = "pnrId") String pnrId) {
//        return this.ticketService.bulkCancelTicket (pnrId).orElse(null);
        return null;
    }
}
