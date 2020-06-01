package com.myRetail.product.web;

import com.myRetail.product.ProductServiceProperties;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

@Component
public class WebClientConfigurer implements WebClientCustomizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebClientConfigurer.class);

    private final int apiReadTimeoutMs;

    private final int apiConnectTimeoutMs;

    public WebClientConfigurer(ProductServiceProperties properties) {
        this.apiReadTimeoutMs = properties.getApiReadTimeoutMs();
        this.apiConnectTimeoutMs = properties.getApiConnectTimeoutMs();
    }

    public void customize(WebClient.Builder webClientBuilder) {
        webClientBuilder.clientConnector(
                new ReactorClientHttpConnector(HttpClient.from(getTcpClient(apiConnectTimeoutMs, apiReadTimeoutMs))));
        webClientBuilder.filter(logResponse());
        webClientBuilder.filter(logRequest());
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            LOGGER.debug("Response: {}", clientResponse.statusCode());
            LOGGER.debug("--- Http Headers of Response: ---");
            clientResponse.headers()
                    .asHttpHeaders()
                    .forEach((name, values) -> values.forEach(value -> LOGGER.debug("{}={}", name, value)));
            return Mono.just(clientResponse);
        });
    }

    private ExchangeFilterFunction logRequest() {
        return (clientRequest, next) -> {
            LOGGER.debug("Request: {} {}", clientRequest.method(), clientRequest.url());
            LOGGER.debug("--- Http Headers of Request: ---");
            clientRequest.headers().forEach(this::logHeader);
            return next.exchange(clientRequest);
        };
    }

    private void logHeader(String name, List<String> values) {
        values.forEach(value -> LOGGER.debug("{}={}", name, value));
    }

    private TcpClient getTcpClient(int connectTimeoutInMillis, int readTimeoutMillis) {
        return TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutInMillis)
                .doOnConnected(conn -> {
                    conn.addHandlerLast(new ReadTimeoutHandler(readTimeoutMillis, TimeUnit.MILLISECONDS));
                });
    }
}
