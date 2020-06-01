package com.myRetail.product.security;

import java.security.GeneralSecurityException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuration that establishes security settings
 */
@Configuration
@EnableWebFluxSecurity
public class ServiceAuthenticationConfig {

    AuthProperties properties;

    public ServiceAuthenticationConfig(AuthProperties properties) {
        this.properties = properties;
    }

    @Bean
    public MapReactiveUserDetailsService readerDetailsService() throws GeneralSecurityException {
        UserDetails reader = User.builder()
                .username(properties.getReaderUsername())
                .password(passwordEncoder().encode(properties.getReaderPassword()))
                .roles("READER")
                .build();

        UserDetails admin = User.builder()
                .username(properties.getAdminUsername())
                .password(passwordEncoder().encode(properties.getAdminPassword()))
                .roles("ADMIN")
                .build();

        return new MapReactiveUserDetailsService(reader, admin);
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.csrf()
                .disable()
                .authorizeExchange()
                .pathMatchers("/info", "/health-check")
                .permitAll()
                .pathMatchers(HttpMethod.PUT)
                .hasRole("ADMIN")
                .anyExchange()
                .authenticated()
                .and()
                .formLogin()
                .and()
                .httpBasic();
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
