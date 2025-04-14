package com.los.leagueofstats.config.rest;

import com.google.common.collect.Lists;
import com.los.leagueofstats.config.AppProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.OutputStream;
import java.util.*;

import static com.los.leagueofstats.config.rest.RestTemplatesConfig.REST_TEMPLATE_MESSAGE_CONVERTERS;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Scope("prototype")
@Log4j2
@Service
public class RestClientImpl extends RestClientHelper implements RestClient {

    /**
     * To create instance of {@link RestClientImpl} with parameters.
     *
     * @return instance of {@link RestClientImpl} with parameters.
     *
     * @see RestClientFactory#newRestTemplate(int, int, int, int, int, int, boolean, boolean, List, List)
     * @since 0.0.1
     */
    public static RestClient getWithParams(String maxConnections,
                                           String maxConnectionsPerRoute,
                                           String connectTimeout,
                                           String connectionRequestTimeout,
                                           String timeoutResponse,
                                           String secondsToLive,
                                           boolean sslDisabled,
                                           List<ClientHttpRequestInterceptor> interceptors,
                                           List<HttpMessageConverter<?>> messageConverters) {
        return new RestClientImpl(maxConnections,
                maxConnectionsPerRoute,
                connectTimeout,
                connectionRequestTimeout,
                timeoutResponse,
                secondsToLive,
                sslDisabled,
                true,
                interceptors,
                messageConverters);
    }

    /**
     * To create instance of {@link RestClientImpl} with parameters.
     *
     * @return instance of {@link RestClientImpl} with parameters.
     *
     * @see RestClientFactory#newRestTemplate(int, int, int, int, int, int, boolean, boolean, List, List)
     * @since 0.11.18.0
     */
    public static RestClient getWithParams(String maxConnections,
                                           String maxConnectionsPerRoute,
                                           String connectTimeout,
                                           String connectionRequestTimeout,
                                           String timeoutResponse,
                                           String secondsToLive,
                                           boolean sslDisabled,
                                           boolean bufferBody,
                                           List<ClientHttpRequestInterceptor> interceptors,
                                           List<HttpMessageConverter<?>> messageConverters) {
        return new RestClientImpl(maxConnections,
                maxConnectionsPerRoute,
                connectTimeout,
                connectionRequestTimeout,
                timeoutResponse,
                secondsToLive,
                sslDisabled,
                bufferBody,
                interceptors,
                messageConverters);
    }

    /**
     * To get instance of {@link RestTemplate} with parameters.
     *
     * @return instance of {@link RestTemplate} with parameters.
     *
     * @see RestClientFactory#newRestTemplate(int, int, int, int, int, int)
     * @since 0.0.1
     * @deprecated you can use this method, but it is better to use
     * {@link RestClientImpl#getWithParams(String, String, String, String, String, String, boolean, boolean, List, List)}
     */
    @Deprecated
    public static RestTemplate getTemplateWithParams(int maxConnections,
                                                     int maxConnectionsPerRoute,
                                                     int connectTimeout,
                                                     int connectionRequestTimeout,
                                                     int timeoutResponse,
                                                     int secondsToLive,
                                                     List<ClientHttpRequestInterceptor> interceptors,
                                                     List<HttpMessageConverter<?>> messageConverters) {

        return createRestTemplate(maxConnections, maxConnectionsPerRoute, connectTimeout, connectionRequestTimeout,
                timeoutResponse, secondsToLive, false, true, interceptors, messageConverters);
    }

    @Override
    public <T> T get(String url,
                     Map<String, String> params,
                     Map<String, String> headers,
                     Class<T> responseType) {
        return super.get(url, responseType, params, headers);
    }

    @Override
    public <T> T get(String url,
                     Map<String, String> params,
                     Map<String, List<String>> paramsL,
                     Map<String, String> headers,
                     Class<T> responseType) {
        return super.get(url, responseType, params, paramsL, headers);
    }

    @Override
    public <T> T get(String url,
                     Map<String, String> params,
                     Map<String, String> headers,
                     ParameterizedTypeReference<T> responseType) {
        return super.get(url, responseType, params, headers);
    }

