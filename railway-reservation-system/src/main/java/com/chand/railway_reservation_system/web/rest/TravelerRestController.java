package com.chand.railway_reservation_system.web.rest;

import com.chand.railway_reservation_system.core.entity.Passenger;
import com.chand.railway_reservation_system.web.response.PassengerBookingResponse;
import com.chand.railway_reservation_system.web.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<PassengerBookingResponse> bookTicket(@RequestBody(required = true) Passenger ticket) {

        PassengerBookingResponse passengerBookingResponse = PassengerBookingResponse.builder(ticket)
                .setName()
                .setSource()
                .setDestination()
                .setTravelersCount()
                .build();

        // call the service layer
        Optional<Passenger> passenger = passengerService.bookSeats(ticket);

        passenger.ifPresentOrElse(currentPassenger -> {
                PassengerBookingResponse.builder(passengerBookingResponse)
                        .setTicketAcceptance(true)
                        .setPNRId(currentPassenger.getPNRId())
                        .setPassengerWaitingCount(currentPassenger.getWaitingCount())
                        .setAllocatedSeats(currentPassenger.getSeatsAllocation())
                        .setMessage("HEY MAN YOU GOT A TICKET, SO GET READY FOR THE JOURNEY")
                        .setStatus(HttpStatus.OK.value())
                        .setTimestamp(System.currentTimeMillis())
                        .build();
            }, () -> {
                PassengerBookingResponse.builder(passengerBookingResponse)
                        .setTicketAcceptance(false)
                        .setMessage("I THINK YOU HAVE SOME VALIDATION ISSUES")
                        .setStatus(HttpStatus.NOT_ACCEPTABLE.value()).
                        setTimestamp(System.currentTimeMillis())
                        .build();
            }
        );

        return new ResponseEntity<>(passengerBookingResponse, HttpStatus.OK);
    }

    @PostMapping("waitingStatus")
    public int getWaitingStatus (@RequestParam String source, @RequestParam String destination) {
        return this.passengerService.getWaitingCount(source, destination);
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
