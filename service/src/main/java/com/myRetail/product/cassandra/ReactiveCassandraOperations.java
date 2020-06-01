package com.myRetail.product.cassandra;

import com.datastax.dse.driver.api.core.cql.reactive.ReactiveRow;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.Statement;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Abstraction over Cassandra CqlSession
 *
 */
@Component
public class ReactiveCassandraOperations {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveCassandraOperations.class);

    private final CqlSession cqlSession;

    public ReactiveCassandraOperations(CqlSession cqlSession) {
        this.cqlSession = cqlSession;
    }

    public <T> Flux<T> getMany(Statement<?> query, Function<ReactiveRow, T> mapper) {
        return Flux.defer(() -> cqlSession.executeReactive(query)).map(mapper);
    }

    public <T> Mono<T> getOne(Statement<?> query, Function<ReactiveRow, T> mapper) {
        return Flux.defer(() -> cqlSession.executeReactive(query)).next().map(mapper);
    }

    public Mono<Boolean> updateOne(Statement<?> update) {
        return Flux.defer(() -> cqlSession.executeReactive(update)).flatMap((row) -> {
            LOGGER.debug("Update applied: {}", row.wasApplied());
            if (row.wasApplied()) {
                return Mono.just(true);
            }
            return Mono.just(false);
        }).next().onErrorResume(e -> {
            LOGGER.error("Exception running update statement {}", update.getClass(), e);
            return Mono.just(false);
        });
    }

    public PreparedStatement createPreparedStatement(String query) {
        return cqlSession.prepare(query);
    }

}
