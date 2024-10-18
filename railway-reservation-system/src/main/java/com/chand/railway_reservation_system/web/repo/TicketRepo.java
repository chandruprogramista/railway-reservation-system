package com.chand.railway_reservation_system.web.repo;

import com.chand.railway_reservation_system.core.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepo extends JpaRepository<Ticket, String> {

}
