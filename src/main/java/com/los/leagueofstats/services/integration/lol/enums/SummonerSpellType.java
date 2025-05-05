package com.los.leagueofstats.services.integration.lol.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SummonerSpellType {
    CLEANSE(1, "Cleanse"),
    EXHAUST(3, "Exhaust"),
    FLASH(4, "Flash"),
    GHOST(6, "Ghost"),
    HEAL(7, "Heal"),
    SMITE(11, "Smite"),
    TELEPORT(12, "Teleport"),
    CLARITY(13, "Clarity"),
    IGNITE(14, "Ignite"),
    BARRIER(21, "Barrier"),
    TO_THE_KING(30, "To the King!"),
    PORO_TOSS(31, "Poro Toss"),
    UNKNOWN(-1, "Unknown");

    private final int id;
    private final String name;

    SummonerSpellType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Получает тип саммонерки по ID.
     *
     * @param id id заклинания
     * @return тип заклинания
     */
    public static SummonerSpellType fromId(int id) {
        return Arrays.stream(values())
                .filter(spell -> spell.id == id)
                .findFirst()
                .orElse(UNKNOWN);
    }
}
