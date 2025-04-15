package com.los.leagueofstats.services.integration.riot.enums;

import com.los.leagueofstats.utils.Region;

public enum RiotRegion implements Region {
    EUROPE,
    AMERICAS,
    ASIA,
    ESPORTS;

    public String getId() {
        return this.name().toLowerCase();
    }
}
