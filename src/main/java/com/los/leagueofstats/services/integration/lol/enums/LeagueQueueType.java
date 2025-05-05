package com.los.leagueofstats.services.integration.lol.enums;

/**
 * Типы рейтинговых очередей в League of Legends.
 */
public enum LeagueQueueType {

    /** Соло рейтинг (одиночные игры) */
    SOLOQ("RANKED_SOLO_5x5"),
    /** Флекс рейтинг (командные игры) */
    FLEX("RANKED_FLEX_SR");

    private final String id;

    private LeagueQueueType(String id) {
        this.id = id;
    }

    /** Возвращает строковый идентификатор очереди */
    public String getId() {
        return this.id;
    }
}
