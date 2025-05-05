package com.los.leagueofstats.services.internal.telegram;

import com.los.leagueofstats.config.telegram.TelegramBotConfProps;
import com.los.leagueofstats.services.integration.lol.exceptions.RiotApiException;
import com.los.leagueofstats.services.internal.telegram.commands.*;
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
    private final GlobalStatisticsCommandHandler globalStatisticsCommandHandler;
    private final StatisticCommandHandler statisticCommandHandler;
    private final LastMatchesCommandHandler lastMatchesCommandHandler;
    private final TelegramBotConfProps props;

    // <editor-fold defaultstate="collapsed" desc="*** Init and setters ***">

    public RiotStatsBot(StartCommandHandler startCommandHandler,
                        ProfileCommandHandler profileCommandHandler,
                        GlobalStatisticsCommandHandler globalStatisticsCommandHandler,
                        StatisticCommandHandler statisticCommandHandler,
                        LastMatchesCommandHandler lastMatchesCommandHandler,
                        TelegramBotConfProps props) {
        this.startCommandHandler = startCommandHandler;
        this.profileCommandHandler = profileCommandHandler;
        this.globalStatisticsCommandHandler = globalStatisticsCommandHandler;
        this.statisticCommandHandler = statisticCommandHandler;
        this.lastMatchesCommandHandler = lastMatchesCommandHandler;
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
        log.info("Получен userInput от пользователя: '{}'", text);
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
                    log.info("Обработка команды /start от chatId: {}", chatId);
                    SendMessage message = startCommandHandler.handle(update);
                    sendMessage(chatId, message);
                }
                case PROFILE -> {
                    log.info("Обработка команды /profile от chatId: {}", chatId);
                    SendMessage message = profileCommandHandler.handle(update);
                    sendMessage(chatId, message);
                }
                case GLOBAL_STATS -> {
                    log.info("Обработка команды /globalstats от chatId: {} — начало сбора статистики", chatId);
                    executeSafely(chatId, "⏳ Собираем статистику, это может занять несколько минут...");
                    SendMessage message = globalStatisticsCommandHandler.handle(update);
                    log.info("Сбор статистики завершён для chatId: {}", chatId);
                    sendMessage(chatId, message);
                }
                case STATS -> {
                    log.info("Обработка команды /stats от chatId: {} — начало сбора статистики", chatId);
                    executeSafely(chatId, "⏳ Собираем статистику, это может занять несколько минут...");
                    SendMessage message = statisticCommandHandler.handle(update);
                    log.info("Сбор статистики завершён для chatId: {}", chatId);
                    sendMessage(chatId, message);
                }
                case LAST_GAMES -> {
                    log.info("Обработка команды /lastgames от chatId: {} — начало сбора статистики", chatId);
                    executeSafely(chatId, "⏳ Собираем статистику, это может занять несколько минут...");
                    SendMessage message = lastMatchesCommandHandler.handle(update);
                    log.info("Сбор статистики завершён для chatId: {}", chatId);
                    sendMessage(chatId, message);
                }
                default -> {
                    log.warn("Получена неизвестная команда от chatId: {}. Отправка подсказки.", chatId);
                    executeSafely(chatId, "Неизвестная команда. Напишите /start для справки.");
                }
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
