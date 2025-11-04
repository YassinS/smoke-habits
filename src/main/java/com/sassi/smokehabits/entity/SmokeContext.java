package com.sassi.smokehabits.entity;

import com.sassi.smokehabits.dto.response.SmokeContextResponse;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "smoke_context")
public class SmokeContext {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    private String context;

    private String colorUI;

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getColorUI() {
        return colorUI;
    }

    public void setColorUI(String colorUI) {
        this.colorUI = colorUI;
    }

    public SmokeContextResponse toSmokeContextResponse() {
        return new SmokeContextResponse(id, context, colorUI);
    }
}
