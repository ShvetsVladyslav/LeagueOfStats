package com.los.leagueofstats.services.internal.telegram;

import com.los.leagueofstats.config.telegram.TelegramBotConfProps;
import com.los.leagueofstats.services.internal.telegram.commands.BotCommandHandler;
import com.los.leagueofstats.services.internal.telegram.enums.TelegramBotCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Component
public class RiotStatsBot extends TelegramLongPollingBot {

    private final TelegramBotConfProps props;
    private final Map<TelegramBotCommands, BotCommandHandler> handlers;

    public RiotStatsBot(TelegramBotConfProps props,
                        List<BotCommandHandler> handlerList) {
        this.props = props;
        this.handlers = handlerList.stream()
                .collect(Collectors.toUnmodifiableMap(BotCommandHandler::command,
                        Function.identity()));
    }

    @Override
    public String getBotUsername() {
        return props.getUsername();
    }

    @Override
    public String getBotToken() {
        return props.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        String chatId = String.valueOf(update.getMessage().getChatId());
        String text = update.getMessage().getText().trim();
        String commandToken = text.split("\\s+", 2)[0];
        TelegramBotCommands command = TelegramBotCommands.getById(commandToken);
        if (command == null) {
            executeSafely(chatId, "Неизвестная команда. Напишите /start для справки.");
            return;
        }

        BotCommandHandler handler = handlers.get(command);
        if (handler == null) {
            executeSafely(chatId, "Неизвестная команда. Напишите /start для справки.");
            return;
        }

        try {
            SendMessage response = handler.handle(update);
            execute(response);
        } catch (Exception e) {
            log.error("Handler error for {}", command, e);
            executeSafely(chatId, "Упс! Произошла ошибка. Попробуйте позже.");
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
