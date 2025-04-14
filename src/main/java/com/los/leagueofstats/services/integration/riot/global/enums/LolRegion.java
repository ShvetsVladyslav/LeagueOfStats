package com.los.leagueofstats.services.integration.riot.global.enums;

import com.los.leagueofstats.utils.Region;

public enum LolRegion implements Region {

    RU,
    EUN1,
    EUW1,
    BR1;

    public String getId() {
        return this.name().toLowerCase();
    }
}
