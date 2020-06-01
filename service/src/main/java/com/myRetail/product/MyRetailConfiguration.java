package com.myRetail.product;

import com.datastax.oss.driver.api.core.CqlSession;
import com.myRetail.product.security.AuthProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ ProductServiceProperties.class, AuthProperties.class })
public class MyRetailConfiguration {

    @Bean
    CqlSession cqlSession() {
        return CqlSession.builder().build();
    }
}