    @Override
    public <T> T get(String url,
                     Map<String, String> params,
                     Map<String, List<String>> paramsL,
                     Map<String, String> headers,
                     ParameterizedTypeReference<T> responseType) {
        return super.get(url, responseType, params, paramsL, headers);
    }

    @Override
    public void get(String url,
                    Map<String, String> params,
                    Map<String, String> headers,
                    OutputStream stream) {
        super.get(url, params, headers, stream);
    }

    @Override
    public <T> T get(String url,
                     Map<String, String> headers,
                     Class<T> responseType) {
        return super.get(url, responseType, null, headers);
    }

    @Override
    public <T> T get(String url,
                     Map<String, String> headers,
                     ParameterizedTypeReference<T> responseType) {
        return super.get(url, responseType, null, headers);
    }

    @Override
    public void get(String url,
                    Map<String, String> headers,
                    OutputStream stream) {
        super.get(url, null, headers, stream);
    }

    @Override
    public <T> T post(String url,
                      Map<String, String> params,
                      Map<String, String> headers,
                      Object body,
                      Class<T> responseType) {
        return super.post(url, responseType, params, headers, body);
    }

    @Override
    public <T> T post(String url,
                      Map<String, String> params,
                      Map<String, String> headers,
                      Object body,
                      ParameterizedTypeReference<T> responseType) {
        return super.post(url, responseType, params, headers, body);
    }

    @Override
    public <T> T post(String url,
                      Map<String, String> headers,
                      Object body,
                      ParameterizedTypeReference<T> responseType) {
        return this.post(url, null, headers, body, responseType);
    }

    @Override
    public <T> T post(String url,
                      Map<String, String> headers,
                      Object body,
                      Class<T> responseType) {
        return this.post(url, null, headers, body, responseType);
    }

    @Override
    public void post(String url,
                     Map<String, String> params,
                     Map<String, String> headers,
                     Object body,
                     OutputStream stream) {
        super.post(url, params, headers, body, stream);
    }

    @Override
    public void post(String url,
                     Map<String, String> headers,
                     Object body,
                     OutputStream stream) {
        super.post(url, null, headers, body, stream);
    }

    @Override
    public <T> T put(String url,
                     Map<String, String> params,
                     Map<String, String> headers,
                     Object body,
                     Class<T> responseType) {
        return super.put(url, responseType, params, headers, body);
    }

    @Override
    public <T> T put(String url,
                     Map<String, String> params,
                     Map<String, String> headers,
                     Object body,
                     ParameterizedTypeReference<T> responseType) {
        return super.put(url, responseType, params, headers, body);
    }

    @Override
    public <T> T put(String url,
                     Map<String, String> headers,
                     Object body,
                     Class<T> responseType) {
        return super.put(url, responseType, null, headers, body);
    }

    @Override
    public <T> T put(String url,
                     Map<String, String> headers,
                     Object body,
                     ParameterizedTypeReference<T> responseType) {
        return super.put(url, responseType, null, headers, body);
    }

    @Override
    public <T> T patch(
            String url,
            Map<String, String> params,
            Map<String, String> headers,
            Object body,
            Class<T> responseType) {
        return super.patch(url, responseType, params, headers, body);
    }

    @Override
    public <T> T patch(
            String url,
            Map<String, String> params,
            Map<String, String> headers,
            Object body,
            ParameterizedTypeReference<T> responseType) {
        return super.patch(url, responseType, params, headers, body);
    }

    @Override
    public <T> T patch(
            String url,
            Map<String, String> headers,
            Object body,
            Class<T> responseType) {
        return super.patch(url, responseType, null, headers, body);
    }

    @Override
    public <T> T patch(
            String url,
            Map<String, String> headers,
            Object body,
            ParameterizedTypeReference<T> responseType) {
        return super.patch(url, responseType, null, headers, body);
    }

    @Override
    public <T> T delete(String url,
                        Map<String, String> params,
                        Map<String, String> headers,
                        Class<T> responseType) {
        return super.delete(url, responseType, params, headers, null);
    }

