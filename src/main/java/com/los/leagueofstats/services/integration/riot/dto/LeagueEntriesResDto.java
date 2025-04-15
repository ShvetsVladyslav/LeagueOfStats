package com.los.leagueofstats.services.integration.riot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeagueEntriesResDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String leagueId;
    private String queueType;
    private String tier;
    private String rank;
    private String puuid;
    private Integer leaguePoints;
    private Integer wins;
    private Integer losses;
    private Boolean veteran;
    private Boolean inactive;
    private Boolean fleshBlood;
    private Boolean hotStreak;
}
