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


    /** Уникальный идентификатор игрока (PUUID) */
    private String puuid;

    /** Имя чемпиона */
    private String championName;

    /** Роль (lane) */
    private String teamPosition;

    /** Победа */
    private Boolean win;

    /** Убийства */
    private Integer kills;

    /** Смерти */
    private Integer deaths;

    /** Помощи */
    private Integer assists;

    /** Урон по чемпионам */
    private Integer totalDamageDealtToChampions;

    /** Полученный урон */
    private Integer totalDamageTaken;

    /** Суммарный фарм: миньоны */
    private Integer totalMinionsKilled;

    /** Суммарный фарм: лес */
    private Integer neutralMinionsKilled;

    /** Уровень чемпиона */
    private Integer champLevel;

    /** Урон по объектам (башни, драконы и т.д.) */
    private Integer damageDealtToObjectives;

    /** Урон по башням */
    private Integer damageDealtToTurrets;

    /** Кол-во поставленных вардов */
    private Integer wardsPlaced;

    /** Кол-во уничтоженных вардов */
    private Integer wardsKilled;

    /** Полученное золото за матч */
    private Integer goldEarned;

    /** ID первого суммонерского заклинания */
    private Integer summoner1Id;

    /** ID второго суммонерского заклинания */
    private Integer summoner2Id;

    /** Предметы */
    private Integer item0;
    private Integer item1;
    private Integer item2;
    private Integer item3;
    private Integer item4;
    private Integer item5;
    private Integer item6;

    /** ID команды (100 или 200) */
    private Integer teamId;
}
