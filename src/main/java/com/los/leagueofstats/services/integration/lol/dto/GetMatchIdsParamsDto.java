package com.los.leagueofstats.services.integration.lol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.Serial;
import java.io.Serializable;

import static com.los.leagueofstats.utils.CollectionUtilities.putIfNotBlank;
import static com.los.leagueofstats.utils.CollectionUtilities.putIfNotNull;

/**
 * DTO с параметрами для запроса списка matchId от Riot API.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetMatchIdsParamsDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** PUUID игрока */
    private String puuid;
    /** Максимум матчей (до 2000) */
    private Integer maxMatches;
    /** ID очереди (420 - SoloQ, 440 - Flex и т.д.) */
    private Integer queueId;
    /** Начало диапазона времени (timestamp в секундах) */
    private Long startTime;
    /** Конец диапазона времени (timestamp в секундах) */
    private Long endTime;
    /** Старт запроса для пагинации */
    private Integer start;
    /** Кол-во матчей за один запрос */
    private Integer count;

    /**
     * Преобразует DTO в Map с query-параметрами.
     */
    public MultiValueMap<String, String> toQueryParams() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        putIfNotNull(params, "start", start);
        putIfNotNull(params, "count", count);
        putIfNotNull(params, "queue", queueId);
        putIfNotNull(params, "startTime", startTime);
        putIfNotNull(params, "endTime", endTime);

        return params;
    }
}
