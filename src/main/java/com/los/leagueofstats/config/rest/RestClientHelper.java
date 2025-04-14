package com.los.leagueofstats.config.rest;

import com.google.common.collect.Lists;
import com.los.leagueofstats.utils.JsonUtil;
import org.apache.http.client.utils.URIBuilder;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

abstract class RestClientHelper {

    private RestTemplate restTemplate;

    protected void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    /**
     * @since 0.11.19.0
     */
    private URI createUri(String url,
                          Map<String, String> params,
                          Map<String, List<String>> paramsL) throws URISyntaxException {
        final URIBuilder uriBuilder = new URIBuilder(url);

        if (!CollectionUtils.isEmpty(params)) {
            params.forEach(uriBuilder :: setParameter);
        }
        if (!CollectionUtils.isEmpty(paramsL)) {
            paramsL.forEach((key, value) -> value.forEach(s -> uriBuilder.addParameter(key, s)));
        }

        return uriBuilder.build();
    }

    /**
     * @since 0.0.1
     */
    private URI createUri(String url,
                          Map<String, String> params) throws URISyntaxException {
        return createUri(url, params, null);
    }

    /**
     * @since 0.0.1
     */
    private HttpEntity createHttpEntity(Object body,
                                        Map<String, String> headers) {
        HttpHeaders httpHeaders = null;
        if (!CollectionUtils.isEmpty(headers)) {
            httpHeaders = new HttpHeaders();
            for (Map.Entry<String, String> header : headers.entrySet()) {
                httpHeaders.add(header.getKey(), header.getValue());
            }
        }

        return new HttpEntity<>(body, httpHeaders);
    }

    /**
     * Common method, that do work of preparing and sending message to rest-service.
     *
     * @param url          url-address to send message.
     * @param responseType type of expected response.
     * @param httpMethod   http-method of sending request.
     * @param body         body of message.
     * @param params       parameters for adding to url.
     * @param headers      headers for adding to request.
     *
     * @return answer with expected answer type.
     *
     * @since 0.0.1
     */
    private <T> T doSend(String url,
                         Class<T> responseType,
                         HttpMethod httpMethod,
                         Object body,
                         Map<String, String> params,
                         Map<String, String> headers) {
        try {
            final URI uri = createUri(url, params);
            final HttpEntity httpEntity = createHttpEntity(body, headers);

            return restTemplate.exchange(uri, httpMethod, httpEntity, responseType).getBody();
        } catch (Exception e) {
            throw new RestClientException("", e);
        }
    }

    /**
     * Common method, that do work of preparing and sending message to rest-service.
     *
     * @param url          url-address to send message.
     * @param responseType type of expected response.
     * @param httpMethod   http-method of sending request.
     * @param body         body of message.
     * @param params       parameters for adding to url.
     * @param paramsL      parameters for adding to url as list.
     * @param headers      headers for adding to request.
     *
     * @return answer with expected answer type.
     *
     * @since 0.0.1
     */
    private <T> T doSend(String url,
                         Class<T> responseType,
                         HttpMethod httpMethod,
                         Object body,
                         Map<String, String> params,
                         Map<String, List<String>> paramsL,
                         Map<String, String> headers) {
        try {
            final URI uri = createUri(url, params, paramsL);
            final HttpEntity httpEntity = createHttpEntity(body, headers);

            return restTemplate.exchange(uri, httpMethod, httpEntity, responseType).getBody();
        } catch (Exception e) {
            throw new RestClientException("", e);
        }
    }

    /**
     * Common method, that do work of preparing and sending message to rest-service.
     *
     * @param url          url-address to send message.
     * @param responseType type of expected response.
     * @param httpMethod   http-method of sending request.
     * @param body         body of message.
     * @param params       parameters for adding to url.
     * @param headers      headers for adding to request.
     *
     * @return answer with expected answer type.
     *
     * @since 0.0.1
     */
    private <T> T doSend(String url,
                         ParameterizedTypeReference<T> responseType,
                         HttpMethod httpMethod,
                         Object body,
                         Map<String, String> params,
                         Map<String, String> headers) {
        try {
            final URI uri = createUri(url, params);
            final HttpEntity httpEntity = createHttpEntity(body, headers);

            return restTemplate.exchange(uri, httpMethod, httpEntity, responseType).getBody();
        } catch (Exception e) {
            throw new RestClientException("", e);
        }
    }

    /**
     * Common method, that do work of preparing and sending message to rest-service.
     *
     * @param url          url-address to send message.
     * @param responseType type of expected response.
     * @param httpMethod   http-method of sending request.
     * @param body         body of message.
     * @param params       parameters for adding to url.
     * @param paramsL      parameters for adding to url as list.
     * @param headers      headers for adding to request.
     *
     * @return answer with expected answer type.
     *
     * @since 1.0.32.0
     */
    private <T> T doSend(String url,
                         ParameterizedTypeReference<T> responseType,
                         HttpMethod httpMethod,
                         Object body,
                         Map<String, String> params,
                         Map<String, List<String>> paramsL,
                         Map<String, String> headers) {
        try {
            final URI uri = createUri(url, params, paramsL);
            final HttpEntity httpEntity = createHttpEntity(body, headers);

            return restTemplate.exchange(uri, httpMethod, httpEntity, responseType).getBody();
        } catch (Exception e) {
            throw new RestClientException("", e);
        }
    }

