package com.company.shortener.domain.repository;

import com.company.shortener.domain.model.ShortUrlGeoData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShortUrlGeoDataRepository extends JpaRepository<ShortUrlGeoData, Long> {

    Optional<ShortUrlGeoData> findByShortUrlIdAndCountryCode(Long shortUrlId, String countryCode);

    List<ShortUrlGeoData> findByShortUrlId(Long shortUrlId);

    @Modifying
    @Query("delete from ShortUrlGeoData g where g.capturedAt < :cutoff")
    int deleteByCapturedAtBefore(@Param("cutoff") LocalDateTime cutoff);
}
