package com.los.leagueofstats.services.integration.lol.enums;

import com.los.leagueofstats.utils.Region;

/**
 * Региональные серверы League of Legends.
 */
public enum LolRegion implements Region {

    /** Россия */
    RU,
    /** Восточная Европа (Nordic & East) */
    EUN1,
    /** Западная Европа (West) */
    EUW1,
    /** Бразилия */
    BR1,
    /** Латинская Америка Север */
    LA1,
    /** Латинская Америка Юг */
    LA2,
    /** Северная Америка */
    NA1,
    /** Океания */
    OC1,
    /** Республика Корея */
    KR,
    /** Турция */
    TR1,
    /** Япония */
    JP1,
    /** Филиппины */
    PH2,
    /** Сингапур, Малайзия, Индонезия */
    SG2,
    /** Таиланд */
    TH2,
    /** Тайвань */
    TW2,
    /** Вьетнам */
    VN2;

    /** Возвращает строковый идентификатор региона в нижнем регистре */
    public String getId() {
        return this.name().toLowerCase();
    }
}
