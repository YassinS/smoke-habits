package com.sassi.smokehabits.repository;

import com.sassi.smokehabits.entity.CigaretteEntry;
import com.sassi.smokehabits.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CigaretteEntryRepository extends JpaRepository<CigaretteEntry, Long> {

    List<CigaretteEntry> findAllByUserOrderByTimestampDesc(User user);
}

