package com.los.leagueofstats.services.internal.telegram.commands;

import com.los.leagueofstats.services.internal.telegram.enums.TelegramBotCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Реализация должна быть помечена {@code @Component}, чтобы Spring видел её.
 */
public interface BotCommandHandler {
    /** Какую команду обслуживает хендлер */
    TelegramBotCommands command();

    /**
     * @param update полный объект обновления Telegram
     * @return подготовленный {@link SendMessage} (бот выполнит его сам)
     */
    SendMessage handle(Update update);
}
