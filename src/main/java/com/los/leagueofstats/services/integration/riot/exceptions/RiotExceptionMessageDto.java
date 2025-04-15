package com.los.leagueofstats.services.integration.riot.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RiotExceptionMessageDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("status_code")
    private String errCode;
    @JsonProperty("message")
    private String errText;
    @JsonIgnore
    private Integer respStatus;
}
