package com.los.leagueofstats.services.integration.lol.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO для ответа с информацией о лиге игрока.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeagueEntriesResDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Уникальный идентификатор лиги */
    private String leagueId;
    /** Тип рейтинговой очереди (например, RANKED_SOLO_5x5) */
    private String queueType;
    /** Уровень игрока (например, GOLD, PLATINUM) */
    private String tier;
    /** Дивизион внутри уровня (например, I, II) */
    private String rank;
    /** Уникальный идентификатор игрока (PUUID) */
    private String puuid;
    /** Количество рейтинговых очков (LP) */
    private Integer leaguePoints;
    /** Количество побед в этом режиме */
    private Integer wins;
    /** Количество поражений в этом режиме */
    private Integer losses;
    /** Флаг, что игрок давно играет в этой лиге */
    private Boolean veteran;
    /** Флаг, что игрок неактивен */
    private Boolean inactive;
    /** Флаг, что игрок новичок или недавно начал играть */
    private Boolean fleshBlood;
    /** Флаг, что у игрока серия побед */
    private Boolean hotStreak;
}