    @Override
    public <T> T delete(String url,
                        Map<String, String> params,
                        Map<String, String> headers,
                        Object body,
                        Class<T> responseType) {
        return super.delete(url, responseType, params, headers, body);
    }

    @Override
    public <T> T delete(String url,
                        Map<String, String> params,
                        Map<String, String> headers,
                        ParameterizedTypeReference<T> responseType) {
        return super.delete(url, responseType, params, headers);
    }

    @Override
    public <T> T delete(String url,
                        Map<String, String> params,
                        Map<String, String> headers,
                        Object body,
                        ParameterizedTypeReference<T> responseType) {
        return super.delete(url, responseType, params, headers, body);
    }


    /*===Private elements===*/

    private boolean isNullOrZero(Integer integer) {
        return integer == null || integer == 0;
    }

    private static RestTemplate createRestTemplate(Integer maxConnections,
                                                   Integer maxConnectionsPerRoute,
                                                   Integer connectionTimeout,
                                                   Integer connectionRequestTimeout,
                                                   Integer connectionResponseTimeout,
                                                   Integer poolConnectionsTimeout,
                                                   boolean sslDisabled,
                                                   boolean bufferBody,
                                                   List<ClientHttpRequestInterceptor> interceptors,
                                                   List<HttpMessageConverter<?>> messageConverters) {
        return RestClientFactory.newRestTemplate(maxConnections, maxConnectionsPerRoute,
                connectionTimeout, connectionRequestTimeout,
                connectionResponseTimeout, poolConnectionsTimeout, sslDisabled, bufferBody,
                interceptors, messageConverters);
    }

    /**
     * @since 0.8.0
     */
    private List<HttpMessageConverter<?>> prepareConverters(List<HttpMessageConverter<?>> converters) {
        Set<HttpMessageConverter<?>> convSet = new HashSet<>();

        if (messageConverters != null) {
            convSet.addAll(messageConverters);
        }

        if (converters != null) {
            convSet.addAll(converters);
        }

        backLog.put("Message converters", convSet.size());

        return Lists.newArrayList(convSet);
    }

    /**
     * @since 0.8.0
     */
    private List<ClientHttpRequestInterceptor> prepareInterceptors(List<ClientHttpRequestInterceptor> interceptors) {
        Set<ClientHttpRequestInterceptor> intSet = new HashSet<>();

        if (interceptors != null) {
            intSet.addAll(interceptors);
        }

        backLog.put("Interceptors", intSet.size());

        return Lists.newArrayList(intSet);
    }

    /**
     * @since 0.8.0
     */
    private Integer calcConnectionTimeout(String connectionTimeoutString) {
        Integer connectionTimeout = isNotBlank(connectionTimeoutString) ? Integer.valueOf(connectionTimeoutString) : null;
        if (isNullOrZero(connectionTimeout)) {
            connectionTimeout = CONNECTION_TIMEOUT;
            log.trace("Custom property for http connection timeout was not found - default value '{}' will be used!",
                    () -> CONNECTION_TIMEOUT);
        }

        backLog.put("Http connection timeout", connectionTimeout);

        return connectionTimeout;
    }

    /**
     * @since 0.8.0
     */
    private Integer calcMaxConnections(String maxConnectionsString) {
        Integer maxConnections = isNotBlank(maxConnectionsString) ? Integer.valueOf(maxConnectionsString) : null;
        if (isNullOrZero(maxConnections)) {
            maxConnections = MAX_CONNECTIONS;
            log.trace("Custom property for http maximum connections was not found - default value '{}' will be used!",
                    () -> MAX_CONNECTIONS);
        }

        backLog.put("Http maximum connections", maxConnections);

        return maxConnections;
    }

    /**
     * @since 0.8.0
     */
    private Integer calcMaxConnectionsPerRoute(String maxConnectionsPerRouteString) {
        Integer maxConnectionsPerRoute =
                isNotBlank(maxConnectionsPerRouteString) ? Integer.valueOf(maxConnectionsPerRouteString) : null;
        if (isNullOrZero(maxConnectionsPerRoute)) {
            maxConnectionsPerRoute = MAX_CONNECTIONS_PER_ROUTE;
            log.trace(
                    "Custom property for http maximum connections per route was not found - default value '{}' will be used!",
                    () -> MAX_CONNECTIONS_PER_ROUTE);
        }

        backLog.put("Http maximum connections", maxConnectionsPerRoute);

        return maxConnectionsPerRoute;
    }

