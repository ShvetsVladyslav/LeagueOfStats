package com.los.leagueofstats.services.integration.lol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Метаданные матча: id, участники, версия и т.п.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchMetadataResDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Версия данных */
    private String dataVersion;
    /** ID матча */
    private String matchId;
    /** Список участников (PUUID) */
    private List<String> participants;
}
