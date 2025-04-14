package com.los.leagueofstats.config.rest;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public interface RestClient {

    <T> T get(String url,
              Map<String, String> params,
              Map<String, String> headers,
              Class<T> responseType);

    <T> T get(String url,
              Map<String, String> params,
              Map<String, List<String>> paramsL,
              Map<String, String> headers,
              Class<T> responseType);

    <T> T get(String url,
              Map<String, String> params,
              Map<String, String> headers,
              ParameterizedTypeReference<T> responseType);

    <T> T get(String url,
              Map<String, String> params,
              Map<String, List<String>> paramsL,
              Map<String, String> headers,
              ParameterizedTypeReference<T> responseType);

    void get(String url,
             Map<String, String> params,
             Map<String, String> headers,
             OutputStream stream);

    <T> T get(String url,
              Map<String, String> headers,
              Class<T> responseType);

    <T> T get(String url,
              Map<String, String> headers,
              ParameterizedTypeReference<T> responseType);

    void get(String url,
             Map<String, String> headers,
             OutputStream stream);

    <T> T post(String url,
               Map<String, String> params,
               Map<String, String> headers,
               Object body,
               Class<T> responseType);

    void post(String url,
              Map<String, String> params,
              Map<String, String> headers,
              Object body,
              OutputStream stream);

    <T> T post(String url,
               Map<String, String> headers,
               Object body,
               ParameterizedTypeReference<T> responseType);

    void post(String url,
              Map<String, String> headers,
              Object body,
              OutputStream stream);

    <T> T post(String url,
               Map<String, String> headers,
               Object body,
               Class<T> responseType);

    <T> T post(String url,
               Map<String, String> params,
               Map<String, String> headers,
               Object body,
               ParameterizedTypeReference<T> responseType);

    <T> T put(String url,
              Map<String, String> params,
              Map<String, String> headers,
              Object body,
              Class<T> responseType);

    <T> T put(String url,
              Map<String, String> params,
              Map<String, String> headers,
              Object body,
              ParameterizedTypeReference<T> responseType);

    <T> T put(String url,
              Map<String, String> headers,
              Object body,
              Class<T> responseType);

    <T> T put(String url,
              Map<String, String> headers,
              Object body,
              ParameterizedTypeReference<T> responseType);

    <T> T patch(
            String url,
            Map<String, String> params,
            Map<String, String> headers,
            Object body,
            Class<T> responseType);

    <T> T patch(
            String url,
            Map<String, String> params,
            Map<String, String> headers,
            Object body,
            ParameterizedTypeReference<T> responseType);

    <T> T patch(
            String url,
            Map<String, String> headers,
            Object body,
            Class<T> responseType);

    <T> T patch(
            String url,
            Map<String, String> headers,
            Object body,
            ParameterizedTypeReference<T> responseType);

    <T> T delete(String url,
                 Map<String, String> params,
                 Map<String, String> headers,
                 Class<T> responseType);

    <T> T delete(String url,
                 Map<String, String> params,
                 Map<String, String> headers,
                 Object body,
                 Class<T> responseType);

    <T> T delete(String url,
                 Map<String, String> params,
                 Map<String, String> headers,
                 ParameterizedTypeReference<T> responseType);

    <T> T delete(String url,
                 Map<String, String> params,
                 Map<String, String> headers,
                 Object body,
                 ParameterizedTypeReference<T> responseType);

    void setAnchorForLog(String name);

    void setErrorHandler(ResponseErrorHandler handler);

    RestTemplate getRestTemplate();

    void initPropertiesLogging();
}
