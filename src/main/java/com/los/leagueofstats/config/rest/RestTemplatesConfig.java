package com.los.leagueofstats.config.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.los.leagueofstats.config.ObjectMapperConfig.CUSTOM_JSON_MAPPER_NAME;
import static com.los.leagueofstats.config.ObjectMapperConfig.CUSTOM_XML_MAPPER_NAME;
import static com.los.leagueofstats.utils.CollectionUtilities.addIfNotExistsByType;
import static com.los.leagueofstats.utils.CollectionUtilities.moveToEndOfListByType;
import static com.los.leagueofstats.utils.SpringUtils.setJacksonObjectMapper;

@Configuration
public class RestTemplatesConfig {

    /** converters for RestTemplate */
    public static final String REST_TEMPLATE_MESSAGE_CONVERTERS = "forRestTemplateMessageConverters";

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.MINUTES)
                .build();
    }

    @Bean
    public OkHttp3ClientHttpRequestFactory okHttp3ClientHttpRequestFactory(OkHttpClient okHttpClient) {
        return new OkHttp3ClientHttpRequestFactory(okHttpClient);
    }

    @Bean
    public RestTemplateBuilder restTemplateBuilder(
            OkHttp3ClientHttpRequestFactory clientHttpRequestFactory,
            @Qualifier(REST_TEMPLATE_MESSAGE_CONVERTERS) List<HttpMessageConverter<?>> messageConverters) {
        return new RestTemplateBuilder()
                .messageConverters(messageConverters)
                .requestFactory(() -> clientHttpRequestFactory);
    }

    @Bean
    @Scope("prototype")
    public RestTemplate getRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }


    /**
     * Converters for RestTemplate-s
     *
     * @param customJsonMapper json mapper
     * @param customXmlMapper  xml mapper
     * @return converters
     */
    @Bean(REST_TEMPLATE_MESSAGE_CONVERTERS)
    public List<HttpMessageConverter<?>> forRestTemplateMessageConverters(
            @Qualifier(CUSTOM_JSON_MAPPER_NAME) ObjectMapper customJsonMapper,
            @Qualifier(CUSTOM_XML_MAPPER_NAME) ObjectMapper customXmlMapper) {
        // get converters from resttemplate
        List<HttpMessageConverter<?>> converters = new RestTemplate().getMessageConverters();

        // add json converter if not exists
        addIfNotExistsByType(converters, new MappingJackson2HttpMessageConverter());
        // set custom ObjectMapper to json and xml converters
        setJacksonObjectMapper(converters, customJsonMapper, MappingJackson2HttpMessageConverter.class);
        setJacksonObjectMapper(converters, customXmlMapper, MappingJackson2XmlHttpMessageConverter.class);

        // move below converters to end of list (priority sorting)
        moveToEndOfListByType(converters, MappingJackson2XmlHttpMessageConverter.class);
        moveToEndOfListByType(converters, Jaxb2RootElementHttpMessageConverter.class);
        return converters;
    }

}
