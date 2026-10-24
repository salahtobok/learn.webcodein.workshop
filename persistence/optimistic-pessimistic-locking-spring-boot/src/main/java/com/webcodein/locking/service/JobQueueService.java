package com.webcodein.locking.service;

import com.webcodein.locking.entity.JobTicket;
import com.webcodein.locking.repository.JobTicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class JobQueueService {

    private final JobTicketRepository jobTicketRepository;

    public JobQueueService(JobTicketRepository jobTicketRepository) {
        this.jobTicketRepository = jobTicketRepository;
    }

    @Transactional
    public Optional<JobTicket> claimAndProcessNextJob() {
        Optional<JobTicket> pendingTicket = jobTicketRepository.findNextJobWithSkipLocked("PENDING");
        if (pendingTicket.isPresent()) {
            JobTicket ticket = pendingTicket.get();
            ticket.setStatus("COMPLETED");
            return Optional.of(jobTicketRepository.saveAndFlush(ticket));
        }
        return Optional.empty();
    }
}
