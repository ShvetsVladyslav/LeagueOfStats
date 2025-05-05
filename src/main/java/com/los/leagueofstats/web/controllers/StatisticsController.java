package com.los.leagueofstats.web.controllers;

import com.los.leagueofstats.services.integration.lol.RiotApiHelper;
import com.los.leagueofstats.services.integration.lol.enums.LolRegion;
import com.los.leagueofstats.services.integration.lol.enums.RiotRegion;
import com.los.leagueofstats.services.integration.lol.exceptions.RiotApiException;
import com.los.leagueofstats.services.internal.lol.profile.ProfileComponent;
import com.los.leagueofstats.services.internal.lol.profile.dto.SummonerProfileDto;
import com.los.leagueofstats.services.internal.lol.stats.StatsComponent;
import com.los.leagueofstats.services.internal.lol.stats.dto.DetailedMatchInfoDto;
import com.los.leagueofstats.services.internal.lol.stats.dto.SummonerMatchStatsDto;
import com.los.leagueofstats.utils.TelegramMessageUtils;
import com.los.leagueofstats.web.dto.CommonWrapperResDto;
import com.los.leagueofstats.web.dto.statistics.DefaultRiotIdReqWebDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST контроллер для получения статистики по игрокам.
 */
@Log4j2
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatisticsController {

    private final ProfileComponent profileComponent;
    private final StatsComponent statsComponent;
    private final RiotApiHelper riotApiHelper;

    /**
     * Получить сезонную статистику игрока по никнейму и тегу.
     *
     * @param username ник игрока
     * @param tag тэг Riot ID
     * @param region регион Riot
     * @return DTO с сезонной статистикой или 404 если не найдено
     */
    @GetMapping("/seasonal")
    public CommonWrapperResDto<SummonerMatchStatsDto> getSeasonalStats(
            @RequestParam String username,
            @RequestParam String tag,
            @RequestParam LolRegion region) {
        try {
            SummonerProfileDto profileDto = profileComponent.getSummonerProfileCacheable(username, tag, region);

            return new CommonWrapperResDto<>(statsComponent.collectSeasonalStats(profileDto.getPuuid()));
        } catch (RiotApiException exception) {
            riotApiHelper.handleDefaultRiotException(exception);
            throw exception;
        } catch (Exception exception) {
            log.error("UNEXPECTED ERROR: " + exception.getMessage());
            throw exception;
        }
    }

    @PostMapping("/global")
    public CommonWrapperResDto<SummonerMatchStatsDto> getGlobalStats(
            @RequestBody @Valid DefaultRiotIdReqWebDto reqDto) {
        try {
            SummonerProfileDto profileDto =
                    profileComponent.getSummonerProfileCacheable(reqDto.getUsername(), reqDto.getTag(), reqDto.getRegion());

            return new CommonWrapperResDto<>(statsComponent.collectGlobalStats(profileDto.getPuuid()));
        } catch (RiotApiException exception) {
            riotApiHelper.handleDefaultRiotException(exception);
            throw exception;
        } catch (Exception exception) {
            log.error("UNEXPECTED ERROR: " + exception.getMessage());
            throw exception;
        }
    }

    @PostMapping("/last-matches")
    public CommonWrapperResDto<List<DetailedMatchInfoDto>> getLastMatchesDetailedStatistic(
            @RequestBody @Valid DefaultRiotIdReqWebDto reqDto) {
        try {
            SummonerProfileDto profileDto =
                    profileComponent.getSummonerProfileCacheable(reqDto.getUsername(), reqDto.getTag(), reqDto.getRegion());

            return new CommonWrapperResDto<>(statsComponent.collectLastMatchesDetailedStats(profileDto.getPuuid()));
        } catch (RiotApiException exception) {
            riotApiHelper.handleDefaultRiotException(exception);
            throw exception;
        } catch (Exception exception) {
            log.error("UNEXPECTED ERROR: " + exception.getMessage());
            throw exception;
        }
    }

    @DeleteMapping("/matchIds/del-cache")
    public CommonWrapperResDto clearMatchIdsCacheForPlayer(
            @RequestParam String username,
            @RequestParam String tag,
            @RequestParam LolRegion region) {
        try {
            SummonerProfileDto profileDto =
                    profileComponent.getSummonerProfileCacheable(username, tag, region);

            statsComponent.deleteCacheGlobalStatsCache(profileDto.getPuuid(), RiotRegion.EUROPE);

            return new CommonWrapperResDto<>();
        } catch (RiotApiException exception) {
            riotApiHelper.handleDefaultRiotException(exception);
            throw exception;
        } catch (Exception exception) {
            log.error("UNEXPECTED ERROR: " + exception.getMessage());
            throw exception;
        }
    }
}
