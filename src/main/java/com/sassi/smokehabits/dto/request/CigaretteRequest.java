package com.sassi.smokehabits.dto.request;

public class CigaretteRequest {
    private Integer cravingLevel = null;

    public CigaretteRequest(Integer cravingLevel) {
        this.cravingLevel = cravingLevel;
    }

    public CigaretteRequest() {
    }

    public Integer getCravingLevel() {
        return cravingLevel;
    }

    public void setCravingLevel(Integer cravingLevel) {
        this.cravingLevel = cravingLevel;
    }
}
