package com.los.leagueofstats.services.integration.lol;

import com.los.leagueofstats.config.rest.RestClient;
import com.los.leagueofstats.services.integration.lol.dto.*;
import com.los.leagueofstats.services.integration.lol.enums.LolRegion;
import com.los.leagueofstats.services.integration.lol.enums.RiotRegion;
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
    @Setter
    @Value("${service.riot.match.get-match-ids-by-puuid.url}")
    private String getMatchIdsByPuuidUrl;
    @Setter
    @Value("${service.riot.match.get-match-by-id.url}")
    private String getMatchByIdUrl;

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

    /**
     * Получает Riot аккаунт по Riot ID (username + tag).
     *
     * @param username имя игрока (например, "Министр Бота")
     * @param tag тег игрока (например, "baddy")
     * @param region регион Riot (например, EUROPE)
     * @return DTO с PUUID, именем и тегом
     */
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

    /**
     * Получает информацию о ранговых лигах по PUUID.
     *
     * @param puuid уникальный идентификатор игрока
     * @param region регион League of Legends (например, RU)
     * @return список с данными по очередям (SoloQ, Flex и т.д.)
     */
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

    /**
     * Получает основные данные призывателя по PUUID.
     *
     * @param puuid уникальный идентификатор игрока
     * @param region регион League of Legends (например, RU)
     * @return DTO с уровнем, ID, именем и другим
     */
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

    /**
     * Получает список matchId игрока по PUUID с фильтрами.
     *
     * @param paramsDto search params
     * @param region    Riot account region
     *
     * @return список matchId
     */
    public List<String> getMatchIdsByPuuid(
            GetMatchIdsParamsDto paramsDto,
            RiotRegion region) {
        checkArgument(paramsDto != null, "Required params are not specified!");
        checkArgument(region != null, "Region is not specified!");

        String url = UriComponentsBuilder.fromUriString(riotApiHelper.getRiotApiBaseUrl(region) + getMatchIdsByPuuidUrl)
                .queryParams(paramsDto.toQueryParams())
                .buildAndExpand(paramsDto.getPuuid())
                .toUriString();

        HttpHeaders headers = riotApiHelper.buildCommonRiotHeaders();

        List<String> resDto;
        try {
            resDto = restClient.get(url, headers.toSingleValueMap(),
                    new ParameterizedTypeReference<List<String>>() {});
        } catch (RestClientException exception) {
            riotApiHelper.checkCommonResponseForError(exception);
            throw exception;
        }

        return resDto;
    }

    /**
     * Получает информацию о матче по matchId.
     *
     * @param matchId ид матча
     * @param region  регион призывателя
     * @return информация о матче
     */
    public RiotMatchResDto getMatchById(
            String matchId,
            RiotRegion region) {
        checkArgument(isNotBlank(matchId), "Match ID is not specified!");

        String url = UriComponentsBuilder.fromUriString(riotApiHelper.getRiotApiBaseUrl(region) + getMatchByIdUrl)
                .buildAndExpand(matchId)
                .toUriString();

        HttpHeaders headers = riotApiHelper.buildCommonRiotHeaders();

        RiotMatchResDto resDto;
        try {
            resDto = restClient.get(url, headers.toSingleValueMap(), RiotMatchResDto.class);
        } catch (RestClientException exception) {
            riotApiHelper.checkCommonResponseForError(exception);
            throw exception;
        }

        return resDto;
    }
}
