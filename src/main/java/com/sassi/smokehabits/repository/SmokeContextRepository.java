package com.sassi.smokehabits.repository;

import com.sassi.smokehabits.entity.SmokeContext;
import com.sassi.smokehabits.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface SmokeContextRepository extends CrudRepository<SmokeContext, UUID> {
    public List<SmokeContext> findAllByUser(User user);

    public SmokeContext findSmokeContextByIdAndUser(UUID id, User user);
}
