package com.los.leagueofstats.services.integration.lol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Ответ Riot API на запрос матча.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RiotMatchResDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Метаданные матча */
    private MatchMetadataResDto metadata;
    /** Информация о матче */
    private MatchInfoResDto info;
}
