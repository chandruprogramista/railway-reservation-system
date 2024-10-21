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

    public static String START;
    public static String END;

    @PostConstruct
    public void construct () {
        START = this.start;
        END = this.end;
        logger.info("start : {}, end : {}", START, END);
    }
}
