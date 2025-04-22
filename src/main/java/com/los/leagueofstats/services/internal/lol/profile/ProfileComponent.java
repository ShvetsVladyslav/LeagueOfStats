package com.los.leagueofstats.services.internal.lol.profile;

import com.google.common.base.Joiner;
import com.los.leagueofstats.services.integration.lol.RiotApiService;
import com.los.leagueofstats.services.integration.lol.dto.LeagueEntriesResDto;
import com.los.leagueofstats.services.integration.lol.dto.RiotAccountResDto;
import com.los.leagueofstats.services.integration.lol.dto.SummonerDataResDto;
import com.los.leagueofstats.services.integration.lol.enums.LeagueQueueType;
import com.los.leagueofstats.services.integration.lol.enums.LolRegion;
import com.los.leagueofstats.services.integration.lol.enums.RiotRegion;
import com.los.leagueofstats.services.integration.lol.exceptions.RiotApiException;
import com.los.leagueofstats.services.internal.lol.profile.dto.SummonerProfileDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Компонент для получения профиля игрока по Riot ID.
 */
@Log4j2
@Component
public class ProfileComponent {

    private final RiotApiService riotApiService;

    /**
     * Создание компонента с зависимостью RiotApiService.
     */
    private ProfileComponent(RiotApiService riotApiService) {
        this.riotApiService = riotApiService;
    }

    @Cacheable(value = "profile", key = "#username + '#' + #tag + ':' + #region")
    public SummonerProfileDto getSummonerProfileCacheable(String username, String tag, LolRegion region) {
        checkArgument(isNotBlank(username), "Username is not specified!");
        checkArgument(isNotBlank(tag), "Tag is not specified!");
        checkArgument(region != null, "Region is not specified!");

        return getSummonerProfile(username, tag, region);
    }

    /**
     * Возвращает профиль призывателя по Riot ID.
     *
     * @param username имя игрока (например, "Министр Бота")
     * @param tag тэг игрока (например, "baddy")
     * @param region регион League of Legends (например, RU)
     * @return краткий профиль с уровнем и рангами, или null, если игрок не найден
     */
    public SummonerProfileDto getSummonerProfile(String username, String tag, LolRegion region) {
        checkArgument(isNotBlank(username), "Username is not specified!");
        checkArgument(isNotBlank(tag), "Tag is not specified!");
        checkArgument(region != null, "Region is not specified!");

        RiotAccountResDto account;
        try {
            log.info("GET PROFILE FROM API");
            account = riotApiService.getRiotAccountByRiotId(username, tag, RiotRegion.EUROPE);
        } catch (RiotApiException exception) {
            if (exception.getRespStatus().equals(HttpStatus.NOT_FOUND.value())) {
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
                .puuid(account.getPuuid())
                .username(account.getGameName())
                .tag(account.getTagLine())
                .summonerLvl(String.valueOf(summonerData.getSummonerLevel()))
                .currentSoloQRank(buildRankStr(soloQInfo))
                .currentFlexRank(buildRankStr(flexInfo))
                .build();
    }

    /**
     * Формирует строку ранга (например, "GOLD II 87 LP").
     * Если null — возвращает "Unranked".
     */
    private String buildRankStr(LeagueEntriesResDto rankedInfo) {
        if (rankedInfo == null) {
            return "Unranked";
        }
        return Joiner.on(" ").join(rankedInfo.getTier(), rankedInfo.getRank(), rankedInfo.getLeaguePoints(), "LP");
    }
}