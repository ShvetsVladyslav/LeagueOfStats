package com.los.leagueofstats.services.integration.lol.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * DTO с описанием ошибки от Riot API.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RiotExceptionMessageDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Код ошибки (поле status_code в ответе Riot) */
    @JsonProperty("status_code")
    private String errCode;

    /** Текст ошибки (поле message в ответе Riot) */
    @JsonProperty("message")
    private String errText;

    /** HTTP-статус ответа (заполняется вручную, не приходит с сервера) */
    @JsonIgnore
    private Integer respStatus;
}
