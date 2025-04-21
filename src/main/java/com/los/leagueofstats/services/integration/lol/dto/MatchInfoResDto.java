package com.los.leagueofstats.services.integration.lol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Информация о матче: игроки, режим, время, длительность и т.д.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchInfoResDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Время создания матча (epoch ms) */
    private Long gameCreation;
    /** Длительность матча в секундах */
    private Long gameDuration;
    /** Время окончания матча (epoch ms) */
    private Long gameEndTimestamp;
    /** Уникальный идентификатор матча */
    private Long gameId;
    /** Название режима игры (например, CLASSIC, ARAM) */
    private String gameMode;
    /** Название матча */
    private String gameName;
    /** Время начала матча (epoch ms) */
    private Long gameStartTimestamp;
    /** Тип игры (например, CUSTOM_GAME, MATCHED_GAME) */
    private String gameType;
    /** Версия клиента, в которой прошёл матч */
    private String gameVersion;
    /** Идентификатор карты (например, 11 — Summoner’s Rift) */
    private Integer mapId;
    /** Список участников матча */
    private List<MatchParticipantDto> participants;
    /** Идентификатор платформы (например, EUW1) */
    private String platformId;
    /** Идентификатор очереди (например, 420 — ранкед соло) */
    private Integer queueId;
    /** Код турнира, если матч был частью турнира */
    private String tournamentCode;
}
