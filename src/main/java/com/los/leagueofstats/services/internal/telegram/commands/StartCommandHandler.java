package com.los.leagueofstats.services.internal.telegram.commands;

import com.los.leagueofstats.services.internal.telegram.enums.TelegramBotCommands;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Обработка команды /start.
 * Показывает приветственное сообщение и список доступных команд.
 */
@Log4j2
@Component
public class StartCommandHandler implements BotCommandHandler {

    /** Возвращает команду, которую обрабатывает — /start */
    @Override
    public TelegramBotCommands command() {
        return TelegramBotCommands.START;
    }

    /**
     * Обрабатывает команду /start и возвращает сообщение с подсказками.
     *
     * @param update сообщение от пользователя
     * @return приветственный текст с описанием команд
     */
    @Override
    public SendMessage handle(Update update) {
        long chatId = update.getMessage().getChatId();
        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("""
                        👋 *Привет!* Я бот статистики Riot Games.
                        Доступные команды:
                        /profile <ник#тег> — Показать профиль игрока
                        /stats <ник#тег> — Общая статистика за последние 500 матчей
                        """)
                .parseMode("Markdown")
                .build();
    }
}