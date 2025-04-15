package com.los.leagueofstats.services.internal.profile;

import com.google.common.base.Joiner;
import com.los.leagueofstats.services.integration.riot.RiotApiService;
import com.los.leagueofstats.services.integration.riot.dto.LeagueEntriesResDto;
import com.los.leagueofstats.services.integration.riot.dto.RiotAccountResDto;
import com.los.leagueofstats.services.integration.riot.dto.SummonerDataResDto;
import com.los.leagueofstats.services.integration.riot.enums.LeagueQueueType;
import com.los.leagueofstats.services.integration.riot.enums.LolRegion;
import com.los.leagueofstats.services.integration.riot.enums.RiotRegion;
import com.los.leagueofstats.services.integration.riot.exceptions.RiotApiException;
import com.los.leagueofstats.services.internal.profile.dto.SummonerProfileDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Log4j2
@Component
public class ProfileComponent {

    // <editor-fold defaultstate="collapsed" desc="*** Util elements ***">



    // </editor-fold>

    private final RiotApiService riotApiService;

    // <editor-fold defaultstate="collapsed" desc="*** Init and setters ***">

    private ProfileComponent(
            RiotApiService riotApiService) {
        this.riotApiService = riotApiService;
    }

    // </editor-fold>

    public SummonerProfileDto getSummonerProfile(
            String username,
            String tag,
            LolRegion region) {
        checkArgument(isNotBlank(username), "Username is not specified!");
        checkArgument(isNotBlank(tag), "Tag is not specified!");
        checkArgument(region != null, "Region is not specified!");

        RiotAccountResDto account;
        try {
            account = riotApiService.getRiotAccountByRiotId(username, tag, RiotRegion.EUROPE);
        } catch (RiotApiException exception) {
            // TODO: exception.getRespStatus().equals(HttpStatus.NOT_FOUND.value())
            if (exception.getErrCode().equals("404")) {
                return null;
            }
            throw exception;
        }

        SummonerDataResDto summonerData = riotApiService.getSummonerData(account.getPuuid(), LolRegion.RU);

        List<LeagueEntriesResDto> rankedInfo = riotApiService.getLeagueEntries(account.getPuuid(), LolRegion.RU);
        LeagueEntriesResDto soloQInfo = rankedInfo.stream()
                .filter(entrie -> LeagueQueueType.SOLOQ.getId().equals(entrie.getQueueType()))
                .findFirst()
                .orElse(null);
        LeagueEntriesResDto flexInfo = rankedInfo.stream()
                .filter(entrie -> LeagueQueueType.FLEX.getId().equals(entrie.getQueueType()))
                .findFirst()
                .orElse(null);

        return SummonerProfileDto.builder()
                .username(account.getGameName())
                .tag(account.getTagLine())
                .summonerLvl(String.valueOf(summonerData.getSummonerLevel()))
                .currentSoloQRank(buildRankStr(soloQInfo))
                .currentFlexRank(buildRankStr(flexInfo))
                .build();
    }


    // ======= private elements =======

    private String buildRankStr(LeagueEntriesResDto rankedInfo) {
        if (rankedInfo == null) {
            return "Unranked";
        }

        return Joiner.on(" ").join(rankedInfo.getTier(), rankedInfo.getRank(), rankedInfo.getLeaguePoints(), "LP");
    }

}
