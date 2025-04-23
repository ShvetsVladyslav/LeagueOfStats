package com.los.leagueofstats.services.internal.telegram.commands;

import com.los.leagueofstats.services.integration.lol.enums.LolRegion;
import com.los.leagueofstats.services.integration.lol.enums.RiotRegion;
import com.los.leagueofstats.services.internal.lol.profile.ProfileComponent;
import com.los.leagueofstats.services.internal.lol.profile.dto.SummonerProfileDto;
import com.los.leagueofstats.services.internal.lol.stats.StatsComponent;
import com.los.leagueofstats.services.internal.lol.stats.dto.MatchStatsDto;
import com.los.leagueofstats.services.internal.telegram.enums.TelegramBotCommands;
import com.los.leagueofstats.utils.TelegramMessageUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.regex.Matcher;

import static com.los.leagueofstats.utils.RiotUtils.RIOT_ID_PATTERN;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Обработка команды /stats.
 * Показывает глобальную статистику игрока: общее число матчей, побед, поражений и винрейт.
 * Делится по режимам: все игры, SoloQ и Flex.
 */
@Log4j2
@Component
public class StatisticsCommandHandler implements BotCommandHandler {

    /** Компонент для получения профиля игрока */
    private final ProfileComponent profileComponent;
    /** Компонент для сбора матчевой статистики */
    private final StatsComponent statsComponent;

    public StatisticsCommandHandler(
            ProfileComponent profileComponent,
            StatsComponent statsComponent) {
        this.profileComponent = profileComponent;
        this.statsComponent = statsComponent;
    }

    /** Возвращает команду, которую обрабатывает — /stats */
    @Override
    public TelegramBotCommands command() {
        return TelegramBotCommands.GLOBAL_STATS;
    }

    /**
     * Обрабатывает команду /stats:
     * — Проверяет формат ввода;
     * — Получает профиль игрока;
     * — Считает статистику по матчам;
     * — Возвращает сообщение с итогами или ошибку.
     */
    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();

        String text = update.getMessage().getText().trim();
        String userInput = text.replace(command().getCommand(), "").trim();

        if (isBlank(userInput)) {
            return SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text(TelegramMessageUtils.commandRequiresArguments(command().getCommand()))
                    .parseMode("Markdown")
                    .build();
        }

        Matcher riotMatcher = RIOT_ID_PATTERN.matcher(userInput);
        if (!riotMatcher.matches()) {
            log.warn("USER INPUT: " + userInput);
            return SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text(TelegramMessageUtils.invalidRiotIdFormat(userInput))
                    .parseMode("Markdown")
                    .build();
        }

        String username = riotMatcher.group("username");
        String tag = riotMatcher.group("tag");

        String message;
        SummonerProfileDto profile = profileComponent.getSummonerProfileCacheable(username, tag, LolRegion.RU);
        if (profile != null) {
            MatchStatsDto stats = statsComponent.collectStats(profile.getPuuid());
            if (stats != null) {
                log.info("COLLECTED STATS: " + stats);
                message = buildStatsMessage(stats);
            } else {
                message = TelegramMessageUtils.noMatchData();
            }
        } else {
            message = TelegramMessageUtils.summonerNotFound(userInput);
        }

        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(message)
                .parseMode("Markdown")
                .build();
    }

    /**
     * Формирует текст статистики на основе данных матчей.
     *
     * @param stats объект с результатами
     * @return отформатированное сообщение
     */
    private String buildStatsMessage(MatchStatsDto stats) {
        return String.format("""
                            📊 Глобальная статистика за максимально возможное количество матчей:
                            Матчей: %d
                            Победы: %d
                            Поражения: %d
                            Винрейт: %.1f%%

                            🏆 Solo Queue:
                            Матчей: %d
                            Победы: %d
                            Поражения: %d
                            Винрейт: %.1f%%

                            👥 Flex:
                            Матчей: %d
                            Победы: %d
                            Поражения: %d
                            Винрейт: %.1f%%
                            """,
                stats.getTotalMatches(),
                stats.getTotalWins(),
                stats.getTotalLosses(),
                stats.getTotalWinRate(),

                stats.getSoloMatches(),
                stats.getSoloWins(),
                stats.getSoloLosses(),
                stats.getSoloWinRate(),

                stats.getFlexMatches(),
                stats.getFlexWins(),
                stats.getFlexLosses(),
                stats.getFlexWinRate()
        );
    }
}