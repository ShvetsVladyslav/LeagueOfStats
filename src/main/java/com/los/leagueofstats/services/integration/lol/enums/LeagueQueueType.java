package com.los.leagueofstats.services.integration.lol.enums;

/**
 * Типы рейтинговых очередей в League of Legends.
 */
public enum LeagueQueueType {

    /** Соло рейтинг (одиночные игры) */
    SOLOQ("RANKED_SOLO_5x5", 420),
    /** Флекс рейтинг (командные игры) */
    FLEX("RANKED_FLEX_SR", 440);

    private final String id;
    private final int riotId;

    private LeagueQueueType(String id, int riotId) {
        this.id = id;
        this.riotId = riotId;
    }

    /** Возвращает строковый идентификатор очереди */
    public String getId() {
        return this.id;
    }

    public Integer getRiotId() {
        return this.riotId;
    }
}
