package com.los.leagueofstats.services.integration.lol.exceptions;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Кастомное исключение для обработки ошибок при вызовах Riot API.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class RiotApiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** Код ошибки (внутренний или внешний) */
    protected String errCode;
    /** Текст ошибки (понятное описание) */
    protected String errText;
    /** HTTP-статус ответа от Riot API */
    protected Integer respStatus;
    /** Дополнительные данные ошибки (может быть null) */
    protected Object payload;

    /** Пустой конструктор */
    public RiotApiException() {
    }

    /** Конструктор с сообщением */
    public RiotApiException(String message) {
        super(message);
    }

    /** Конструктор с сообщением и причиной */
    public RiotApiException(String message, Throwable cause) {
        super(message, cause);
    }

    /** Конструктор с причиной */
    public RiotApiException(Throwable cause) {
        super(cause);
    }

    /**
     * Полный конструктор исключения.
     *
     * @param errCode код ошибки
     * @param errText текст ошибки
     * @param respStatus HTTP статус
     * @param payload тело ошибки
     * @param message сообщение исключения
     * @param cause причина
     */
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

    /**
     * Быстрое создание исключения через статический метод.
     *
     * @param errCode код ошибки
     * @param errText текст ошибки
     * @param payload тело ошибки
     * @return готовое исключение
     */
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