package com.los.leagueofstats.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.los.leagueofstats.utils.mappers.CustomJsonMapper;
import com.los.leagueofstats.utils.mappers.CustomXmlMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ObjectMapperConfig {

    public static final String CUSTOM_JSON_MAPPER_NAME = "customJsonMapper";
    public static final String CUSTOM_XML_MAPPER_NAME = "customXmlMapper";

    @Bean(CUSTOM_JSON_MAPPER_NAME)
    @Primary
    public ObjectMapper jacksonJsonObjectMapper() {
        return new CustomJsonMapper();
    }

    @Bean(CUSTOM_XML_MAPPER_NAME)
    public XmlMapper jacksonXmlObjectMapper() {
        return new CustomXmlMapper();
    }

}
