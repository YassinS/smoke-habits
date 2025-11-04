package com.sassi.smokehabits.repository;

import com.sassi.smokehabits.dto.analytics.ContextAnalyticsDto;
import com.sassi.smokehabits.entity.CigaretteEntry;
import com.sassi.smokehabits.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CigaretteEntryRepository extends JpaRepository<CigaretteEntry, Long> {

    List<CigaretteEntry> findAllByUserOrderByTimestampDesc(User user);

    @Query("""
    SELECT new com.sassi.smokehabits.dto.analytics.ContextAnalyticsDto(
        COALESCE(ctx.context, 'No Context'),
        COUNT(c),
        AVG(c.cravingLevel)
    )
    FROM CigaretteEntry c
    LEFT JOIN c.context ctx
    WHERE c.user.id = :userId
    GROUP BY ctx.context
    ORDER BY COUNT(c) DESC
""")
    List<ContextAnalyticsDto> findContextAnalyticsByUserId(UUID userId);
}

