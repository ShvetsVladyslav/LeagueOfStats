package com.los.leagueofstats.services.internal.lol.stats;

import com.los.leagueofstats.services.integration.lol.dto.MatchInfoResDto;
import com.los.leagueofstats.services.integration.lol.dto.MatchParticipantDto;
import com.los.leagueofstats.services.integration.lol.dto.RiotMatchResDto;
import com.los.leagueofstats.services.integration.lol.enums.RiotRegion;
import com.los.leagueofstats.services.internal.lol.stats.dto.MatchStatsDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Компонент для сбора статистики по матчам игрока.
 */
@Log4j2
@Component
public class StatsComponent {

    /** Сервис для получения ID и данных матчей */
    private final MatchFetchService matchFetchService;

    /**
     * Инициализация компонента.
     *
     * @param matchFetchService сервис матчей
     */
    private StatsComponent(MatchFetchService matchFetchService) {
        this.matchFetchService = matchFetchService;
    }

    /**
     * Получает список матчей и собирает статистику по ним.
     *
     * @param puuid PUUID игрока
     * @return сводка статистики по общим, соло и флекс играм
     */
    public MatchStatsDto collectStats(String puuid) {
        List<String> matchIds = matchFetchService.getMaxCountMatchIdsCacheable(puuid, RiotRegion.EUROPE);

        List<RiotMatchResDto> matches = fetchMatchesSequentiallyWithDelay(matchIds);

        int totalWins = 0;
        int totalLosses = 0;
        int soloWins = 0;
        int soloLosses = 0;
        int flexWins = 0;
        int flexLosses = 0;

        for (RiotMatchResDto response : matches) {
            MatchInfoResDto info = response.getInfo();
            Integer queueId = info.getQueueId();

            MatchParticipantDto player = info.getParticipants().stream()
                    .filter(p -> p.getPuuid().equals(puuid))
                    .findFirst()
                    .orElse(null);

            if (player == null) continue;

            boolean win = Boolean.TRUE.equals(player.getWin());

            if (win) totalWins++;
            else totalLosses++;

            if (queueId != null && queueId == 420) {
                if (win) soloWins++;
                else soloLosses++;
            } else if (queueId != null && queueId == 440) {
                if (win) flexWins++;
                else flexLosses++;
            }
        }

        return buildStats(totalWins, totalLosses, soloWins, soloLosses, flexWins, flexLosses);
    }

    /**
     * Строит итоговую статистику на основе побед/поражений.
     */
    private MatchStatsDto buildStats(int tWin, int tLose, int sWin, int sLose, int fWin, int fLose) {
        MatchStatsDto dto = new MatchStatsDto();

        int totalGames = tWin + tLose;
        int soloGames = sWin + sLose;
        int flexGames = fWin + fLose;

        dto.setTotalMatches(totalGames);
        dto.setTotalWins(tWin);
        dto.setTotalLosses(tLose);
        dto.setTotalWinRate(calcWinRate(tWin, totalGames));

        dto.setSoloMatches(soloGames);
        dto.setSoloWins(sWin);
        dto.setSoloLosses(sLose);
        dto.setSoloWinRate(calcWinRate(sWin, soloGames));

        dto.setFlexMatches(flexGames);
        dto.setFlexWins(fWin);
        dto.setFlexLosses(fLose);
        dto.setFlexWinRate(calcWinRate(fWin, flexGames));

        return dto;
    }

    /**
     * Вычисляет винрейт.
     */
    private Double calcWinRate(int wins, int total) {
        return total == 0 ? 0.0 : (wins * 100.0 / total);
    }

    /**
     * Последовательно тянет матчи с задержкой, чтобы не упираться в лимиты API.
     *
     * @param matchIds список матчей
     * @return список матчей с полной информацией
     */
    private List<RiotMatchResDto> fetchMatchesSequentiallyWithDelay(List<String> matchIds) {
        List<RiotMatchResDto> result = new ArrayList<>();

        for (String matchId : matchIds) {
            try {
                RiotMatchResDto match = matchFetchService.getMatchByIdCacheable(matchId, RiotRegion.EUROPE); // без rateLimiter
                result.add(match);
            } catch (Exception ex) {
                log.warn("Failed to fetch matchId: {}", matchId, ex);
            }

            // ⏱️ задержка 1200мс — это 50 RPS максимум и ~100 за 2 минуты
            try {
                Thread.sleep(1200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        return result;
    }
}