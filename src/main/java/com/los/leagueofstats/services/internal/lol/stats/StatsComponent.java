package com.los.leagueofstats.services.internal.lol.stats;

import com.google.common.collect.ImmutableList;
import com.los.leagueofstats.services.integration.lol.dto.MatchInfoResDto;
import com.los.leagueofstats.services.integration.lol.dto.MatchParticipantDto;
import com.los.leagueofstats.services.integration.lol.dto.RiotMatchResDto;
import com.los.leagueofstats.services.integration.lol.enums.RiotQueueType;
import com.los.leagueofstats.services.integration.lol.enums.RiotRegion;
import com.los.leagueofstats.services.internal.lol.stats.dto.ModeStatsDto;
import com.los.leagueofstats.services.internal.lol.stats.dto.SummonerMatchStatsDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.los.leagueofstats.utils.CollectionUtilities.equalsAny;

/**
 * Компонент для сбора статистики по матчам игрока.
 */
@Log4j2
@Component
public class StatsComponent {

    private static final List<Integer> RANKED_QUEUE_TYPE_IDS = ImmutableList.of(RiotQueueType.RANKED_SOLO_5x5.getQueueId());
    private static final List<Integer> FLEX_QUEUE_TYPE_IDS = ImmutableList.of(RiotQueueType.RANKED_FLEX_SR.getQueueId());
    private static final List<Integer> TOTAL_QUEUE_TYPE_IDS = ImmutableList.of(RiotQueueType.NORMAL_DRAFT_PICK.getQueueId(),
            RiotQueueType.NORMAL_BLIND_PICK.getQueueId(),
            RiotQueueType.RANKED_FLEX_SR.getQueueId(),
            RiotQueueType.RANKED_SOLO_5x5.getQueueId());

    /** Сервис для получения ID и данных матчей */
    private final MatchFetchService matchFetchService;

    /**
     * Инициализация компонента.
     *
     * @param matchFetchService сервис матчей
     */
    public StatsComponent(MatchFetchService matchFetchService) {
        this.matchFetchService = matchFetchService;
    }

    /**
     * Получает список матчей и собирает статистику по ним.
     *
     * @param puuid PUUID игрока
     * @return сводка статистики по общим, соло и флекс играм
     */
    public SummonerMatchStatsDto collectGlobalStats(String puuid) {
        List<String> matchIds = matchFetchService.getMaxCountMatchIdsCacheable(puuid, RiotRegion.EUROPE);

        List<RiotMatchResDto> matches = fetchMatches(matchIds);

        ModeStatsDto globalStats = aggregateStats(puuid, matches);
        ModeStatsDto soloStats = aggregateStats(puuid, filterMatchesByQueue(matches, RANKED_QUEUE_TYPE_IDS));
        ModeStatsDto flexStats = aggregateStats(puuid, filterMatchesByQueue(matches, FLEX_QUEUE_TYPE_IDS));

        return SummonerMatchStatsDto.builder()
                .total(globalStats)
                .rankedSolo(soloStats)
                .rankedFlex(flexStats)
                .build();
    }

    /**
     * Получить сезонную статистику игрока.
     */
    public SummonerMatchStatsDto collectSeasonalStats(
            String puuid) {
        List<String> matchIds = matchFetchService.getCurrentSeasonMatchIdsCacheable(puuid, RiotRegion.EUROPE);

        //TODO fix statistic bug: Не фильтрует за текущий сезон
        List<RiotMatchResDto> matches = fetchMatches(matchIds);

        additionalLogs(matches);

        ModeStatsDto totalStats = aggregateStats(puuid, filterMatchesByQueue(matches, TOTAL_QUEUE_TYPE_IDS));
        ModeStatsDto soloStats = aggregateStats(puuid, filterMatchesByQueue(matches, RANKED_QUEUE_TYPE_IDS));
        ModeStatsDto flexStats = aggregateStats(puuid, filterMatchesByQueue(matches, FLEX_QUEUE_TYPE_IDS));

        return SummonerMatchStatsDto.builder()
                .total(totalStats)
                .rankedSolo(soloStats)
                .rankedFlex(flexStats)
                .build();
    }

    /**
     * Отфильтровать матчи по нужному режиму игры.
     */
    private List<RiotMatchResDto> filterMatchesByQueue(List<RiotMatchResDto> matches, List<Integer> queueTypes) {
        return matches.stream()
                .filter(match -> equalsAny(queueTypes, match.getInfo().getQueueId()))
                .toList();
    }

    /**
     * Агрегировать статистику по матчам.
     */
    private ModeStatsDto aggregateStats(
            String puuid,
            List<RiotMatchResDto> matches) {
        int wins = 0;
        int losses = 0;

        for (RiotMatchResDto match : matches) {
            MatchInfoResDto info = match.getInfo();

            MatchParticipantDto player = info.getParticipants().stream()
                    .filter(p -> p.getPuuid().equals(puuid))
                    .findFirst()
                    .orElse(null);

            if (player == null) continue;

            boolean win = Boolean.TRUE.equals(player.getWin());

            if (win) wins++;
            else losses++;
        }

        int total = wins + losses;
        double winRate = calcWinRate(wins, total);

        return ModeStatsDto.builder()
                .totalGames(total)
                .wins(wins)
                .losses(losses)
                .winRate(winRate)
                .build();
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
    private List<RiotMatchResDto> fetchMatches(List<String> matchIds) {
        List<RiotMatchResDto> result = new ArrayList<>();

        for (String matchId : matchIds) {
            try {
                RiotMatchResDto match = matchFetchService.getMatchByIdCacheable(matchId, RiotRegion.EUROPE); // без rateLimiter
                result.add(match);
            } catch (Exception ex) {
                log.warn("Failed to fetch matchId: {}", matchId, ex);
            }
        }

        return result;
    }

    private void additionalLogs(List<RiotMatchResDto> matches) {
        int normal = 0;
        int soloq = 0;
        int flex = 0;
        int aram = 0;
        int arena = 0;

        for (RiotMatchResDto match: matches) {
            if (match.getInfo().getQueueId() == 400) normal++;
            if (match.getInfo().getQueueId() == 420) soloq++;
            if (match.getInfo().getQueueId() == 440) flex++;
            if (match.getInfo().getQueueId() == 450) aram++;
            if (match.getInfo().getQueueId() == 1700) arena++;
        }

        log.info("NORMAL: " + normal);
        log.info("SOLOQ: " + soloq);
        log.info("FLEX: " + flex);
        log.info("ARAM: " + aram);
        log.info("ARENA: " + arena);
    }
}