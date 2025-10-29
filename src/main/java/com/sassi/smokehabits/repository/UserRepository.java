package com.sassi.smokehabits.repository;


import com.sassi.smokehabits.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    User getUserById(UUID id);
}
