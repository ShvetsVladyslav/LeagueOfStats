package com.los.leagueofstats.services.internal.telegram.commands;

import com.los.leagueofstats.services.integration.lol.enums.LolRegion;
import com.los.leagueofstats.services.internal.lol.profile.ProfileComponent;
import com.los.leagueofstats.services.internal.lol.profile.dto.SummonerProfileDto;
import com.los.leagueofstats.services.internal.lol.stats.StatsComponent;
import com.los.leagueofstats.services.internal.lol.stats.dto.SummonerMatchStatsDto;
import com.los.leagueofstats.services.internal.telegram.enums.TelegramBotCommands;
import com.los.leagueofstats.utils.TelegramMessageUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.regex.Matcher;

import static com.los.leagueofstats.utils.RiotUtils.RIOT_ID_PATTERN;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Log4j2
@Component
public class StatisticCommandHandler implements BotCommandHandler {

    /** Компонент для получения профиля игрока */
    private final ProfileComponent profileComponent;
    /** Компонент для сбора матчевой статистики */
    private final StatsComponent statsComponent;

    public StatisticCommandHandler(
            ProfileComponent profileComponent,
            StatsComponent statsComponent) {
        this.profileComponent = profileComponent;
        this.statsComponent = statsComponent;
    }

    @Override
    public TelegramBotCommands command() {
        return TelegramBotCommands.STATS;
    }

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
            SummonerMatchStatsDto stats = statsComponent.collectSeasonalStats(profile.getPuuid());
            if (stats != null) {
                log.info("COLLECTED STATS: " + stats);
                message = formatSeasonStats(username, tag, stats);
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

    private String formatSeasonStats(String username, String tag, SummonerMatchStatsDto dto) {
        return String.format("""
            ✅ *%s#%s — Общая статистика за сезон*

            🔹 Общие игры: %d
            ✅ Побед: %d
            ❌ Поражений: %d
            📊 Винрейт: %.1f%%

            🏆 *Рейтинговая соло (SoloQ)*
            🔹 Игры: %d
            ✅ Побед: %d
            ❌ Поражений: %d
            📊 Винрейт: %.1f%%

            🛡️ *Рейтинговая флекс (Flex 5v5)*
            🔹 Игры: %d
            ✅ Побед: %d
            ❌ Поражений: %d
            📊 Винрейт: %.1f%%
            """,
                username, tag,

                dto.getTotal().getTotalGames(),
                dto.getTotal().getWins(),
                dto.getTotal().getLosses(),
                dto.getTotal().getWinRate(),

                dto.getRankedSolo().getTotalGames(),
                dto.getRankedSolo().getWins(),
                dto.getRankedSolo().getLosses(),
                dto.getRankedSolo().getWinRate(),

                dto.getRankedFlex().getTotalGames(),
                dto.getRankedFlex().getWins(),
                dto.getRankedFlex().getLosses(),
                dto.getRankedFlex().getWinRate()
        );
    }
}
