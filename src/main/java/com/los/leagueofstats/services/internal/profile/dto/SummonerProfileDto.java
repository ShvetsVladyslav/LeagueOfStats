package com.los.leagueofstats.services.internal.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SummonerProfileDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private String tag;
    private String summonerLvl;
    private String currentSoloQRank;
    private String currentFlexRank;
}
