package com.los.leagueofstats.services.integration.lol.enums;

import com.los.leagueofstats.utils.Region;

/**
 * Региональные хабы Riot API (используются для вызовов матчей, аккаунтов и т.д.).
 */
public enum RiotRegion implements Region {

    /** Европа (EUW, EUNE, TR, RU и т.д.) */
    EUROPE,
    /** Америка (NA, BR, LAN, LAS и т.д.) */
    AMERICAS,
    /** Азия (KR, JP) */
    ASIA,
    /** Специальный регион для киберспорта */
    ESPORTS;

    /** Возвращает строковый идентификатор региона в нижнем регистре */
    public String getId() {
        return this.name().toLowerCase();
    }
}
