package com.los.leagueofstats.services.internal.telegram.enums;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public enum TelegramBotCommands {
    START("/start");

    private final String id;

    private TelegramBotCommands(String id) {
        this.id = id;
    }

    /**
     * Get id.
     *
     * @return id
     */
    public String getId() {
        return this.id;
    }

    /** to get enum value by id */
    private static final Map<String, TelegramBotCommands> ID_INDEX = Stream.of(TelegramBotCommands.values())
            .collect(ImmutableMap.toImmutableMap(TelegramBotCommands::getId, Function.identity()));

    /**
     * Get enum value by id
     *
     * @param id id
     * @return enum value,
     *         or null if id is unknown
     */
    @Nullable
    public static TelegramBotCommands getById(@Nullable String id) {
        return ID_INDEX.get(id);
    }
}
