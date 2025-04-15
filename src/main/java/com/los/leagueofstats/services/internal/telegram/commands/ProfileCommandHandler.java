package com.los.leagueofstats.services.internal.telegram.commands;

import com.los.leagueofstats.services.integration.riot.enums.LolRegion;
import com.los.leagueofstats.services.internal.profile.ProfileComponent;
import com.los.leagueofstats.services.internal.profile.dto.SummonerProfileDto;
import com.los.leagueofstats.services.internal.telegram.enums.TelegramBotCommands;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;

@Log4j2
@Component
public class ProfileCommandHandler implements BotCommandHandler{

    private static final String RIOT_ID_REGEX = "^(?<username>[\\p{L}\\d_]+(?:\\s+[\\p{L}\\d_]+)*)#(?<tag>[A-Za-z0-9]{2,5})$";
    private static final Pattern RIOT_ID_PATTERN = Pattern.compile(RIOT_ID_REGEX);
    private final ProfileComponent profileComponent;

    public ProfileCommandHandler(ProfileComponent profileComponent) {
        this.profileComponent = profileComponent;
    }

    @Override
    public TelegramBotCommands command() {
        return TelegramBotCommands.PROFILE;
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();

        String text = update.getMessage().getText().trim();
        String userInput = text.replace(command().getCommand(), "").trim();

        Matcher riotMatcher = RIOT_ID_PATTERN.matcher(userInput);
        if (!riotMatcher.matches()) {
            log.warn("USER INPUT: " + userInput);
            return SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text(String.format(
                            """
                            |⚠️ *Неверный формат никнейма*
                            |
                            |Вы ввели: *%s*
                            |
                            |• В нике разрешены буквы (латиница / кириллица) и пробелы
                            |• После `#` — 2–5 латинских букв, без цифр  
                            |
                            |Пример: `Faker#KR` или `Министр Бота#baddy`
                            """,
                            userInput         // то, что ввёл пользователь
                    ))
                    .parseMode("Markdown")
                    .build();
        }

        String username = riotMatcher.group("username"); // "Министр Бота"
        String tag = riotMatcher.group("tag");      // "baddy"

        String message;
        SummonerProfileDto profile = profileComponent.getSummonerProfile(username, tag, LolRegion.RU);
        if (profile != null) {
            message = String.format(
                            """
                            🏅 *%s#%s*
                                    
                            📈 Уровень: *%s*
                            ⚔️ SoloQ: *%s*
                            🛡  Flex 5×5: *%s*
                            """,
                    profile.getUsername(),          // Ник
                    profile.getTag(),               // Тег
                    profile.getSummonerLvl(),               // Уровень призывателя
                    profile.getCurrentSoloQRank(),          // Текущий ранг в соло
                    profile.getCurrentFlexRank()            // Текущий ранг в флексе
            );
        } else {
            message =  String.format(
                    """
                    |😕 *Призыватель не найден*
                    |
                    |Я не смог найти игрока *%s*.
                    |
                    |• Проверьте, что ник и тег написаны без ошибок (регистр важен)  
                    |• Убедитесь, что выбран правильный регион  
                    |• Возможно, игрок недавно сменил Riot ID
                    |
                    |Попробуйте ещё раз чуть позже.
                    """,
                    userInput
            );
        }

        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(message)
                .parseMode("Markdown")
                .build();
    }
}
