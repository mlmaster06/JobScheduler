package com.example.demo.jobscheduler;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScheduledMessageRepository extends JpaRepository<ScheduledMessage, UUID> {

    // Fetch pending messages scheduled for execution
    List<ScheduledMessage> findByStatusAndScheduledTimeBefore(String status, LocalDateTime time);

    Optional<ScheduledMessage> findByScheduledTime(LocalDateTime scheduledTime);
}

//asd
