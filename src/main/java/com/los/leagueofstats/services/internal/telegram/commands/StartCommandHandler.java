package com.los.leagueofstats.services.internal.telegram.commands;

import com.los.leagueofstats.services.internal.telegram.enums.TelegramBotCommands;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class StartCommandHandler  implements BotCommandHandler {
    @Override
    public TelegramBotCommands command() {
        return TelegramBotCommands.START;
    }

    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();
        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("""
                        👋 *Привет!* Я бот статистики Riot Games.
                        Доступные команды:
                        /profile <username>#<tag> — краткая информация о призывателе
                        """)
                .parseMode("Markdown")
                .build();
    }
}