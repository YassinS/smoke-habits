package com.sassi.smokehabits.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "cigarette_entries")
public class CigaretteEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private Instant timestamp;

    private int cravingLevel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "context_id")
    private SmokeContext context;

    public CigaretteEntry() {}

    public CigaretteEntry(User user, int cravingLevel) {
        this.user = user;
        this.timestamp = Instant.now();
        this.cravingLevel = cravingLevel;
    }

    public CigaretteEntry(User user, int cravingLevel, SmokeContext context) {
        this.user = user;
        this.timestamp = Instant.now();
        this.cravingLevel = cravingLevel;
        this.context = context;
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

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public int getCravingLevel() {
        return cravingLevel;
    }

    public void setCravingLevel(int cravingLevel) {
        this.cravingLevel = cravingLevel;
    }

    public SmokeContext getContext() {
        return context;
    }

    public void setContext(SmokeContext context) {
        this.context = context;
    }
}
