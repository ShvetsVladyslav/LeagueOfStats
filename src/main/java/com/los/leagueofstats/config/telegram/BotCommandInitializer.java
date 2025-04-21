package com.los.leagueofstats.config.telegram;

import com.los.leagueofstats.services.internal.telegram.RiotStatsBot;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class BotCommandInitializer {

    private final RiotStatsBot myTelegramBot;

    @Bean
    public CommandLineRunner registerCommands() {
        return args -> {
            List<BotCommand> commands = List.of(
                    new BotCommand("/start", "Начало работы"),
                    new BotCommand("/profile", "Профиль игрока <ник#тег>"),
                    new BotCommand("/stats", "Статистика последних 500 матчей <ник#тег>")
            );

            myTelegramBot.execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        };
    }
}
