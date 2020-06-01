package com.myRetail.product.security;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * Annotation used to bootstrap bby-recs-configuration-service-core application container.
 */
@Target(value = TYPE)
@Retention(value = RUNTIME)
@Documented
@Import(value = ServiceAuthenticationConfig.class)
public @interface EnableServiceAuthentication {
    // empty by design

}