    /**
     * @since 0.8.0
     */
    private Integer calcConnectionRequestTimeout(String connectionRequestTimeoutString) {
        Integer connectionRequestTimeout =
                isNotBlank(connectionRequestTimeoutString) ? Integer.valueOf(connectionRequestTimeoutString) : null;
        if (isNullOrZero(connectionRequestTimeout)) {
            connectionRequestTimeout = CONNECTION_REQUEST_TIMEOUT;
            log.trace(
                    "Custom property for http connection request timeout was not found - default value '{}' will be used!",
                    () -> CONNECTION_REQUEST_TIMEOUT);
        }

        backLog.put("Http connection request", connectionRequestTimeout);

        return connectionRequestTimeout;
    }

    /**
     * @since 0.8.0
     */
    private Integer calcConnectionResponseTimeout(String connectionResponseTimeoutString) {
        Integer connectionResponseTimeout =
                isNotBlank(connectionResponseTimeoutString) ? Integer.valueOf(connectionResponseTimeoutString) : null;
        if (isNullOrZero(connectionResponseTimeout)) {
            connectionResponseTimeout = CONNECTION_RESPONSE_TIMEOUT;
            log.trace(
                    "Custom property for http connection response timeout was not found - default value '{}' will be used!",
                    () -> CONNECTION_RESPONSE_TIMEOUT);
        }

        backLog.put("Http connection response", connectionResponseTimeout);

        return connectionResponseTimeout;
    }

    /**
     * @since 0.8.0
     */
    private Integer calcPoolConnectionsTimeout(String poolConnectionsTimeoutString) {
        Integer poolConnectionsTimeout =
                isNotBlank(poolConnectionsTimeoutString) ? Integer.valueOf(poolConnectionsTimeoutString) : null;
        if (isNullOrZero(poolConnectionsTimeout)) {
            poolConnectionsTimeout = POOL_CONNECTIONS_TIMEOUT;
            log.trace(
                    "Custom property for http pool connections timeout was not found - default value '{}' will be used!",
                    () -> POOL_CONNECTIONS_TIMEOUT);
        }

        backLog.put("Http pool connections", poolConnectionsTimeout);

        return poolConnectionsTimeout;
    }

    // <editor-fold defaultstate="collapsed" desc="***Util elements***">

    public static final Integer CONNECTION_TIMEOUT = 2000;
    private static final String CONNECTION_TIMEOUT_NAME = "http.connection.timeout";
    public static final Integer MAX_CONNECTIONS = 10;
    private static final String MAX_CONNECTIONS_NAME = "http.max.connections";
    public static final Integer MAX_CONNECTIONS_PER_ROUTE = 10;
    private static final String MAX_CONNECTIONS_PER_ROUTE_NAME = "http.max.connection.per.rout";
    public static final Integer CONNECTION_REQUEST_TIMEOUT = 5000;
    private static final String CONNECTION_REQUEST_TIMEOUT_NAME = "http.connection.request.timeout";
    public static final Integer CONNECTION_RESPONSE_TIMEOUT = 5000;
    private static final String CONNECTION_RESPONSE_TIMEOUT_NAME = "http.connection.response.timeout";
    public static final Integer POOL_CONNECTIONS_TIMEOUT = 86400; //one day in seconds
    private static final String POOL_CONNECTIONS_TIMEOUT_NAME = "http.connection.response.timeout";

    private String serviceName;
    private Map<String, Integer> backLog = new HashMap<>();

    private List<HttpMessageConverter<?>> messageConverters;
    private AppProperties appProperties;

