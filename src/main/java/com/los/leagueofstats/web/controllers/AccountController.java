package com.los.leagueofstats.web.controllers;

import com.los.leagueofstats.services.integration.riot.RiotApiService;
import com.los.leagueofstats.services.integration.riot.dto.RiotAccountResDto;
import com.los.leagueofstats.services.integration.riot.enums.RiotRegion;
import com.los.leagueofstats.web.dto.CommonWrapperResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/account", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {

    private final RiotApiService riotApiService;

    @GetMapping("/get")
    public CommonWrapperResDto<RiotAccountResDto> getRiotAccountData(
            @RequestParam(value = "username") String username,
            @RequestParam(value = "tag") String tag,
            @RequestParam(value = "region") RiotRegion region) {
        return new CommonWrapperResDto<>(riotApiService.getRiotAccountByRiotId(username, tag, region));
    }

    @GetMapping
    public int summ(@RequestParam(value = "a") Integer a,
                    @RequestParam(value = "b") Integer b) {
        return a + b;
    }
}
