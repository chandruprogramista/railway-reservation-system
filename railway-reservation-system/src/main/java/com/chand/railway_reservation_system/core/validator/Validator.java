package com.chand.railway_reservation_system.core.validator;

import com.chand.railway_reservation_system.core.Constants;
import com.chand.railway_reservation_system.core.datastructure.Seat;
import com.chand.railway_reservation_system.core.entity.Ticket;

import java.util.function.BiPredicate;

public class Validator {

    public static BiPredicate<String, String> sourceAndDestinationValidator () {
        return (s, d) -> d.charAt(0) > s.charAt(0) && inTheRequiredRange(s) && inTheRequiredRange(d);
    }

    private static boolean inTheRequiredRange (String rangeString) {
        return rangeString.charAt(0) >= Constants.START.charAt(0) && rangeString.charAt(0) <= Constants.END.charAt(0);
    }

    public static BiPredicate<Ticket, Integer> cancelingCountValidator() {
        return (t, c) -> c <= t.getTravelersCount() && c > 0;
    }

    public static BiPredicate<Seat<Ticket>, Ticket> preAddCheck () {
        return Seat::preAddCheck;
    }
}
