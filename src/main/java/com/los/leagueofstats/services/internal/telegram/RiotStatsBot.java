package com.los.leagueofstats.services.internal.telegram;

import com.los.leagueofstats.config.telegram.TelegramBotConfProps;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class RiotStatsBot extends TelegramLongPollingBot {

    private final TelegramBotConfProps telegramBotConfProps;

    @Override
    public String getBotUsername() {
        return telegramBotConfProps.getUsername();
    }

    @Override
    public String getBotToken() {
        return telegramBotConfProps.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var chatId = update.getMessage().getChatId().toString();
            var text   = update.getMessage().getText().trim();

            // Простейший парсер команд
            if (text.startsWith("/ping")) {
                executeSafely(chatId, "pong 🏓");
            } else if (text.startsWith("/summoner ")) {
                var name = text.substring(10).trim();
                var summary = riotService.getSummonerSummary(name);
                executeSafely(chatId, summary);
            } else {
                executeSafely(chatId,
                        """
                        🤖 *Доступные команды*
                        /ping — проверка связи  
                        /summoner <ник> — краткая информация о призывателе
                        """);
            }
        }
    }

    private void executeSafely(String chatId, String text) {
        try {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .parseMode("Markdown")
                    .build());
        } catch (Exception e) {
            // логируйте, но не падайте
        }
    }
}
