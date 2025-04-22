package com.los.leagueofstats.services.internal.telegram.commands;

import com.los.leagueofstats.services.integration.lol.enums.LolRegion;
import com.los.leagueofstats.services.internal.lol.profile.ProfileComponent;
import com.los.leagueofstats.services.internal.lol.profile.dto.SummonerProfileDto;
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
 * Обработка команды /profile.
 * Показывает краткий профиль игрока по Riot ID (ник#тег).
 */
@Log4j2
@Component
public class ProfileCommandHandler implements BotCommandHandler {

    /** Компонент для получения профиля игрока */
    private final ProfileComponent profileComponent;

    public ProfileCommandHandler(ProfileComponent profileComponent) {
        this.profileComponent = profileComponent;
    }

    /** Возвращает команду, которую обрабатывает — /profile */
    @Override
    public TelegramBotCommands command() {
        return TelegramBotCommands.PROFILE;
    }

    /**
     * Обработка команды:
     * — Проверяет формат ввода;
     * — Получает профиль призывателя;
     * — Возвращает информацию или сообщение об ошибке.
     */
    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();

        String text = update.getMessage().getText().trim();
        String userInput = text.replace(command().getCommand(), "").trim();

        if (isBlank(userInput)) {
            log.warn("USER INPUT IS EMPTY!");
            return SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text(TelegramMessageUtils.commandRequiresArguments(command().getCommand()))
                    .parseMode("Markdown")
                    .build();
        }

        Matcher riotMatcher = RIOT_ID_PATTERN.matcher(userInput);
        if (!riotMatcher.matches()) {
            log.warn("USER IS NOT MATCH RIOT ID FORMAT: " + userInput);
            return SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text(TelegramMessageUtils.invalidRiotIdFormat(userInput))
                    .parseMode("Markdown")
                    .build();
        }

        String username = riotMatcher.group("username");
        String tag = riotMatcher.group("tag");

        String message;
        SummonerProfileDto profile = profileComponent.getSummonerProfile(username, tag, LolRegion.RU);
        if (profile != null) {
            log.info("PROFILE: " + profile);
            message = String.format(
                    """
                    🏅 *%s#%s*

                    📈 Уровень: *%s*
                    ⚔️ SoloQ: *%s*
                    🛡  Flex 5×5: *%s*
                    """,
                    profile.getUsername(),
                    profile.getTag(),
                    profile.getSummonerLvl(),
                    profile.getCurrentSoloQRank(),
                    profile.getCurrentFlexRank()
            );
        } else {
            log.warn("SUMMONER " + userInput + " NOT FOUND");
            message = TelegramMessageUtils.summonerNotFound(userInput);
        }

        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(message)
                .parseMode("Markdown")
                .build();
    }
}