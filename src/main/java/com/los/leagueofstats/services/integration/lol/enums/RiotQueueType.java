package com.los.leagueofstats.services.integration.lol.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Перечисление всех типов игровых очередей в League of Legends.
 */
@Getter
@AllArgsConstructor
public enum RiotQueueType {

    /** Рейтинговая игра — Solo/Duo (5v5). */
    RANKED_SOLO_5x5(420),
    /** Рейтинговая игра — Flex 5v5. */
    RANKED_FLEX_SR(440),
    /** Обычная игра — Draft Pick (5v5). */
    NORMAL_DRAFT_PICK(400),
    /** Обычная игра — Blind Pick (5v5). */
    NORMAL_BLIND_PICK(430),
    /** ARAM — Случайный выбор чемпионов (5v5). */
    ARAM(450),
    /** Clash — Турнирные игры (5v5). */
    CLASH(700),
    /** URF — Ультра быстрая потасовка. */
    URF(1020),
    /** One for All — Один за всех. */
    ONE_FOR_ALL(1010),
    /** Arena — режим Арены 2v2v2v2. */
    ARENA(1700),
    /** Co-op vs AI — Intro (боты). */
    AI_INTRO(830),
    /** Co-op vs AI — Beginner (боты). */
    AI_BEGINNER(840),
    /** Co-op vs AI — Intermediate (боты). */
    AI_INTERMEDIATE(850),
    /** ARURF — Случайный URF. */
    ARURF(900),
    /** Nexus Blitz — Экспериментальный режим. */
    NEXUS_BLITZ(1200),
    /** Старый ивент — Blood Moon. */
    BLOOD_MOON(600),
    /** Старый ивент — PROJECT: Hunters. */
    PROJECT_HUNTERS(2020),
    /** Старый ивент — Overcharge. */
    OVERCHARGE(2010);

    /** ID очереди. */
    private final Integer queueId;
}