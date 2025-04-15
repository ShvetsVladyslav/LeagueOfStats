package com.los.leagueofstats.services.integration.riot.exceptions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class RiotApiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    protected String errCode;
    protected String errText;
    protected Integer respStatus;

    protected Object payload;

    public RiotApiException() {
    }

    public RiotApiException(String message) {
        super(message);
    }

    public RiotApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public RiotApiException(Throwable cause) {
        super(cause);
    }

    @Builder
    public RiotApiException(String errCode,
                           String errText,
                           Integer respStatus,
                           Object payload,
                           String message,
                           Throwable cause) {
        super(message, cause);
        this.errCode = errCode;
        this.errText = errText;
        this.respStatus = respStatus;
        this.payload = payload;
    }

    public static RiotApiException newInstance(
            String errCode,
            String errText,
            Object payload) {
        return RiotApiException.builder()
                .errCode(errCode)
                .errText(errText)
                .payload(payload)
                .build();
    }

}
