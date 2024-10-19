package com.chand.railway_reservation_system.core;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constants {

    private final Logger logger = LoggerFactory.getLogger(Constants.class);

    @Value("${train.start}")
    private String start;

    @Value("${train.end}")
    private String end;

    @Value("${train.count.waiting-upper-bound}")
    private int max_wait_count;

    @Value("${train.count.seats}")
    private int max_people_for_single_pnr;

    public static String START;
    public static String END;
    public static int MAX_WAIT_COUNT;
    public static int MAX_PEOPLE_FOR_SINGLE_PNR;

    @PostConstruct
    public void construct () {
        START = this.start;
        END = this.end;
        MAX_WAIT_COUNT = this.max_wait_count;
        MAX_PEOPLE_FOR_SINGLE_PNR = this.max_people_for_single_pnr;
        logger.info("start : {}, end : {}", START, END);
        logger.info("max_wait_counts : {}, max_count_single_pnr : {}", MAX_WAIT_COUNT, MAX_PEOPLE_FOR_SINGLE_PNR);
    }
}
