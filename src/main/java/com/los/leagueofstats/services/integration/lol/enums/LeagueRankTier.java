package com.los.leagueofstats.services.integration.lol.enums;

/**
 * Уровни (тиеры) ранга в League of Legends.
 */
public enum LeagueRankTier {

    /** Железо */
    IRON,
    /** Бронза */
    BRONZE,
    /** Серебро */
    SILVER,
    /** Золото */
    GOLD,
    /** Платина */
    PLATINUM,
    /** Изумруд */
    EMERALD,
    /** Алмаз */
    DIAMOND,
    /** Мастер */
    MASTER,
    /** Грандмастер */
    GRANDMASTER,
    /** Претендент */
    CHALLENGER;

    /** Возвращает строковый идентификатор тира */
    public String getId() {
        return this.name();
    }
}
