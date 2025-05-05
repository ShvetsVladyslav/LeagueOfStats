package com.los.leagueofstats.services.internal.telegram.commands;

import com.los.leagueofstats.services.integration.lol.enums.LolRegion;
import com.los.leagueofstats.services.internal.lol.profile.ProfileComponent;
import com.los.leagueofstats.services.internal.lol.profile.dto.SummonerProfileDto;
import com.los.leagueofstats.services.internal.lol.stats.StatsComponent;
import com.los.leagueofstats.services.internal.lol.stats.dto.DetailedMatchInfoDto;
import com.los.leagueofstats.services.internal.lol.stats.dto.SummonerMatchStatsDto;
import com.los.leagueofstats.services.internal.telegram.enums.TelegramBotCommands;
import com.los.leagueofstats.utils.TelegramMessageUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.los.leagueofstats.utils.RiotUtils.RIOT_ID_PATTERN;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Log4j2
@Component
public class LastMatchesCommandHandler implements BotCommandHandler {

    private final ProfileComponent profileComponent;
    private final StatsComponent statsComponent;

    public LastMatchesCommandHandler(
            ProfileComponent profileComponent,
            StatsComponent statsComponent) {
        this.profileComponent = profileComponent;
        this.statsComponent = statsComponent;
    }

    @Override
    public TelegramBotCommands command() {
        return TelegramBotCommands.LAST_GAMES;
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
            List<DetailedMatchInfoDto> stats = statsComponent.collectLastMatchesDetailedStats(profile.getPuuid());
            if (stats != null) {
                log.info("COLLECTED STATS: " + stats);
                message = formatDetailedMatches(username, tag, stats);
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
     * Формирует Markdown-сообщение по списку матчей.
     *
     * @param username имя игрока
     * @param tag тег игрока
     * @param matches список матчей
     * @return отформатированное сообщение
     */
    public String formatDetailedMatches(String username, String tag, List<DetailedMatchInfoDto> matches) {
        if (matches.isEmpty()) {
            return "*Игры не найдены для " + username + "#" + tag + "*";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("*Последние матчи ").append(username).append("#").append(tag).append("*\n");

        int index = 1;
        for (DetailedMatchInfoDto match : matches) {
            sb.append("\n*").append(index).append(". ").append(match.getChampionName());

            if (match.getRole() != null && !match.getRole().isBlank()) {
                sb.append(" | ").append(capitalize(match.getRole()));
            }

            sb.append(" | ").append(match.getGameMode())
                    .append(" | ").append(match.getWin() ? "Победа" : "Поражение").append("*\n");

            sb.append("KDA: ").append(match.getKills()).append("/")
                    .append(match.getDeaths()).append("/").append(match.getAssists())
                    .append(" | Урон: ").append(match.getDamageDealt())
                    .append(" | Получено: ").append(match.getDamageTaken()).append("\n");

            sb.append("CS: ").append(match.getTotalCs())
                    .append(" (").append(match.getCsPerMinute()).append("/мин)")
                    .append(" | Уровень: ").append(match.getLevel()).append("\n");

            sb.append("Суммонерки: ").append(match.getSummonerSpell1())
                    .append(" + ").append(match.getSummonerSpell2()).append("\n");

            if (match.getItems() != null && !match.getItems().isEmpty()) {
                sb.append("Предметы: ");
                sb.append(match.getItems().stream()
                                .filter(it -> !it.equals("0"))
                                .collect(Collectors.joining(" | ")))
                        .append("\n");
            }

            sb.append("Длительность: ").append(match.getGameDuration());

            if (match.getTeamScore() != null) {
                sb.append(" | Счёт команды: ").append(match.getTeamScore());
            }

            if (Boolean.TRUE.equals(match.getIsMvp())) {
                sb.append(" 🏆");
            }

            sb.append("\n");
            index++;
        }

        return sb.toString();
    }

    private String capitalize(String input) {
        if (input == null || input.isBlank()) return "";
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}