package com.los.leagueofstats.utils.mappers;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;

import java.time.LocalDate;

public class CustomXmlModule extends JacksonXmlModule {

    public CustomXmlModule() {
        super.setDefaultUseWrapper(false);
    }
}