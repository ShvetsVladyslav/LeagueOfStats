package com.los.leagueofstats.services.internal.lol.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Статистика матчей игрока по всем режимам:
 * общая, SoloQ, Flex. Используется для отображения пользователю.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchStatsDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Общее количество матчей (все режимы) */
    private Integer totalMatches;
    /** Общее количество побед (все режимы) */
    private Integer totalWins;
    /** Общее количество поражений (все режимы) */
    private Integer totalLosses;
    /** Общий винрейт (%) */
    private Double totalWinRate;
    /** Кол-во матчей в Solo Queue */
    private Integer soloMatches;
    /** Победы в Solo Queue */
    private Integer soloWins;
    /** Поражения в Solo Queue */
    private Integer soloLosses;
    /** Винрейт в Solo Queue */
    private Double soloWinRate;
    /** Кол-во матчей в Flex */
    private Integer flexMatches;
    /** Победы в Flex */
    private Integer flexWins;
    /** Поражения в Flex */
    private Integer flexLosses;
    /** Винрейт в Flex */
    private Double flexWinRate;
}
