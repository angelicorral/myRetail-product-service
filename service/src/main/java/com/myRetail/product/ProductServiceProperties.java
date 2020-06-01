package com.myRetail.product;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "myretail.product")
public class ProductServiceProperties {

    private int cassandraReadTimeoutMs;

    private int apiConnectTimeoutMs;

    private int apiReadTimeoutMs;

    public int getCassandraReadTimeoutMs() {
        return cassandraReadTimeoutMs;
    }

    public void setCassandraReadTimeoutMs(int cassandraReadTimeoutMs) {
        this.cassandraReadTimeoutMs = cassandraReadTimeoutMs;
    }

    public int getApiConnectTimeoutMs() {
        return apiConnectTimeoutMs;
    }

    public void setApiConnectTimeoutMs(int apiConnectTimeoutMs) {
        this.apiConnectTimeoutMs = apiConnectTimeoutMs;
    }

    public int getApiReadTimeoutMs() {
        return apiReadTimeoutMs;
    }

    public void setApiReadTimeoutMs(int apiReadTimeoutMs) {
        this.apiReadTimeoutMs = apiReadTimeoutMs;
    }

}
