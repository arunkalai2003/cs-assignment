package com.company.shortener.domain.repository;

import com.company.shortener.domain.model.ShortUrlAnalyticsSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ShortUrlAnalyticsSummaryRepository extends JpaRepository<ShortUrlAnalyticsSummary, Long> {

    Optional<ShortUrlAnalyticsSummary> findByShortUrlShortCode(String shortCode);

    Optional<ShortUrlAnalyticsSummary> findByShortUrlId(Long shortUrlId);

    @Modifying
    @Query("delete from ShortUrlAnalyticsSummary s where s.lastUpdated < :cutoff")
    int deleteByLastUpdatedBefore(@Param("cutoff") LocalDateTime cutoff);
}
