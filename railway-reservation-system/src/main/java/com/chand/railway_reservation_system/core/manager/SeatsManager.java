package com.chand.railway_reservation_system.core.manager;

import com.chand.railway_reservation_system.core.datastructure.Seat;
import com.chand.railway_reservation_system.core.entity.Ticket;
import com.chand.railway_reservation_system.core.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.chand.railway_reservation_system.core.Constants.MAX_PEOPLE_FOR_SINGLE_PNR;
import static com.chand.railway_reservation_system.core.Constants.MAX_WAIT_COUNT;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SeatsManager {

    private String coachName;
    private int currentWaitingCount;
    private Seat<Ticket>[] seats;
    private final Logger logger = LoggerFactory.getLogger(SeatsManager.class);
    private final Queue<Ticket> waitingQueue = new LinkedList<>();
//    private final Map<String, Ticket> ticketMap = new HashMap<>();
    private int seatStartNumber;
//    private int seatEndNumber;

    public SeatsManager() {
    }

    public SeatsManager(String coachName, int start, int end) {
        this.coachName = coachName;
        this.createSeatsWithRange(start, end);
    }

    // explicitly set the coach seats
    public void createSeatsWithRange(int start, int end) {
        this.seats = new Seat[end - start + 1]; // 100, 50
        Arrays.setAll(seats, (index) -> {
            seats[index] = new Seat<>(index + start);
            return seats[index];
        });
        this.seatStartNumber = start;
//        this.seatEndNumber = end;
        this.coachName = "UR";
        logger.info("SEATS ARE CREATED WITH THE COUNT OF : '{}', AND COACH NAME IS : {}", this.seats.length, this.coachName);
    }

    // return the booked seats from the book ticket function
    public Optional<List<Integer>> getSeats(Ticket ticket) {

        if (ticket.getInitialTravelersCount() > MAX_PEOPLE_FOR_SINGLE_PNR) {
            this.logger.info("MAX PEOPLE FOR A SINGLE TICKET IS : {}, AND YOUR TRAVELERS COUNT IS EXCEEDED", MAX_PEOPLE_FOR_SINGLE_PNR);
            return Optional.empty();
        }

        Optional<List<Integer>> arrangedSeats = getSeats(0, ticket, ticket.getInitialTravelersCount());

        arrangedSeats.ifPresentOrElse(seatsList -> this.logger.info("FOR : '{}', ALLOCATED SEATS ARE : '{}'", ticket.getName(), seatsList), () -> this.logger.info("SEATS ARE NOT AVAILABLE FOR : '{}', WITH THE COUNT OF TRAVELERS : '{}'", ticket.getName(), ticket.getTravelersCount()));

        return arrangedSeats;
    }

    private Optional<List<Integer>> getSeats(int index, Ticket ticket, int requiredSeats) {

        if (requiredSeats == 0) {
            return Optional.of(new ArrayList<>());
        }
        // reach the end of the seats array
        if (index >= this.seats.length) {
            if (requiredSeats <= MAX_WAIT_COUNT - currentWaitingCount) {
                this.currentWaitingCount += requiredSeats;
                // update the waiting count in the ticket
                ticket.setWaitingCount(requiredSeats);
                this.waitingQueue.add(ticket);
                return Optional.of(new ArrayList<>());
            }
            return Optional.empty();
        }

        // seat is available for this ticket PNRPair
        // we know the current seat has a room to insert ticket
        if (Validator.preAddCheck().test(this.seats[index], ticket)) {
            Optional<List<Integer>> optionalSeatsList = getSeats(index + 1, ticket, requiredSeats - 1);
            optionalSeatsList.ifPresent(seatsList -> {
                seatsList.add(this.seats[index].getSeatNumber());
            });

            return optionalSeatsList;
        }
        // in the current seat has no availability to the current ticket
        // then go without reduction of requiredSeats
        return getSeats(index + 1, ticket, requiredSeats);
    }

    public void bookSeats(Ticket ticket, List<Integer> seatsList) {
        // ex seat start = 101, end = 230
        // so array is 0 - 128 indices are available
        // seatNumber = 128 - 101
        seatsList.forEach(seatNumber -> {
            this.seats[seatNumber - this.seatStartNumber].add(ticket);
        });

        logger.info("BOOKED SEATS ARE : '{}', AND CURRENT WAITING COUNT IS : '{}'", seatsList, ticket.getWaitingCount());
    }

    public void updateWaitingCount(int count) {
        this.currentWaitingCount += count;
    }

    public Set<Ticket> cancelSeats (Ticket ticket, int countToBeDeleted) {

        if (Validator.cancelingCountValidator().negate().test(ticket, countToBeDeleted)) {
            logger.info("THE DELETING MUST BE LESS THAN OR EQUAL ACTUAL CURRENT TRAVELING COUNT OF THIS TICKET PNR : {}", ticket.getPNRId());
        } else {
            int decreasedWaits = 0;
            // condition for the count is exceeding the actual booked tickets which means that the person wants to delete the waiting count as-well
            // so we need to handle the case
            // bulk delete
            if (countToBeDeleted == ticket.getTravelersCount()) {
                return this.bulkCancelSeat(ticket);
            }
            // condition states that delete is also include the waiting count also
            else if (countToBeDeleted > ticket.getTravelersCount() - ticket.getWaitingCount()) {
                decreasedWaits = (countToBeDeleted - (ticket.getTravelersCount() - ticket.getWaitingCount()));
                ticket.setWaitingCount(ticket.getWaitingCount() - decreasedWaits);
                countToBeDeleted -= decreasedWaits;
                --this.currentWaitingCount;
            }

            List<Integer> sublist = ticket.getSeatsAllocation().subList(0, countToBeDeleted);
            Set<Ticket> modifiedTicketsSet = this.cancelSeatHelper(ticket, sublist);
            ticket.setTravelersCount(ticket.getTravelersCount() - countToBeDeleted);
            ticket.getSeatsAllocation().removeAll(sublist);

            this.logger.info("THIS TICKET PNR : '{}' TRAVELERS COUNT TO BE REDUCED TO COUNT : {}", ticket.getPNRId(), countToBeDeleted + decreasedWaits);
            System.out.println("waiting queue : " + this.waitingQueue);
            System.out.println("modified set : " + modifiedTicketsSet);
            return modifiedTicketsSet;
        }

        return Set.of();
    }

    public Set<Ticket> bulkCancelSeat (Ticket ticket) {
        Set<Ticket> modifiedTicketsSet = this.cancelSeatHelper (ticket, ticket.getSeatsAllocation());
        this.logger.info("BULK SEAT CANCEL DONE! PNR : {}", ticket.getPNRId());
        return modifiedTicketsSet;
    }

    private Set<Ticket> cancelSeatHelper (Ticket ticket, List<Integer> sublist) {

        Set<Ticket> changedTickets = new HashSet<>();

        sublist.forEach(seatNumber -> {
            Seat<Ticket> currentSeat = this.seats[seatNumber - this.seatStartNumber];
            currentSeat.remove(ticket);
            System.out.println("After removing : " + currentSeat);
            this.waitingQueue.forEach(waitingTicket -> {
                // If current waiting queue ticket is fit into the current seat
                System.out.println("inside the waiting queue loop");
                if (Seat.preAddCheck(currentSeat, waitingTicket)) {
                    System.out.println("inside the condition");
                    currentSeat.add(waitingTicket);
                    waitingTicket.setWaitingCount(waitingTicket.getWaitingCount() - 1);
                    changedTickets.add(waitingTicket);
                    --this.currentWaitingCount;
                    this.logger.info("ONE QUEUED TICKET HAS IT'S SEAT AND PNR : {}, SEAT NO : {}, ", waitingTicket.getPNRId(), currentSeat.getSeatNumber());
                }
                // if the current waiting ticket reaches the waitingCount == 0
                this.waitingQueue.removeIf(currentWaitingTicket -> currentWaitingTicket.equals(waitingTicket) && currentWaitingTicket.getWaitingCount() == 0);
            });
        });

        return changedTickets;
    }

    public String getCoachName() {
        return coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }
}