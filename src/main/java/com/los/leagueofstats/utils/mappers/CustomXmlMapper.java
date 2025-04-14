package com.los.leagueofstats.utils.mappers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static org.springframework.boot.convert.ApplicationConversionService.configure;

public class CustomXmlMapper extends XmlMapper {

    public CustomXmlMapper() {
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        registerModule(new CustomXmlModule());
        registerModule(new JavaTimeModule());
        registerModule(new GuavaModule());
    }
}