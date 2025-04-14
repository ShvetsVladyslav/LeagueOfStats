package com.los.leagueofstats.config.telegram;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Component
@ConfigurationProperties(prefix = "configuration.telegram-bot")
@Validated
@Data
public class TelegramBotConfProps {

    @NotBlank
    private String username;
    @NotBlank
    private String token;
}
