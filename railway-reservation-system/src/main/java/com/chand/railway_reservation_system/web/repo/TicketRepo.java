package com.chand.railway_reservation_system.web.repo;

import com.chand.railway_reservation_system.core.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepo extends JpaRepository<Passenger, String> {

}
