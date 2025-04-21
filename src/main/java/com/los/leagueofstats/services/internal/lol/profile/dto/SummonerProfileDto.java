package com.los.leagueofstats.services.internal.lol.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Профиль призывателя с основными данными и рангами.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SummonerProfileDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Уникальный идентификатор игрока (PUUID) */
    private String puuid;
    /** Имя игрока */
    private String username;
    /** Тег Riot ID */
    private String tag;
    /** Уровень призывателя */
    private String summonerLvl;
    /** Текущий ранг в Solo Queue (например, GOLD II 80 LP) */
    private String currentSoloQRank;
    /** Текущий ранг в Flex 5×5 (например, SILVER IV 43 LP) */
    private String currentFlexRank;
}