    @Autowired
    @Qualifier(REST_TEMPLATE_MESSAGE_CONVERTERS)
    public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        this.messageConverters = messageConverters;
        log.debug("Message converters for {}: {}",
                RestClientImpl.class :: getCanonicalName,
                () -> messageConverters);
    }

    @Autowired
    public void setAppProperties(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    /**
     * @since 0.8.0
     */
    @Override
    public void setAnchorForLog(String name) {
        RestTemplate restTemplate = super.getRestTemplate();

        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();

        restTemplate.setInterceptors(interceptors);

        this.serviceName = name;

        initPropertiesLogging();
    }

    @Override
    public void initPropertiesLogging() {
        log.debug("Additional properties of RestClient for {}: {}",
                () -> serviceName,
                () -> backLog);
    }

    @Override
    public void setErrorHandler(ResponseErrorHandler handler) {
        if (handler != null) {
            super.getRestTemplate().setErrorHandler(handler);
        }
    }

    /**
     * To create {@link RestClientImpl}.<br>
     * RestTemplate will be initialized with params from properties (or default params in case of empty properties).
     *
     * @since 0.0.1
     */
    public RestClientImpl() {
    }

    private RestClientImpl(String maxConnections,
                           String maxConnectionsPerRoute,
                           String connectTimeout,
                           String connectionRequestTimeout,
                           String timeoutResponse,
                           String secondsToLive,
                           boolean sslDisabled,
                           boolean bufferBody,
                           List<ClientHttpRequestInterceptor> interceptors,
                           List<HttpMessageConverter<?>> messageConverters) {
        super.setRestTemplate(
                createRestTemplate(
                        calcMaxConnections(maxConnections),
                        calcMaxConnectionsPerRoute(maxConnectionsPerRoute),
                        calcConnectionTimeout(connectTimeout),
                        calcConnectionRequestTimeout(connectionRequestTimeout),
                        calcConnectionResponseTimeout(timeoutResponse),
                        calcPoolConnectionsTimeout(secondsToLive),
                        sslDisabled,
                        bufferBody,
                        prepareInterceptors(interceptors),
                        prepareConverters(messageConverters)));
    }

    @PostConstruct
    public void init() {
        //        log.info("Start initializing variables...");

        Integer connectionTimeout = calcConnectionTimeout(appProperties.getProperty(CONNECTION_TIMEOUT_NAME));
        //        log.debug("Http connection timeout was initialized with value '{}'.", connectionTimeout);

        Integer maxConnections = calcMaxConnections(appProperties.getProperty(MAX_CONNECTIONS_NAME));
        //        log.debug("Http maximum connections were initialized with value '{}'.", maxConnections);

        Integer maxConnectionsPerRoute = calcMaxConnectionsPerRoute(
                appProperties.getProperty(MAX_CONNECTIONS_PER_ROUTE_NAME));
        //        log.debug("Http maximum connections per route were initialized with value '{}'.", maxConnectionsPerRoute);

        Integer connectionRequestTimeout = calcConnectionRequestTimeout(
                appProperties.getProperty(CONNECTION_REQUEST_TIMEOUT_NAME));
        //        log.debug("Http connection request timeout was initialized with value '{}'.", connectionRequestTimeout);

        Integer connectionResponseTimeout = calcConnectionResponseTimeout(
                appProperties.getProperty(CONNECTION_RESPONSE_TIMEOUT_NAME));
        //        log.debug("Http connection response timeout was initialized with value '{}'.", connectionResponseTimeout);

        Integer poolConnectionsTimeout = calcPoolConnectionsTimeout(
                appProperties.getProperty(POOL_CONNECTIONS_TIMEOUT_NAME));
        //        log.debug("Http pool connections timeout was initialized with value '{}'.", poolConnectionsTimeout);

        List<ClientHttpRequestInterceptor> interceptors = prepareInterceptors(null);
        //        log.debug("Interceptors are prepared in quantity: {}.", interceptors:: size);

        List<HttpMessageConverter<?>> converters = prepareConverters(null);
        //        log.debug("Message converters are prepared in quantity: {}.", converters:: size);

        super.setRestTemplate(createRestTemplate(maxConnections, maxConnectionsPerRoute, connectionTimeout,
                connectionRequestTimeout, connectionResponseTimeout, poolConnectionsTimeout,
                false, true, interceptors, converters));

        //        log.info("Initializing variables is finished.");
    }

    // </editor-fold>
}
