package com.los.leagueofstats.services.integration.riot;

import com.los.leagueofstats.config.rest.RestClient;
import com.los.leagueofstats.services.integration.riot.dto.LeagueEntriesResDto;
import com.los.leagueofstats.services.integration.riot.dto.RiotAccountResDto;
import com.los.leagueofstats.services.integration.riot.dto.SummonerDataResDto;
import com.los.leagueofstats.services.integration.riot.enums.LolRegion;
import com.los.leagueofstats.services.integration.riot.enums.RiotRegion;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;


@Service
@Log4j2
public class RiotApiService {

    // <editor-fold defaultstate="collapsed" desc="***Util elements***">

    @Setter
    @Value("${service.riot.accoutns.get-account.url}")
    private String getRiotAccountUrl;
    @Setter
    @Value("${service.riot.league.get-entries.url}")
    private String getLeagueEntriesUrl;
    @Setter
    @Value("${service.riot.summoner.get-summoner-date.url}")
    private String getSummonerDataUrl;

    // </editor-fold>

    private final RestClient restClient;
    private final RiotApiHelper riotApiHelper;

    // <editor-fold defaultstate="collapsed" desc="***Init and setters***">

    @Autowired
    public RiotApiService(RestClient restClient,
                          RiotApiHelper riotApiHelper) {
        this.restClient = restClient;
        this.riotApiHelper = riotApiHelper;

        this.restClient.setAnchorForLog(this.getClass().getSimpleName());
    }

    // </editor-fold>

    public RiotAccountResDto getRiotAccountByRiotId(
            String username,
            String tag,
            RiotRegion region) {
        checkArgument(isNotBlank(username), "Username is not specified!");
        checkArgument(isNotBlank(tag), "Tag is not specified!");
        checkArgument(region != null, "Region is not specified!");

        String url = UriComponentsBuilder.fromUriString(riotApiHelper.getRiotApiBaseUrl(region) + getRiotAccountUrl)
                .encode()
                .buildAndExpand(username,tag)
                .toUriString();

        HttpHeaders headers = riotApiHelper.buildCommonRiotHeaders();

        RiotAccountResDto resDto;
        try {
            resDto = restClient.get(url, headers.toSingleValueMap(), RiotAccountResDto.class);
        } catch (RestClientException exception) {
            riotApiHelper.checkCommonResponseForError(exception);
            throw exception;
        }

        return resDto;
    }

    public List<LeagueEntriesResDto> getLeagueEntries(
            String puuid,
            LolRegion region) {
        checkArgument(isNotBlank(puuid), "PUUID is not specified!");
        checkArgument(region != null, "Region is not specified!");

        String url = UriComponentsBuilder.fromUriString(riotApiHelper.getRiotApiBaseUrl(region) + getLeagueEntriesUrl)
                .encode()
                .buildAndExpand(puuid)
                .toUriString();

        HttpHeaders headers = riotApiHelper.buildCommonRiotHeaders();

        List<LeagueEntriesResDto> resDto;
        try {
            resDto = restClient.get(url, headers.toSingleValueMap(),
                    new ParameterizedTypeReference<List<LeagueEntriesResDto>>() {});
        } catch (RestClientException exception) {
            riotApiHelper.checkCommonResponseForError(exception);
            throw exception;
        }

        return resDto;
    }

    public SummonerDataResDto getSummonerData(
            String puuid,
            LolRegion region) {
        checkArgument(isNotBlank(puuid), "PUUID is not specified!");
        checkArgument(region != null, "Region is not specified!");

        String url = UriComponentsBuilder.fromUriString(riotApiHelper.getRiotApiBaseUrl(region) + getSummonerDataUrl)
                .encode()
                .buildAndExpand(puuid)
                .toUriString();

        HttpHeaders headers = riotApiHelper.buildCommonRiotHeaders();

        SummonerDataResDto resDto;
        try {
            resDto = restClient.get(url, headers.toSingleValueMap(), SummonerDataResDto.class);
        } catch (RestClientException exception) {
            riotApiHelper.checkCommonResponseForError(exception);
            throw exception;
        }

        return resDto;
    }

}
