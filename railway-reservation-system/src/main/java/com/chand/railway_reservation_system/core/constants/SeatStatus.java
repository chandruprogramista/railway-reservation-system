    package com.chand.railway_reservation_system.core.constants;

    import org.springframework.http.HttpStatus;

    public enum SeatStatus {

        FETCH("SEATS ARE FETCHED"),
        BOOK("SEATS ARE BOOKED"),
        CANCEL("SEATS ARE CANCELED"),
        BULK_CANCEL("TICKET IS CANCELED"),
        NOT_PROCESSED("NOT PROCESSED, THERE IS SOME ISSUE"),
        VALIDATION_ERROR("NOT PROCESSED, THERE IS SOME ISSUE"),
        EMPTY("IT'S EMPTY");

        public final String status;

        SeatStatus (String status) {
            this.status = status;
        }

        public String status() {
            return status;
        }
    }
