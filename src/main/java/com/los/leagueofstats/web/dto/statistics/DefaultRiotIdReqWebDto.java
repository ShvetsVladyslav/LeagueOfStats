package com.los.leagueofstats.web.dto.statistics;

import com.los.leagueofstats.services.integration.lol.enums.LolRegion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DefaultRiotIdReqWebDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String username;
    @NotBlank
    private String tag;
    @NotNull
    private LolRegion region;
}
