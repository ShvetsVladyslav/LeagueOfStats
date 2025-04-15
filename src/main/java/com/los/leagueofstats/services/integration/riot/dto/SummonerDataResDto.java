package com.los.leagueofstats.services.integration.riot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SummonerDataResDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String accountId;
    private String puuid;
    private Integer profileIconId;
    private Long revisionDate;
    private Integer summonerLevel;
}
