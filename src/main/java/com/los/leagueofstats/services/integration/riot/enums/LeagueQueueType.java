package com.los.leagueofstats.services.integration.riot.enums;

public enum LeagueQueueType {
    SOLOQ("RANKED_SOLO_5x5"),
    FLEX("RANKED_FLEX_SR");

    private final String id;

    private LeagueQueueType(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}
