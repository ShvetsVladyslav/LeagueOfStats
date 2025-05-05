package com.los.leagueofstats.services.integration.lol.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * Перечисление всех типов игровых очередей в League of Legends.
 */
@Getter
@AllArgsConstructor
public enum RiotQueueType {

    /** Рейтинговая игра — Solo/Duo (5v5). */
    RANKED_SOLO_5x5(420, "Solo/Duo"),
    /** Рейтинговая игра — Flex 5v5. */
    RANKED_FLEX_SR(440, "Flex"),
    /** Обычная игра — Draft Pick (5v5). */
    NORMAL_DRAFT_PICK(400, "Draft Pick"),
    /** Обычная игра — Blind Pick (5v5). */
    NORMAL_BLIND_PICK(430, "Blind Pick"),
    /** ARAM — Случайный выбор чемпионов (5v5). */
    ARAM(450, "ARAM"),
    /** Clash — Турнирные игры (5v5). */
    CLASH(700, "Clash"),
    /** URF — Ультра быстрая потасовка. */
    URF(1020, "URF"),
    /** One for All — Один за всех. */
    ONE_FOR_ALL(1010, "One for All"),
    /** Arena — режим Арены 2v2v2v2. */
    ARENA(1700, "Arena"),
    /** Co-op vs AI — Intro (боты). */
    AI_INTRO(830, "Co-op vs AI — Intro"),
    /** Co-op vs AI — Beginner (боты). */
    AI_BEGINNER(840, "Co-op vs AI — Beginner"),
    /** Co-op vs AI — Intermediate (боты). */
    AI_INTERMEDIATE(850, "Co-op vs AI — Intermediate"),
    /** ARURF — Случайный URF. */
    ARURF(900, "ARURF"),
    /** Nexus Blitz — Экспериментальный режим. */
    NEXUS_BLITZ(1200, "Nexus Blitz"),
    /** Старый ивент — Blood Moon. */
    BLOOD_MOON(600, "Blood Moon"),
    /** Старый ивент — PROJECT: Hunters. */
    PROJECT_HUNTERS(2020, "PROJECT: Hunters"),
    /** Старый ивент — Overcharge. */
    OVERCHARGE(2010, "Overcharge");

    /** ID очереди. */
    private final Integer queueId;
    /** Название очереди */
    private final String queueName;

    /**
     * Получает тип очереди по ID.
     *
     * @param id id очереди
     * @return тип очереди
     */
    @Nullable
    public static RiotQueueType fromId(int id) {
        return Arrays.stream(values())
                .filter(queueType -> queueType.queueId == id)
                .findFirst()
                .orElse(null);
    }
}