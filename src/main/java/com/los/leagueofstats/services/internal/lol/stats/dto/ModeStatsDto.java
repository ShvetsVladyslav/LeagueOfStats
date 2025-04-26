package com.los.leagueofstats.services.internal.lol.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Статистика по одному игровому режиму.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModeStatsDto implements Serializable {

    /** Версия сериализации. */
    private static final long serialVersionUID = 1L;

    /** Количество игр. */
    private Integer totalGames;

    /** Количество побед. */
    private Integer wins;

    /** Количество поражений. */
    private Integer losses;

    /** Винрейт в процентах. */
    private Double winRate;
}