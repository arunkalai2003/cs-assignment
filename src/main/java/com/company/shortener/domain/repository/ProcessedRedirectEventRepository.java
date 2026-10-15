package com.company.shortener.domain.repository;

import com.company.shortener.domain.model.ProcessedRedirectEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ProcessedRedirectEventRepository extends JpaRepository<ProcessedRedirectEvent, Long> {

    boolean existsByEventId(String eventId);

    @Modifying
    @Query("delete from ProcessedRedirectEvent e where e.processedAt < :cutoff")
    int deleteByProcessedAtBefore(@Param("cutoff") LocalDateTime cutoff);
}
