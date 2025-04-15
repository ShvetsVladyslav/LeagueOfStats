package com.los.leagueofstats.services.internal.telegram.enums;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public enum TelegramBotCommands {
    START("/start"),
    PROFILE("/profile");

    private final String command;

    private TelegramBotCommands(String command) {
        this.command = command;
    }

    /**
     * Get id.
     *
     * @return id
     */
    public String getCommand() {
        return this.command;
    }

    /** to get enum value by id */
    private static final Map<String, TelegramBotCommands> ID_INDEX = Stream.of(TelegramBotCommands.values())
            .collect(ImmutableMap.toImmutableMap(TelegramBotCommands::getCommand, Function.identity()));

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
