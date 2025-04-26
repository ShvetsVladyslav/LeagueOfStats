package com.los.leagueofstats.services.integration.lol;

import com.los.leagueofstats.config.riot.RiotApiConfProps;
import com.los.leagueofstats.services.integration.lol.exceptions.RiotApiException;
import com.los.leagueofstats.services.integration.lol.exceptions.RiotExceptionMessageDto;
import com.los.leagueofstats.utils.JsonUtil;
import com.los.leagueofstats.utils.Region;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Nullable;
import java.util.Collections;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Хелпер для работы с Riot API:
 * — Формирует URL
 * — Собирает заголовки
 * — Обрабатывает ошибки
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class RiotApiHelper {

    /** Конфигурация с Riot API токеном */
    private final RiotApiConfProps riotApiConfProps;

    // <editor-fold defaultstate="collapsed" desc="*** URL Utils ***">

    @Setter
    @Value("${service.riot.base-url}")
    private String riotApiBaseUrl;

    /**
     * Формирует финальный URL Riot API с подставленным регионом.
     *
     * @param region регион Riot (например, EUROPE)
     * @return готовый URL
     */
    public String getRiotApiBaseUrl(Region region) {
        checkArgument(region != null, "Region is not specified!");

        return UriComponentsBuilder.fromUriString(riotApiBaseUrl)
                .buildAndExpand(region.getId())
                .toUriString();
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="*** HEADER Utils ***">

    public static final String RIOT_TOKEN_HEADER = "X-Riot-Token";

    /**
     * Формирует стандартные заголовки для вызова Riot API.
     *
     * @return заголовки с токеном и контент-типом
     */
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

    /**
     * Пытается достать JSON-ошибку от Riot API из исключения.
     *
     * @param e RestClientException
     * @return объект с деталями ошибки или null
     */
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
     * Пытается получить HTTP статус из исключения.
     *
     * @param e RestClientException
     * @return HTTP статус или null
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
     * Вытаскивает RestClientResponseException из RestClientException.
     *
     * @param e RestClientException
     * @return RestClientResponseException или null
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

    public void handleDefaultRiotException(RiotApiException exception) throws RiotApiException {
        HttpStatus status = HttpStatus.valueOf(exception.getRespStatus());

        if (HttpStatus.TOO_MANY_REQUESTS.equals(status)) {
            log.error("Exceeded Riot API limit: " + exception.getErrText());
            throw exception;
        } else if (status.is5xxServerError()) {
            log.error("Got Riot API 5xx error: " + exception.getErrText());
            throw exception;
        } else {
            log.error("UNEXPECTED RIOT ERROR: " + exception.getErrText() + " ERROR CODE: " + exception.getRespStatus());
            throw exception;
        }
    }

    // </editor-fold>
}
