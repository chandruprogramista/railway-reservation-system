package com.chand.railway_reservation_system.core.validator;

import com.chand.railway_reservation_system.core.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Validator {

    public static Logger logger = LoggerFactory.getLogger(Validator.class);

    public static final String NAME_PATTERN = "^[a-zA-Z\\s]{2,50}$";

    public static <T> boolean genericValidator (T element, Predicate<T> predicate, Optional<String> message) {
        boolean tempVar = predicate.test(element);
        if (!tempVar && message.isPresent()) logger.info(message.get());
        return tempVar;
    }

    public static boolean nameValidator (String name, String message) {
        boolean tempVar = name != null && name.matches(NAME_PATTERN);
        if (!tempVar) logger.info(message);
        return tempVar;
    }

    public static BiPredicate<String, String> sourceAndDestinationValidator () {
        return (s, d) -> d.charAt(0) > s.charAt(0) && inTheRequiredRange(s) && inTheRequiredRange(d);
    }

    private static boolean inTheRequiredRange (String rangeString) {
        return rangeString.charAt(0) >= Constants.START.charAt(0) && rangeString.charAt(0) <= Constants.END.charAt(0);
    }
}
