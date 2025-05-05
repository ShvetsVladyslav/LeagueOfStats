package com.los.leagueofstats.services.internal.lol.stats;

import com.google.common.collect.ImmutableList;
import com.los.leagueofstats.services.integration.lol.dto.MatchInfoResDto;
import com.los.leagueofstats.services.integration.lol.dto.MatchParticipantDto;
import com.los.leagueofstats.services.integration.lol.dto.RiotMatchResDto;
import com.los.leagueofstats.services.integration.lol.enums.RiotQueueType;
import com.los.leagueofstats.services.integration.lol.enums.RiotRegion;
import com.los.leagueofstats.services.integration.lol.enums.SummonerSpellType;
import com.los.leagueofstats.services.internal.lol.stats.dto.DetailedMatchInfoDto;
import com.los.leagueofstats.services.internal.lol.stats.dto.ModeStatsDto;
import com.los.leagueofstats.services.internal.lol.stats.dto.SummonerMatchStatsDto;
import com.los.leagueofstats.utils.Region;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.los.leagueofstats.utils.CollectionUtilities.equalsAny;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

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

        List<RiotMatchResDto> matches = fetchMatches(matchIds);

        //additionalLogs(matches);

        ModeStatsDto totalStats = aggregateStats(puuid, filterMatchesByQueue(matches, TOTAL_QUEUE_TYPE_IDS));
        ModeStatsDto soloStats = aggregateStats(puuid, filterMatchesByQueue(matches, RANKED_QUEUE_TYPE_IDS));
        ModeStatsDto flexStats = aggregateStats(puuid, filterMatchesByQueue(matches, FLEX_QUEUE_TYPE_IDS));

        return SummonerMatchStatsDto.builder()
                .total(totalStats)
                .rankedSolo(soloStats)
                .rankedFlex(flexStats)
                .build();
    }

    public List<DetailedMatchInfoDto> collectLastMatchesDetailedStats(
            String puuid) {
        checkArgument(isNotBlank(puuid), "PUUID is not specified!");

        List<String> matchIds = matchFetchService.getNCountMatchIds(puuid, 5, RiotRegion.EUROPE);

        List<RiotMatchResDto> matches = fetchMatches(matchIds);

        return matches.stream()
                .map(match -> toDetailedDto(match, puuid))
                .toList();
    }

    public void deleteCacheGlobalStatsCache(
            String puuid,
            RiotRegion region) {
        checkArgument(isNotBlank(puuid), "PUUID is not specified!");
        checkArgument(region != null, "Region is not specified!");

        matchFetchService.deleteMaxCountMatchIdsCache(puuid, region);
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

    private double round(double value, int digits) {
        return Math.round(value * Math.pow(10, digits)) / Math.pow(10, digits);
    }

    private String formatDuration(long seconds) {
        long minutes = seconds / 60;
        long sec = seconds % 60;
        return String.format("%d:%02d", minutes, sec);
    }

    private String calculateTeamScore(MatchInfoResDto info, int teamId) {
        int kills = info.getParticipants().stream()
                .filter(p -> p.getTeamId() == teamId)
                .mapToInt(MatchParticipantDto::getKills)
                .sum();

        int deaths = info.getParticipants().stream()
                .filter(p -> p.getTeamId() == teamId)
                .mapToInt(MatchParticipantDto::getDeaths)
                .sum();

        return String.format("%d / %d", kills, deaths);
    }

    DetailedMatchInfoDto toDetailedDto(RiotMatchResDto match, String puuid) {
        MatchParticipantDto player = match.getInfo().getParticipants()
                .stream()
                .filter(p -> puuid.equals(p.getPuuid()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Игрок не найден в матче"));

        int totalCs = player.getTotalMinionsKilled() + player.getNeutralMinionsKilled();
        double gameMinutes = match.getInfo().getGameDuration() / 60.0;
        double csPerMin = totalCs / gameMinutes;

        return DetailedMatchInfoDto.builder()
                .championName(player.getChampionName())
                .role(player.getTeamPosition())
                .gameMode(Optional.ofNullable(RiotQueueType.fromId(match.getInfo().getQueueId()))
                        .map(RiotQueueType::getQueueName)
                        .orElse(null))
                .win(player.getWin())
                .kills(player.getKills())
                .deaths(player.getDeaths())
                .assists(player.getAssists())
                .damageDealt(player.getTotalDamageDealtToChampions())
                .damageTaken(player.getTotalDamageTaken())
                .totalCs(totalCs)
                .csPerMinute(round(csPerMin, 1))
                .level(player.getChampLevel())
                .summonerSpell1(SummonerSpellType.fromId(player.getSummoner1Id()).getName())
                .summonerSpell2(SummonerSpellType.fromId(player.getSummoner2Id()).getName())
                // TODO: Add items integration
                .items(Stream.of(
                        player.getItem0(), player.getItem1(), player.getItem2(),
                        player.getItem3(), player.getItem4(), player.getItem5(),
                        player.getItem6()
                ).map(Object::toString).toList())
                .gameDuration(formatDuration(match.getInfo().getGameDuration()))
                .teamScore(calculateTeamScore(match.getInfo(), player.getTeamId()))
                .isMvp(isPlayerMvp(match.getInfo(), player))
                .build();
    }

    private boolean isPlayerMvp(MatchInfoResDto info, MatchParticipantDto player) {
        int teamId = player.getTeamId();
        List<MatchParticipantDto> teamPlayers = info.getParticipants().stream()
                .filter(p -> p.getTeamId() == teamId)
                .toList();

        double playerKda = kda(player);
        int maxKdaIndex = findMaxIndex(teamPlayers, this::kda);

        int playerDmg = player.getTotalDamageDealtToChampions();
        int maxDmgIndex = findMaxIndex(teamPlayers, MatchParticipantDto::getTotalDamageDealtToChampions);

        double playerKillPart = killParticipation(info, teamPlayers, player);
        int maxPartIndex = findMaxIndex(teamPlayers, p -> killParticipation(info, teamPlayers, p));

        int maxGoldIndex = findMaxIndex(teamPlayers, MatchParticipantDto::getGoldEarned);
        int maxWardsIndex = findMaxIndex(teamPlayers, MatchParticipantDto::getWardsPlaced);
        int maxObjDmgIndex = findMaxIndex(teamPlayers, MatchParticipantDto::getDamageDealtToObjectives);

        int matches = 0;
        if (teamPlayers.get(maxKdaIndex).equals(player)) matches++;
        if (teamPlayers.get(maxDmgIndex).equals(player)) matches++;
        if (teamPlayers.get(maxPartIndex).equals(player)) matches++;



        if (teamPlayers.get(maxGoldIndex).equals(player)) matches++;
        if (teamPlayers.get(maxWardsIndex).equals(player)) matches++;
        if (teamPlayers.get(maxObjDmgIndex).equals(player)) matches++;

        return matches >= 2;
    }

    private double kda(MatchParticipantDto p) {
        return (p.getKills() + p.getAssists()) / Math.max(1.0, p.getDeaths());
    }

    private double killParticipation(MatchInfoResDto info, List<MatchParticipantDto> team, MatchParticipantDto player) {
        int teamKills = team.stream().mapToInt(MatchParticipantDto::getKills).sum();
        return teamKills == 0 ? 0 : (player.getKills() + player.getAssists()) / (double) teamKills;
    }

    private <T> int findMaxIndex(List<T> list, ToDoubleFunction<T> mapper) {
        double max = Double.NEGATIVE_INFINITY;
        int maxIndex = -1;
        for (int i = 0; i < list.size(); i++) {
            double value = mapper.applyAsDouble(list.get(i));
            if (value > max) {
                max = value;
                maxIndex = i;
            }
        }
        return maxIndex;
    }
}