    /**
     * Common method, that do work of preparing and sending message to rest-service.
     *
     * @param url        url-address to send message.
     * @param stream     stream to write answer to.
     * @param httpMethod http-method of sending request.
     * @param params     parameters for adding to url.
     * @param headers    headers for adding to request.
     *
     * @since 0.11.11.0
     */
    private void doSend(String url,
                        OutputStream stream,
                        HttpMethod httpMethod,
                        Object body,
                        Map<String, String> params,
                        Map<String, String> headers) {
        try {
            final URI uri = createUri(url, params);
            restTemplate.execute(uri, httpMethod,
                    (ClientHttpRequest requestCallback) -> {
                        requestCallback.getHeaders().putAll(headers.entrySet().stream()
                                .collect(
                                        Collectors.toMap(Map.Entry :: getKey,
                                                e -> Lists.newArrayList(
                                                        e.getValue()))));
                        if (body != null) {
                            requestCallback.getBody().write(JsonUtil.writeValueAsBytes(body));
                        }
                    },
                    responseExtractor -> {
                        IOUtils.copy(responseExtractor.getBody(), stream);
                        return null;
                    });
        } catch (Exception e) {
            throw new RestClientException("", e);
        }
    }

    /**
     * Send message to rest-service (GET) with expected answer type.
     *
     * @param url          url-address to send message.
     * @param responseType type of expected response.
     * @param params       parameters for adding to url.
     * @param headers      headers for adding to request.
     *
     * @return answer with expected answer type.
     *
     * @since 0.0.1
     */
    protected <T> T get(String url,
                        Class<T> responseType,
                        Map<String, String> params,
                        Map<String, String> headers) {
        return doSend(url, responseType, HttpMethod.GET, null, params, headers);
    }

    /**
     * Send message to rest-service (GET) with expected answer type.
     *
     * @param url          url-address to send message.
     * @param responseType type of expected response.
     * @param params       parameters for adding to url.
     * @param paramsL      parameters for adding to url as list.
     * @param headers      headers for adding to request.
     *
     * @return answer with expected answer type.
     *
     * @since 0.11.19.0
     */
    protected <T> T get(String url,
                        Class<T> responseType,
                        Map<String, String> params,
                        Map<String, List<String>> paramsL,
                        Map<String, String> headers) {
        return doSend(url, responseType, HttpMethod.GET, null, params, paramsL, headers);
    }

    /**
     * Send message to rest-service (GET) with expected answer type.
     *
     * @param url          url-address to send message.
     * @param responseType type of expected response.
     * @param params       parameters for adding to url.
     * @param headers      headers for adding to request.
     *
     * @return answer with expected answer type.
     *
     * @since 0.0.1
     */
    protected <T> T get(String url,
                        ParameterizedTypeReference<T> responseType,
                        Map<String, String> params,
                        Map<String, String> headers) {
        return doSend(url, responseType, HttpMethod.GET, null, params, headers);
    }

    /**
     * Send message to rest-service (GET) with expected answer type.
     *
     * @param url          url-address to send message.
     * @param responseType type of expected response.
     * @param params       parameters for adding to url.
     * @param paramsL      parameters for adding to url as list.
     * @param headers      headers for adding to request.
     *
     * @return answer with expected answer type.
     *
     * @since 1.0.32.0
     */
    protected <T> T get(String url,
                        ParameterizedTypeReference<T> responseType,
                        Map<String, String> params,
                        Map<String, List<String>> paramsL,
                        Map<String, String> headers) {
        return doSend(url, responseType, HttpMethod.GET, null, params, paramsL, headers);
    }

    /**
     * To send message to rest-service (GET) with writing answer to stream.
     *
     * @param url     url-address to send message.
     * @param params  parameters for adding to url.
     * @param headers headers for adding to request.
     * @param stream  stream to write to.
     *
     * @since 0.11.11.0
     */
    protected void get(String url,
                       Map<String, String> params,
                       Map<String, String> headers,
                       OutputStream stream) {
        doSend(url, stream, HttpMethod.GET, null, params, headers);
    }

    /**
     * Send message to rest-service (POST) with expected answer type.
     *
     * @param url          url-address to send message.
     * @param responseType type of expected response.
     * @param body         body of message.
     * @param params       parameters for adding to url.
     * @param headers      headers for adding to request.
     *
     * @return answer with expected answer type.
     *
     * @since 0.0.1
     */
    protected <T> T post(String url,
                         Class<T> responseType,
                         Map<String, String> params,
                         Map<String, String> headers,
                         Object body) {
        return doSend(url, responseType, HttpMethod.POST, body, params, headers);
    }

