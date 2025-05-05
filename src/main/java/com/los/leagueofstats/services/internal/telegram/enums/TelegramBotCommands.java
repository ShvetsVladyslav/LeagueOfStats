package com.los.leagueofstats.services.internal.telegram.enums;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Список поддерживаемых Telegram-команд для бота.
 */
public enum TelegramBotCommands {

    /** Команда запуска бота (/start) */
    START("/start"),
    /** Получение профиля игрока (/profile <ник#тег>) */
    PROFILE("/profile"),
    /** Получение глобальной статистики (/globalstats <ник#тег>) */
    GLOBAL_STATS("/globalstats"),
    /** Показать общую статистику за всё время (/stats <ник#тег>) */
    STATS("/stats"),
    /**  */
    LAST_GAMES("/lastgames");

    private final String command;

    private TelegramBotCommands(String command) {
        this.command = command;
    }

    /** Возвращает текст команды (например, /start) */
    public String getCommand() {
        return this.command;
    }

    /** Индекс команд по их строковому значению */
    private static final Map<String, TelegramBotCommands> ID_INDEX = Stream.of(TelegramBotCommands.values())
            .collect(ImmutableMap.toImmutableMap(TelegramBotCommands::getCommand, Function.identity()));

    /**
     * Возвращает enum-команду по строковому значению.
     *
     * @param id строка команды (например, "/profile")
     * @return соответствующая команда или null, если не найдена
     */
    @Nullable
    public static TelegramBotCommands getById(@Nullable String id) {
        return ID_INDEX.get(id);
    }
}
