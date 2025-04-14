package com.los.leagueofstats.services.integration.riot.accounts;

import com.los.leagueofstats.config.rest.RestClient;
import com.los.leagueofstats.services.integration.riot.accounts.dto.RiotAccountResDto;
import com.los.leagueofstats.services.integration.riot.global.enums.RiotRegion;
import com.los.leagueofstats.utils.RiotApiHelper;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Log4j2
@Service
public class RiotAccountApiService {

    // <editor-fold defaultstate="collapsed" desc="***Util elements***">

    @Setter
    @Value("${service.riot.accoutns.get-account.url}")
    private String getRiotAccountUrl;

    // </editor-fold>

    private final RestClient restClient;
    private final RiotApiHelper riotApiHelper;

    // <editor-fold defaultstate="collapsed" desc="***Init and setters***">

    @Autowired
    public RiotAccountApiService(RestClient restClient,
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

}
