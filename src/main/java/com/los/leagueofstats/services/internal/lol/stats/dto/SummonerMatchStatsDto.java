package com.los.leagueofstats.services.internal.lol.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Сезонная статистика по игроку в разрезе режимов.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SummonerMatchStatsDto implements Serializable {

    /** Версия сериализации. */
    private static final long serialVersionUID = 1L;

    /** Общая статистика за сезон по всем режимам. */
    private ModeStatsDto total;

    /** Статистика за сезон в рейтинговом соло режиме. */
    private ModeStatsDto rankedSolo;

    /** Статистика за сезон в рейтинговом флекс режиме. */
    private ModeStatsDto rankedFlex;
}
