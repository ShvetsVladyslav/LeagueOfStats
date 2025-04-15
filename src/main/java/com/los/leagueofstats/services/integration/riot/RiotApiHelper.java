package com.los.leagueofstats.services.integration.riot;

import com.los.leagueofstats.config.riot.RiotApiConfProps;
import com.los.leagueofstats.services.integration.riot.exceptions.RiotApiException;
import com.los.leagueofstats.services.integration.riot.exceptions.RiotExceptionMessageDto;
import com.los.leagueofstats.utils.JsonUtil;
import com.los.leagueofstats.utils.Region;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Nullable;
import java.util.Collections;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Log4j2
@Component
@RequiredArgsConstructor
public class RiotApiHelper {

    private final RiotApiConfProps riotApiConfProps;

    // <editor-fold defaultstate="collapsed" desc="*** URL Utils ***">

    @Setter
    @Value("${service.riot.base-url}")
    private String riotApiBaseUrl;

    public String getRiotApiBaseUrl(Region region) {
        checkArgument(region != null, "Region is not specified!");

        return UriComponentsBuilder.fromUriString(riotApiBaseUrl)
                .buildAndExpand(region.getId())
                .toUriString();
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="*** HEADER Utils ***">

    public static final String RIOT_TOKEN_HEADER = "X-Riot-Token";

    public HttpHeaders buildCommonRiotHeaders() {
        HttpHeaders headers = new HttpHeaders();

        headers.add(RIOT_TOKEN_HEADER, riotApiConfProps.getRiotApiToken());
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        return headers;
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="*** EXCEPTION Utils ***">

    public void checkCommonResponseForError(RestClientException exception) {
        RiotExceptionMessageDto res = tryGetJsonCommonResponse(exception);

        if (res == null || res.getRespStatus() == null) {
            return;
        }

        throw RiotApiException.builder()
                .errCode(res.getErrCode())
                .errText(res.getErrText())
                .respStatus(res.getRespStatus())
                .payload(res)
                .build();
    }

    @Nullable
    public RiotExceptionMessageDto tryGetJsonCommonResponse(RestClientException e){
        RestClientResponseException rcex = extractRestClientResponseException(e);
        if (rcex == null) {
            return null;
        }

        RiotExceptionMessageDto dto = JsonUtil.fromJson(rcex.getResponseBodyAsString(), RiotExceptionMessageDto.class);
        dto.setRespStatus(rcex.getRawStatusCode());

        return dto;
    }

    /**
     * Получение статуса ответа.
     *
     * @param e исключение от сервиса
     * @return статус ответа, или null если не удалось получить
     */
    @Nullable
    public Integer tryExtractResponseStatus(RestClientException e) {
        RestClientResponseException rcex = extractRestClientResponseException(e);
        if (rcex == null) {
            return null;
        }

        return rcex.getRawStatusCode();
    }


    /**
     * Вытаскиваем RestClientResponseException из RestClientException
     *
     * @param e RestClientException
     *
     * @return RestClientResponseException или null, если нет
     */
    @Nullable
    private RestClientResponseException extractRestClientResponseException(RestClientException e) {
        Throwable cause = e.getCause();
        if (cause == null) {
            return null;
        }
        if (!(cause instanceof RestClientResponseException)) {
            return null;
        }

        return (RestClientResponseException) cause;
    }

    // </editor-fold>
}
