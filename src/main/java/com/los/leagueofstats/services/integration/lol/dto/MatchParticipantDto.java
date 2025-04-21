package com.los.leagueofstats.services.integration.lol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Информация об одном участнике матча.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchParticipantDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** PUUID игрока */
    private String puuid;
    /** Победил ли игрок в матче */
    private Boolean win;
    /** ID выбранного чемпиона */
    private Integer championId;
    /** Имя выбранного чемпиона */
    private String championName;
    /** Количество убийств */
    private Integer kills;
    /** Количество смертей */
    private Integer deaths;
    /** Количество ассистов */
    private Integer assists;
    /** Уровень чемпиона в конце матча */
    private Integer champLevel;
    /** Золото, заработанное за матч */
    private Integer goldEarned;
    /** Позиция игрока в команде (например, TOP, JUNGLE) */
    private String teamPosition;
}
