package com.los.leagueofstats.services.internal.telegram;

import com.los.leagueofstats.config.telegram.TelegramBotConfProps;
import com.los.leagueofstats.services.integration.lol.exceptions.RiotApiException;
import com.los.leagueofstats.services.internal.telegram.commands.ProfileCommandHandler;
import com.los.leagueofstats.services.internal.telegram.commands.StartCommandHandler;
import com.los.leagueofstats.services.internal.telegram.commands.StatisticsCommandHandler;
import com.los.leagueofstats.services.internal.telegram.enums.TelegramBotCommands;
import com.los.leagueofstats.utils.TelegramMessageUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Log4j2
@Component
public class RiotStatsBot extends TelegramLongPollingBot {

    // <editor-fold defaultstate="collapsed" desc="*** Util elements ***">



    // </editor-fold>

    private final StartCommandHandler startCommandHandler;
    private final ProfileCommandHandler profileCommandHandler;
    private final StatisticsCommandHandler statisticsCommandHandler;
    private final TelegramBotConfProps props;

    // <editor-fold defaultstate="collapsed" desc="*** Init and setters ***">

    public RiotStatsBot(StartCommandHandler startCommandHandler,
                        ProfileCommandHandler profileCommandHandler,
                        StatisticsCommandHandler statisticsCommandHandler,
                        TelegramBotConfProps props) {
        this.startCommandHandler = startCommandHandler;
        this.profileCommandHandler = profileCommandHandler;
        this.statisticsCommandHandler = statisticsCommandHandler;
        this.props = props;
    }

    @Override
    public String getBotUsername() {
        return props.getUsername();
    }

    @Override
    public String getBotToken() {
        return props.getToken();
    }

    // </editor-fold>

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        String chatId = String.valueOf(update.getMessage().getChatId());
        String text = update.getMessage().getText().trim();
        if (!text.startsWith("/")) {
            return;
        }
        List<String> splitedTextLine = List.of(text.split("\\s+", 2));
//        checkArgument(!splitedTextLine.isEmpty() && splitedTextLine.size() <= 2, "Text line is not satisfying conditions!");

        String commandToken = splitedTextLine.get(0);
        TelegramBotCommands command = TelegramBotCommands.getById(commandToken);
        if (command == null) {
            executeSafely(chatId, "Неизвестная команда. Напишите /start для справки.");
            return;
        }
        try {
            switch (command) {
                case START -> {
                    SendMessage message = startCommandHandler.handle(update);
                    sendMessage(chatId, message);
                }
                case PROFILE -> {
                    SendMessage message = profileCommandHandler.handle(update);
                    sendMessage(chatId, message);
                }
                case GLOBAL_STATS -> {
                    executeSafely(chatId, "⏳ Собираем статистику, это может занять несколько минут...");
                    SendMessage message = statisticsCommandHandler.handle(update);
                    sendMessage(chatId, message);
                }
                default -> executeSafely(chatId, "Неизвестная команда. Напишите /start для справки.");
            }
        } catch (RiotApiException exception) {
            HttpStatus status = HttpStatus.valueOf(exception.getRespStatus());

            if (HttpStatus.TOO_MANY_REQUESTS.equals(status)) {
                log.error("Exceeded Riot API limit: " + exception.getErrText());
                executeSafely(chatId, TelegramMessageUtils.rateLimitExceeded());
            } else if (status.is5xxServerError()) {
                log.error("Got Riot API 5xx error: " + exception.getErrText());
                executeSafely(chatId, TelegramMessageUtils.riotApiError());
            } else {
                log.error("UNEXPECTED RIOT ERROR: " + exception.getErrText() + " ERROR CODE: " + exception.getRespStatus());
                executeSafely(chatId, TelegramMessageUtils.unknownError());
            }
        } catch (Exception exception) {
            log.error("UNEXPECTED ERROR: " + exception.getMessage());
            executeSafely(chatId, TelegramMessageUtils.unknownError());
        }
    }


    private void sendMessage(
            String chatId,
            SendMessage message) {
        try {
            execute(message);
        } catch (Exception e) {
            log.error("Handler error for " + e.getMessage());
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
            log.error("Got error while trying to send telegram message");
        }
    }
}
