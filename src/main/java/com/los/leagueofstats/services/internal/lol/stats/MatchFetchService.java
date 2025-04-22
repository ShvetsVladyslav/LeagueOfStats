package com.los.leagueofstats.services.internal.lol.stats;

import com.google.common.util.concurrent.RateLimiter;
import com.los.leagueofstats.services.integration.lol.RiotApiService;
import com.los.leagueofstats.services.integration.lol.dto.GetMatchIdsParamsDto;
import com.los.leagueofstats.services.integration.lol.dto.RiotMatchResDto;
import com.los.leagueofstats.services.integration.lol.enums.RiotRegion;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для получения матчей с ограничением по скорости.
 */
@Log4j2
@Service
public class MatchFetchService {

    // <editor-fold defaultstate="collapsed" desc="*** Util elements ***">



    // </editor-fold>

    private final RiotApiService riotApiService;

    // <editor-fold defaultstate="collapsed" desc="*** Init and setters ***">

    private MatchFetchService(
            RiotApiService riotApiService) {
        this.riotApiService = riotApiService;
    }

    // </editor-fold>

    // ограничение 20 RPS, можно выставить 18–19 для страховки
    private final RateLimiter rateLimiter = RateLimiter.create(19.0);

    /**
     * Получает матч от Riot с учётом лимита.
     */
    public RiotMatchResDto getMatchById(String matchId) {
        return riotApiService.getMatchById(matchId, RiotRegion.EUROPE);
    }

    /**
     * Получает все матчId игрока с учётом лимитов и параметров.
     */
    public List<String> getLastMatchIds(String puuid) {
        rateLimiter.acquire(); // подождёт, если лимит превышен
        List<String> result = new ArrayList<>();

        int start = 0;
        int count = 10;
        int max = 20;

        while (result.size() < max) {
            int currentBatch = Math.min(count, max - result.size());

            GetMatchIdsParamsDto currentParams = GetMatchIdsParamsDto.builder()
                    .puuid(puuid)
                    .start(start)
                    .count(currentBatch)
                    .build();

            List<String> batch = riotApiService.getMatchIdsByPuuid(currentParams, RiotRegion.EUROPE);
            log.info("BATCH RESULT: " + batch.toString());

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
}
