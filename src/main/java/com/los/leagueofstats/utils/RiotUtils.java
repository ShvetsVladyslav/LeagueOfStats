package com.los.leagueofstats.utils;

import java.util.regex.Pattern;

/**
 * Утилита для данных Riot.
 */
public final class RiotUtils {
    public static final String RIOT_ID_REGEX = "^(?<username>[\\p{L}\\d_]+(?:\\s+[\\p{L}\\d_]+)*)#(?<tag>[A-Za-z0-9]{2,5})$";
    public static final Pattern RIOT_ID_PATTERN = Pattern.compile(RIOT_ID_REGEX);
}