    /**
     * Send message to rest-service (POST) with expected answer type.
     *
     * @param url          url-address to send message.
     * @param responseType type of expected response.
     * @param body         body of message.
     * @param params       parameters for adding to url.
     * @param headers      headers for adding to request.
     *
     * @return answer with expected answer type.
     *
     * @since 0.0.1
     */
    protected <T> T post(String url,
                         ParameterizedTypeReference<T> responseType,
                         Map<String, String> params,
                         Map<String, String> headers,
                         Object body) {
        return doSend(url, responseType, HttpMethod.POST, body, params, headers);
    }

    /**
     * Send message to rest-service (POST) with writing answer into stream.
     *
     * @param url     url-address to send message.
     * @param body    body of message.
     * @param params  parameters for adding to url.
     * @param headers headers for adding to request.
     * @param stream  stream to write answer to.
     *
     * @since 0.11.17.0
     */
    protected void post(String url,
                        Map<String, String> params,
                        Map<String, String> headers,
                        Object body,
                        OutputStream stream) {
        doSend(url, stream, HttpMethod.POST, body, params, headers);
    }

    /**
     * Send message to rest-service (PUT) with expected answer type.
     *
     * @param url          url-address to send message.
     * @param responseType type of expected response.
     * @param body         body of message.
     * @param params       parameters for adding to url.
     * @param headers      headers for adding to request.
     *
     * @return answer with expected answer type.
     */
    protected <T> T put(String url,
                        Class<T> responseType,
                        Map<String, String> params,
                        Map<String, String> headers,
                        Object body) {
        return doSend(url, responseType, HttpMethod.PUT, body, params, headers);
    }

    /**
     * Send message to rest-service (PUT) with expected answer type.
     *
     * @param url          url-address to send message.
     * @param responseType type of expected response.
     * @param body         body of message.
     * @param params       parameters for adding to url.
     * @param headers      headers for adding to request.
     *
     * @return answer with expected answer type.
     */
    protected <T> T put(String url,
                        ParameterizedTypeReference<T> responseType,
                        Map<String, String> params,
                        Map<String, String> headers,
                        Object body) {
        return doSend(url, responseType, HttpMethod.PUT, body, params, headers);
    }

    /**
     * Send message to rest-service (PATCH) with expected answer type.
     *
     * @param url url-address to send message.
     * @param responseType type of expected response.
     * @param body body of message.
     * @param params parameters for adding to url.
     * @param headers headers for adding to request.
     *
     * @return answer with expected answer type.
     */
    protected <T> T patch(
            String url,
            Class<T> responseType,
            Map<String, String> params,
            Map<String, String> headers,
            Object body) {
        return doSend(url, responseType, HttpMethod.PATCH, body, params, headers);
    }

    /**
     * Send message to rest-service (PATCH) with expected answer type.
     *
     * @param url url-address to send message.
     * @param responseType type of expected response.
     * @param body body of message.
     * @param params parameters for adding to url.
     * @param headers headers for adding to request.
     *
     * @return answer with expected answer type.
     */
    protected <T> T patch(
            String url,
            ParameterizedTypeReference<T> responseType,
            Map<String, String> params,
            Map<String, String> headers,
            Object body) {
        return doSend(url, responseType, HttpMethod.PATCH, body, params, headers);
    }

    /**
     * Send message to rest-service (DELETE) with expected answer type.
     *
     * @param url          url-address to send message.
     * @param responseType type of expected response.
     * @param params       parameters for adding to url.
     * @param headers      headers for adding to request.
     * @param body         body of message.
     *
     * @return answer with expected answer type.
     *
     * @since 0.11.9.0
     */
    protected <T> T delete(String url,
                           Class<T> responseType,
                           Map<String, String> params,
                           Map<String, String> headers,
                           Object body) {
        return doSend(url, responseType, HttpMethod.DELETE, body, params, headers);
    }

    /**
     * Send message to rest-service (DELETE) with expected answer type.
     *
     * @param url          url-address to send message.
     * @param responseType type of expected response.
     * @param params       parameters for adding to url.
     * @param headers      headers for adding to request.
     *
     * @return answer with expected answer type.
     *
     * @since 0.11.9.0
     */
    protected <T> T delete(String url,
                           ParameterizedTypeReference<T> responseType,
                           Map<String, String> params,
                           Map<String, String> headers) {
        return doSend(url, responseType, HttpMethod.DELETE, null, params, headers);
    }

    /**
     * Send message to rest-service (DELETE) with expected answer type.
     *
     * @param url          url-address to send message.
     * @param responseType type of expected response.
     * @param params       parameters for adding to url.
     * @param headers      headers for adding to request.
     *
     * @return answer with expected answer type.
     *
     * @since 1.0.42.1
     */
    protected <T> T delete(String url,
                           ParameterizedTypeReference<T> responseType,
                           Map<String, String> params,
                           Map<String, String> headers,
                           Object body) {
        return doSend(url, responseType, HttpMethod.DELETE, body, params, headers);
    }

}
