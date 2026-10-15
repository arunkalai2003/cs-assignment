package com.company.shortener.domain.repository;

import com.company.shortener.domain.model.ShortUrlInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShortUrlRepository extends JpaRepository<ShortUrlInfo, Long> {

    Optional<ShortUrlInfo> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    Optional<ShortUrlInfo> findByShortCodeAndActiveTrue(String shortCode);
}
