package com.los.leagueofstats.services.integration.riot.enums;

public enum LeagueRankTier {
    IRON,
    BRONZE,
    SILVER,
    GOLD,
    PLATINUM,
    EMERALD,
    DIAMOND;


    public String getId() {
        return this.name();
    }
}
