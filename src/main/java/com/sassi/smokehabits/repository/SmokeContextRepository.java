package com.sassi.smokehabits.repository;

import com.sassi.smokehabits.entity.SmokeContext;
import com.sassi.smokehabits.entity.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface SmokeContextRepository
    extends CrudRepository<SmokeContext, UUID> {
    List<SmokeContext> findAllByUser(User user);

    @Query(
        "SELECT sc FROM SmokeContext sc WHERE sc.id = :id AND sc.user.id = :userId"
    )
    SmokeContext findSmokeContextByIdAndUser(
        @Param("id") UUID id,
        @Param("userId") UUID userId
    );

    @Query(
        "SELECT sc FROM SmokeContext sc WHERE sc.id = :id AND sc.user = :user"
    )
    SmokeContext findContextByIdAndUser(
        @Param("id") UUID id,
        @Param("user") User user
    );
}
