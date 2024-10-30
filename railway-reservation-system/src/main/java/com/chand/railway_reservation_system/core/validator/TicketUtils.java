package com.chand.railway_reservation_system.core.validator;

import com.chand.railway_reservation_system.core.constants.SeatStatus;
import com.chand.railway_reservation_system.core.entity.Passenger;
import com.chand.railway_reservation_system.core.entity.Ticket;
import com.chand.railway_reservation_system.core.manager.SeatsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TicketUtils {

    private static SeatsManager seatsManager;

    private static int maxPeopleForSinglePNR;

    private static int minPeopleForSinglePNR;

    private static String source;

    private static String destination;

    @Autowired
    public TicketUtils (SeatsManager seatsManager, @Value("${train.start}") String source, @Value("${train.end}") String destination, @Value("${train.count.max-passengers-for-single-booking}") int maxCount, @Value("${train.count.min-passengers-for-single-booking}") int minCount) {
        TicketUtils.seatsManager = seatsManager;
        TicketUtils.source = source;
        TicketUtils.destination = destination;
        TicketUtils.maxPeopleForSinglePNR = maxCount;
        TicketUtils.minPeopleForSinglePNR = minCount;
    }

    public static Optional<List<Integer>> getSeats(Passenger passenger) {
        // test for max count validation
        if (!Validator.genericValidator(
                passenger,
                testPassenger -> testPassenger.getTravelersCount() <= maxPeopleForSinglePNR && testPassenger.getTravelersCount() >= minPeopleForSinglePNR,
                Optional.of(String.format("PASSENGER COUNT FOR A SINGLE PNR IS TOO HIGH OR TOO LOW `HINT PASSENGER 'MAX COUNT : %s' AND 'MIN COUNT : %s'`", maxPeopleForSinglePNR, minPeopleForSinglePNR))
        ) || !Validator.nameValidator(
                passenger.getName(),
                "YOUR NAME DOES NOT MEET THE CERTAIN CRITERIA"
        ) || !Validator.genericValidator(
                passenger,
                testPassenger -> testPassenger.getSource().compareTo(source) >= 0 && testPassenger.getSource().compareTo(destination) < 0,
                Optional.of(String.format("SOURCE IS MUST BE IN THE REQUIRED RANGE `AND THE RANGE IS SOURCE : '%s', DESTINATION : '%s'`", source, destination))
        ) || !Validator.genericValidator(
                passenger,
                testPassenger -> testPassenger.getDestination().compareTo(source) > 0 && testPassenger.getDestination().compareTo(destination) <= 0,
                Optional.of(String.format("DESTINATION IS MUST BE IN THE REQUIRED RANGE `AND THE RANGE IS SOURCE : '%s', DESTINATION : '%s'`", source, destination))
        ) || !Validator.genericValidator(
                passenger,
                testPassenger -> testPassenger.getSource().compareTo(testPassenger.getDestination()) < 0,
                Optional.of(String.format("SOURCE IS TOO LARGE THAN DESTINATION `SOURCE : '%s' AND DESTINATION : '%s'`", passenger.getSource(), passenger.getDestination()))
        )
        )
            return Optional.empty();

        return Optional.of(seatsManager.getSeats(passenger));
    }

    public static void bookSeats(List<Integer> seatsAllocation, Passenger passenger) {
        Objects.requireNonNull(passenger, "PASSENGER COULDN'T BE 'NULL'");
        Objects.requireNonNull(seatsAllocation, "SEATS ALLOCATION COULDN'T BE 'NULL'");
        seatsManager.bookSeats(seatsAllocation, passenger);
    }

    public static SeatStatus cancelSeats(int countToBeDeleted, Passenger passenger) {
        if (!Validator.genericValidator(
                passenger,
                testPassenger -> testPassenger.getTravelersCount() >= countToBeDeleted && countToBeDeleted >= minPeopleForSinglePNR,
                Optional.of(String.format("COUNT TO BE CANCEL IS TOO LARGE ARE TOO SMALL THAN ACTUAL PASSENGERS COUNT IN THE PNR `COUNT TO BE CANCEL : '%s', ACTUAL PASSENGERS COUNT : '%s'`", countToBeDeleted, passenger.getTravelersCount()))
        )
        )
            return SeatStatus.VALIDATION_ERROR;
        return seatsManager.cancelSeats(countToBeDeleted, passenger, true);
    }

    public static Passenger cancelSeats (Passenger passenger) {
        seatsManager.cancelSeats(passenger);
        return passenger;
    }

    public static Passenger getCurrentState (Passenger passenger) {
        return seatsManager.getCurrentState(passenger);
    }

    public static int getWaitingCount (String s, String d) {
        boolean tempVar1 = s.compareTo(source) >= 0 && s.compareTo(destination) < 0;
        boolean tempVar2 = d.compareTo(source) > 0 && d.compareTo(destination) <= 0;
        boolean tempVar3 = s.compareTo(d) < 0;

        if (tempVar1 && tempVar2 && tempVar3)
            return seatsManager.getWaitingCount(s, d);
        return -1;
    }
}
