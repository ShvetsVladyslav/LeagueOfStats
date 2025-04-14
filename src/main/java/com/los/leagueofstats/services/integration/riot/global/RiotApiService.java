package com.los.leagueofstats.services.integration.riot.global;

import com.los.leagueofstats.config.rest.RestClient;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


@Service
@Log4j2
public class RiotApiService {

    // <editor-fold defaultstate="collapsed" desc="***Util elements***">



    // </editor-fold>

    private final RestClient restClient;

    // <editor-fold defaultstate="collapsed" desc="***Init and setters***">

    @Autowired
    public RiotApiService(RestClient restClient) {
        this.restClient = restClient;

        this.restClient.setAnchorForLog(this.getClass().getSimpleName());
    }

    // </editor-fold>



}
