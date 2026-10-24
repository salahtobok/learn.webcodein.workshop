package com.webcodein.locking.repository;

import com.webcodein.locking.entity.JobTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobTicketRepository extends JpaRepository<JobTicket, Long> {

    @Query(value = "SELECT * FROM job_tickets WHERE status = :status ORDER BY id ASC LIMIT 1 FOR UPDATE SKIP LOCKED", nativeQuery = true)
    Optional<JobTicket> findNextJobWithSkipLocked(@Param("status") String status);

    List<JobTicket> findByStatus(String status);
}
