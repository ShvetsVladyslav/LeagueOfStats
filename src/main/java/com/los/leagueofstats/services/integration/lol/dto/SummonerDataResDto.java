package com.los.leagueofstats.services.integration.lol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Ответ Riot API с данными призывателя.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SummonerDataResDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Внутренний ID призывателя */
    private String id;
    /** Account ID (устаревший идентификатор аккаунта) */
    private String accountId;
    /** Уникальный идентификатор игрока (PUUID) */
    private String puuid;
    /** ID иконки профиля */
    private Integer profileIconId;
    /** Дата последнего изменения (epoch ms) */
    private Long revisionDate;
    /** Уровень призывателя */
    private Integer summonerLevel;
}
