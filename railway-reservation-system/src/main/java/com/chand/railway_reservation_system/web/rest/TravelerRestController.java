package com.chand.railway_reservation_system.web.rest;

import com.chand.railway_reservation_system.core.entity.Ticket;
import com.chand.railway_reservation_system.web.response.TicketBookingResponse;
import com.chand.railway_reservation_system.web.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// Need to load the mysql table to the actual SeatManager component for every refershing

@RestController
@RequestMapping("book")
public class TravelerRestController {

    TicketService ticketService;

    @Autowired
    public TravelerRestController (TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping(value = "tickets")
    public List<Ticket> getAllTickets () {
        return this.ticketService.getAllTickets();
    }

    @GetMapping(value = "tickets/{pnrId}")
    public Ticket getTicket (@PathVariable(name = "pnrId") String pnrId) {
        return this.ticketService.getTicket(pnrId);
    }

    @PostMapping(value = "ticket", produces = "application/json")
    public ResponseEntity<TicketBookingResponse> bookTicket (@RequestBody(required = true) Ticket ticket) {

        TicketBookingResponse ticketBookingResponse = TicketBookingResponse.builder(ticket)
                .setName()
                .setSource()
                .setDestination()
                .setTravelersCount()
                .build();

        Optional<List<Integer>> optionalSeatsList;

        if ((optionalSeatsList = ticketService.bookTicket(ticket)).isPresent()) {
            // add the seats that are allocated to this ticket
            return new ResponseEntity<>(TicketBookingResponse.builder(ticketBookingResponse)
                    .setTicketAcceptance(true)
                    .setTicketWaitingCount(ticket.getWaitingCount())
                    .setAllocatedSeats(optionalSeatsList.get())
                    .setMessage("The ticket is successfully booked")
                    .setStatus(HttpStatus.OK.value())
                    .setTimestamp(System.currentTimeMillis())
                    .build(), HttpStatus.OK);
        }

        else {
            return new ResponseEntity<>(TicketBookingResponse.builder(ticketBookingResponse)
                    .setTicketAcceptance(false)
                    .setMessage("Hey man sorry, there is no ticket for your travelling journey")
                    .setStatus(HttpStatus.NOT_ACCEPTABLE.value())
                    .setTimestamp(System.currentTimeMillis())
                    .build(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PutMapping("cancel")
    public Ticket cancel (@RequestParam String pnrId, @RequestParam Integer cancelCount) {
        return this.ticketService.cancelTicket(pnrId, cancelCount).orElse(null);
    }

    @DeleteMapping("cancel/{pnrId}")
    public Ticket cancel (@PathVariable(value = "pnrId") String pnrId) {
        return this.ticketService.bulkCancelTicket (pnrId).orElse(null);
    }
}
