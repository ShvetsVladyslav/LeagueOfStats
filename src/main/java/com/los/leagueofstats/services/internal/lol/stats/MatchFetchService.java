package com.los.leagueofstats.services.internal.lol.stats;

import com.google.common.util.concurrent.RateLimiter;
import com.los.leagueofstats.config.riot.RiotApiConfProps;
import com.los.leagueofstats.services.integration.lol.RiotApiService;
import com.los.leagueofstats.services.integration.lol.dto.GetMatchIdsParamsDto;
import com.los.leagueofstats.services.integration.lol.dto.RiotMatchResDto;
import com.los.leagueofstats.services.integration.lol.enums.RiotRegion;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.los.leagueofstats.config.redis.CacheConfig.*;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Сервис для получения матчей с ограничением по скорости.
 */
@Log4j2
@Service
public class MatchFetchService {

    // <editor-fold defaultstate="collapsed" desc="*** Util elements ***">



    // </editor-fold>

    private final RiotApiService riotApiService;
    private final RiotApiConfProps riotApiConfProps;

    // <editor-fold defaultstate="collapsed" desc="*** Init and setters ***">

    public MatchFetchService(
            RiotApiService riotApiService,
            RiotApiConfProps riotApiConfProps) {
        this.riotApiService = riotApiService;
        this.riotApiConfProps = riotApiConfProps;
    }

    // </editor-fold>

    // ограничение 20 RPS, можно выставить 18–19 для страховки
    private final RateLimiter rateLimiter = RateLimiter.create(19.0);

    /**
     * Получает матч от Riot. Cacheable
     */
    @Cacheable(value = MATCH_CACHE_NAME, key = "#matchId + ':' + #region")
    public RiotMatchResDto getMatchByIdCacheable(
            String matchId,
            RiotRegion region) {
        checkArgument(isNotBlank(matchId), "Match ID is not specified!");
        checkArgument(region != null, "Region is not specified!");

        return getMatchById(matchId, region);
    }

    /**
     * Получает матч от Riot с учётом лимита.
     */
    public RiotMatchResDto getMatchById(String matchId, RiotRegion region) {
        checkArgument(isNotBlank(matchId), "Match ID is not specified!");
        checkArgument(region != null, "Region is not specified!");

        log.debug("GET MATCH FROM API " + matchId);

        // ⏱️ задержка 1200мс — это 50 RPS максимум и ~100 за 2 минуты
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return riotApiService.getMatchById(matchId, region);
    }

    /**
     * Получает все матчId игрока с учётом лимитов и параметров. Cacheable
     */
    @Cacheable(value = GLOBAL_MATCHES_CACHE_NAME, key = "#puuid + ':' + #region")
    public List<String> getMaxCountMatchIdsCacheable(
            String puuid,
            RiotRegion region) {
        checkArgument(isNotBlank(puuid), "PUUID is not specified!");
        checkArgument(region != null, "Region is not specified!");

        return getMaxCountMatchIds(puuid, region);
    }

    /**
     * Получает все матчId игрока с учётом лимитов и параметров.
     */
    public List<String> getMaxCountMatchIds(
            String puuid,
            RiotRegion region) {
        rateLimiter.acquire(); // подождёт, если лимит превышен
        List<String> result = new ArrayList<>();

        int start = 0;
        int count = 100;
        int max = 2000;

        while (result.size() < max) {
            int currentBatch = Math.min(count, max - result.size());

            GetMatchIdsParamsDto currentParams = GetMatchIdsParamsDto.builder()
                    .puuid(puuid)
                    .start(start)
                    .count(currentBatch)
                    .build();

            List<String> batch = riotApiService.getMatchIdsByPuuid(currentParams, region);
            log.debug("BATCH RESULT: " + batch.toString());

            if (batch.isEmpty()) break;

            result.addAll(batch);
            start += currentBatch;
            try {
                Thread.sleep(1000); // пауза между батчами
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return result;
    }

    /**
     * Получает все матчId игрока в текущем сезоне с учётом лимитов и параметров. Кешировано
     */
    @Cacheable(value = SEASON_MATCHES_CACHE_NAME, key = "#puuid + ':' + #region")
    public List<String> getCurrentSeasonMatchIdsCacheable(
            String puuid,
            RiotRegion region) {
        checkArgument(isNotBlank(puuid), "PUUID is not specified!");
        checkArgument(region != null, "Region is not specified!");

        return getCurrentSeasonMatchIds(puuid, region);
    }

    /**
     * Получает все матчId игрока в текущем сезоне с учётом лимитов и параметров.
     */
    public List<String> getCurrentSeasonMatchIds(
            String puuid,
            RiotRegion region) {
        rateLimiter.acquire(); // подождёт, если лимит превышен
        List<String> result = new ArrayList<>();

        int start = 0;
        int count = 100;
        int max = 2000;

        while (result.size() < max) {
            int currentBatch = Math.min(count, max - result.size());

            GetMatchIdsParamsDto currentParams = GetMatchIdsParamsDto.builder()
                    .puuid(puuid)
                    .startTime(getStartOfCurrentSeasonTimestamp())
                    .start(start)
                    .count(currentBatch)
                    .build();

            List<String> batch = riotApiService.getMatchIdsByPuuid(currentParams, region);
            log.debug("BATCH RESULT: " + batch.toString());

            if (batch.isEmpty()) break;

            result.addAll(batch);
            start += currentBatch;
            try {
                Thread.sleep(1000); // пауза между батчами
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return result;
    }

    /**
     * Получить timestamp начала текущего соревновательного сезона.
     */
    public Long getStartOfCurrentSeasonTimestamp() {
        return Instant.parse(riotApiConfProps.getSeasonStartDateTime()).getEpochSecond();
    }
}
