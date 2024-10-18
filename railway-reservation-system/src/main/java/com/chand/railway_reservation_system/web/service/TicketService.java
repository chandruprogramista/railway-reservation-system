package com.chand.railway_reservation_system.web.service;

import com.chand.railway_reservation_system.core.Constants;
import com.chand.railway_reservation_system.core.entity.Ticket;
import com.chand.railway_reservation_system.core.manager.SeatsManager;
import com.chand.railway_reservation_system.core.validator.Validator;
import com.chand.railway_reservation_system.web.repo.TicketRepo;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TicketService {

    // for the persistence purpose
    private final TicketRepo ticketRepo;
    // manages our seats
    private final SeatsManager seatsManager;

    private final Logger logger = LoggerFactory.getLogger(TicketService.class);

    @Autowired
    public TicketService (TicketRepo ticketRepo, SeatsManager seatsManager) {
        this.ticketRepo = ticketRepo;
        this.seatsManager = seatsManager;
    }

    @PostConstruct
    public void postConstruct () {
        this.seatsManager.createSeatsWithRange(1, 8);
        this.logger.info("INITIAL DATA LOADS FROM 'DB' TO IN-MEMORY 'SEATS'");
        this.ticketRepo.findAll().forEach(singleTicket -> {
            this.seatsManager.bookSeats(singleTicket, singleTicket.getSeatsAllocation());
            this.seatsManager.updateWaitingCount(singleTicket.getWaitingCount());
        });
    }

    public List<Ticket> getAllTickets () {
        return this.ticketRepo.findAll();
    }

    public Ticket getTicket (String pnrId) {
        return this.ticketRepo.findById(pnrId).orElseGet(Ticket::new);
    }

    @Transactional
    public Optional<List<Integer>> bookTicket (Ticket ticket) {

        if (Validator.sourceAndDestinationValidator().test(ticket.getSource(), ticket.getDestination())) {
            setSomeAdditional(ticket);

            Optional<List<Integer>> optionalSeatsList = this.seatsManager.getSeats(ticket);

            optionalSeatsList.ifPresentOrElse(
                    (seatsList) -> {
                        this.seatsManager.bookSeats(ticket, seatsList);
                        ticket.setSeatsAllocation(seatsList);
                        this.ticketRepo.save(ticket);
                        // If the ticket is persisted then the PNR is generated
                        this.logger.info("TICKET IS PERSISTED SUCCESSFULLY AND 'PNR' IS GENERATED : '{}'", ticket.getPNRId());
                    },
                    () -> this.logger.info("TICKET BOOKING IS UNSUCCESSFUL FOR 'NAME' : '{}'", ticket.getName())

            );

            return optionalSeatsList;
        }
        else {
            this.logger.info("THE SOURCE AND DESTINATION ARE IN THE OPPOSITE FORM (OR) BOTH ARE SAME");
            return Optional.empty();
        }
    }

    @Transactional
    public Optional<Ticket> cancelTicket (String pnrId, int countToBeDelete) {
        return cancelTicketHelper(pnrId, Optional.of(countToBeDelete));
    }

    public Optional<Ticket> bulkCancelTicket (String pnrId) {
        return cancelTicketHelper(pnrId, Optional.empty());
    }

    private Optional<Ticket> cancelTicketHelper (String pnrId, Optional<Integer> optionalCount) {
        Optional<Ticket> optionalTicket = this.ticketRepo.findById(pnrId);
        optionalTicket.ifPresentOrElse(
                ticket -> {
                    int countToBeDelete = optionalCount.orElse(optionalTicket.get().getTravelersCount());
                    // we need the modified entities while the cancelation of this entity
                    Set<Ticket> modifiedTickets = this.seatsManager.cancelSeats(ticket, countToBeDelete);
                    modifiedTickets.forEach(this.ticketRepo::save);
                    Ticket updatedTicket = this.ticketRepo.save(ticket);
                    this.logger.info("TICKET'S TRAVELERS COUNT IS TO BE UPDATED SUCCESSFULLY TICKET : {}", updatedTicket);
                },
                () -> this.logger.info("TICKET NOT FOUND PNR : {}", pnrId)
        );

        return optionalTicket;
    }

    private void setSomeAdditional (Ticket ticket) {
        // set initial travelers count
        // this won't change
        ticket.setInitialTravelersCount(ticket.getTravelersCount());

        // set the start and end to the comparison purpose in the Seat data-structure
        ticket.setStart(ticket.getSource().charAt(0) - Constants.START.charAt(0));
        ticket.setEnd(ticket.getDestination().charAt(0) - Constants.START.charAt(0));
    }
}
