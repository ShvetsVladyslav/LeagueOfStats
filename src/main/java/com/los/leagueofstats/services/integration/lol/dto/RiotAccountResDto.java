package com.los.leagueofstats.services.integration.lol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Ответ Riot API с информацией об аккаунте по Riot ID.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RiotAccountResDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Уникальный идентификатор игрока (PUUID) */
    private String puuid;
    /** Имя игрока в Riot (например, Министр Бота) */
    private String gameName;
    /** Тег Riot ID (например, baddy) */
    private String tagLine;
}
