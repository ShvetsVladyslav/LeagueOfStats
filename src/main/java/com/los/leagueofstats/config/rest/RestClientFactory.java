package com.los.leagueofstats.config.rest;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
class RestClientFactory {

    /**
     * Create {@link RestTemplate} with specified parameters.
     *
     * @param maxConnections           maximum quantity of opened connections in the pull of the {@link RestTemplate}.
     * @param maxConnectionsPerRoute   maximum quantity of opened connections in the pull per route (url).
     * @param connectTimeout           establishing new connection timeout.
     * @param connectionRequestTimeout waiting for the getting of a new pull connection timeout. If all connections in the
     *                                 pull of the {@link RestTemplate} are busy (are using in other flows), all subsequent
     *                                 requests will accumulate and wait time = connectionRequestTimeout, until the pull
     *                                 become free and request will proceed or will be rejected.
     * @param timeoutResponse          waiting for the remote server answer timeout after request been sent.
     * @param secondsToLive            waiting timeout in seconds for closing not active connections in a pool.
     *
     * @return new {@link RestTemplate} with tuned settings. In debug logging level all request and response parameters
     * are logged include bodies
     *
     * @since 0.0.1
     * @deprecated it is recommended to use {@link RestClientFactory#newRestTemplate(int, int, int, int, int, int, boolean, boolean, List, List)} instead.
     */
    @Deprecated
    static RestTemplate newRestTemplate(int maxConnections,
                                        int maxConnectionsPerRoute,
                                        int connectTimeout,
                                        int connectionRequestTimeout,
                                        int timeoutResponse,
                                        int secondsToLive) {
        return newRestTemplate(maxConnections, maxConnectionsPerRoute, connectTimeout, connectionRequestTimeout,
                timeoutResponse, secondsToLive, false, true, null, null);
    }

    /**
     * Create {@link RestTemplate} with specified parameters.
     *
     * @param maxConnections           maximum quantity of opened connections in the pull of the {@link RestTemplate}.
     * @param maxConnectionsPerRoute   maximum quantity of opened connections in the pull per route (url).
     * @param connectTimeout           establishing new connection timeout.
     * @param connectionRequestTimeout waiting for the getting of a new pull connection timeout. If all connections in the
     *                                 pull of the {@link RestTemplate} are busy (are using in other flows), all subsequent
     *                                 requests will accumulate and wait time = connectionRequestTimeout, until the pull
     *                                 become free and request will proceed or will be rejected.
     * @param timeoutResponse          waiting for the remote server answer timeout after request been sent.
     * @param secondsToLive            waiting timeout in seconds for closing not active connections in a pool.
     * @param sslDisabled              flag of SSL disabled.
     * @param bufferBody               true - to buffer body for POST/PUT, false - not.
     * @param interceptors             the list of interceptors for rest client (mar be null).
     * @param messageConverters        the list of message converters (may be null).
     *
     * @return new {@link RestTemplate} with tuned settings. In debug logging level all request and response parameters
     * are logged include bodies
     *
     * @since 0.0.1
     */
    static RestTemplate newRestTemplate(int maxConnections,
                                        int maxConnectionsPerRoute,
                                        int connectTimeout,
                                        int connectionRequestTimeout,
                                        int timeoutResponse,
                                        int secondsToLive,
                                        boolean sslDisabled,
                                        boolean bufferBody,
                                        List<ClientHttpRequestInterceptor> interceptors,
                                        List<HttpMessageConverter<?>> messageConverters) {

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(secondsToLive,
                TimeUnit.SECONDS);
        cm.setMaxTotal(maxConnections);
        cm.setDefaultMaxPerRoute(maxConnectionsPerRoute);
        //именно на этот таймаут смотрит SSLSocketFactory при открытии SSLSocket'a
        cm.setDefaultSocketConfig(
                SocketConfig.copy(SocketConfig.DEFAULT)
                        .setSoTimeout(timeoutResponse)
                        .build()
        );

        HttpClientBuilder builder = HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(
                        RequestConfig.copy(RequestConfig.DEFAULT)
                                .setSocketTimeout(timeoutResponse)
                                .setConnectionRequestTimeout(connectionRequestTimeout)
                                .setConnectTimeout(connectTimeout)
                                .build()
                );

        RestTemplate rt = new RestTemplate(prepareClientHttpRequestFactory(builder, sslDisabled, bufferBody));

        if (!isEmpty(interceptors)) {
            List<ClientHttpRequestInterceptor> rtInterceptors = rt.getInterceptors();
            rtInterceptors.addAll(interceptors);

            rt.setInterceptors(rtInterceptors);
        }

        if (!isEmpty(messageConverters)) {
            List<HttpMessageConverter<?>> rtMessageConverters = rt.getMessageConverters();
            //Наши настроенные конвертеры ставим в самое начало
            rtMessageConverters.addAll(0, messageConverters);

            rt.setMessageConverters(rtMessageConverters);
        }

        return rt;
    }

    private static ClientHttpRequestFactory prepareClientHttpRequestFactory(HttpClientBuilder builder,
                                                                            boolean sslDisabled,
                                                                            boolean bufferBody) {
        if (sslDisabled) {
            log.warn("SSL is disabled");
            try {
                SSLContext sslContext = new SSLContextBuilder()
                        .loadTrustMaterial((chain, authType) -> true)
                        .build();
                builder.setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE));
            } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException ex) {
                throw new IllegalStateException("Configuration ssl factory error", ex);
            }
        }

        CloseableHttpClient httpclient = builder
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();

        if (bufferBody) {
            return new HttpComponentsClientHttpRequestFactory();
        }

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setBufferRequestBody(false);

        return factory;
    }
}
