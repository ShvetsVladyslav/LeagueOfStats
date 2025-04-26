package com.los.leagueofstats.config.riot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Component
@ConfigurationProperties(prefix = "configuration.riot-api")
@Validated
@Data
public class RiotApiConfProps {

    @NotBlank
    private String riotApiToken;
    @NotNull
    private String seasonStartDateTime;
}
