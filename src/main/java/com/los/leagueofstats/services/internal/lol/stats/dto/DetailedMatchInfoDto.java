package com.los.leagueofstats.services.internal.lol.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetailedMatchInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Название чемпиона */
    private String championName;

    /** Роль (если есть) */
    private String role;

    /** Тип игры: ранкед/обычная */
    private String gameMode;

    /** Победа или поражение */
    private Boolean win;

    /** KDA */
    private Integer kills;
    private Integer deaths;
    private Integer assists;

    /** Урон по чемпионам */
    private Integer damageDealt;

    /** Полученный урон */
    private Integer damageTaken;

    /** Общее количество фарма */
    private Integer totalCs;

    /** CS в минуту */
    private Double csPerMinute;

    /** Уровень */
    private Integer level;

    /** Суммонерки */
    private String summonerSpell1;
    private String summonerSpell2;

    /** Список предметов (0–6, + Trinket) */
    private List<String> items;

    /** Длительность игры */
    private String gameDuration;

    /** Командный счёт */
    private String teamScore;

    /** Был ли MVP (если есть логика) */
    private Boolean isMvp;
}