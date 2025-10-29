package com.sassi.smokehabits.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cigarette_entries")
public class CigaretteEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private LocalDateTime timestamp;

    private int cravingLevel;

    public CigaretteEntry() {}

    public CigaretteEntry(User user, LocalDateTime timestamp, int cravingLevel) {
        this.user = user;
        this.timestamp = timestamp;
        this.cravingLevel = cravingLevel;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getCravingLevel() {
        return cravingLevel;
    }

    public void setCravingLevel(int cravingLevel) {
        this.cravingLevel = cravingLevel;
    }
}
