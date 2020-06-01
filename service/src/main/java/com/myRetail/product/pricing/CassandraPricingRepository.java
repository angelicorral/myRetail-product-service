package com.myRetail.product.pricing;

import com.datastax.dse.driver.api.core.cql.reactive.ReactiveRow;
import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.myRetail.product.ProductServiceProperties;
import com.myRetail.product.cassandra.ReactiveCassandraOperations;
import com.myRetail.product.pricing.entity.PricingProductEntity;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * 
 * Pricing Repository hosted in Cassandra
 * Read and Write operations are by default set to LOCAL_QUORUM
 * Cassandra Read timeout is configurable in properties
 *
 */
@Component
public class CassandraPricingRepository implements PricingRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraPricingRepository.class);

    private final ReactiveCassandraOperations reactiveCassandraOps;

    private final PreparedStatement getProductStatement;

    private final PreparedStatement updateProductPriceStatement;

    private final int cassandraReadTimeoutMs;

    protected static final Function<String, PricingProductEntity> DEFAULT_PRODUCT_CREATOR = (
            id) -> new PricingProductEntity(id, null);

    public CassandraPricingRepository(ReactiveCassandraOperations reactiveCassandraOps,
            ProductServiceProperties properties) {
        this.cassandraReadTimeoutMs = properties.getCassandraReadTimeoutMs();
        this.reactiveCassandraOps = reactiveCassandraOps;
        this.getProductStatement = reactiveCassandraOps
                .createPreparedStatement("SELECT id, price FROM myRetail.product where id=?");
        this.updateProductPriceStatement = reactiveCassandraOps
                .createPreparedStatement("UPDATE myRetail.product set price=? where id=? IF EXISTS");
    }

    private Function<ReactiveRow, PricingProductEntity> rowMapper = (row) -> {
        BigDecimal price = row.getBigDecimal("price");
        if (price == null) {
            price = BigDecimal.ZERO;
        }
        return new PricingProductEntity(row.getString("id"), price);
    };

    @Override
    public Mono<PricingProductEntity> getProduct(String id) {

        BoundStatement boundStatement = getProductStatement.bind(id);
        boundStatement.setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);
        return reactiveCassandraOps.getOne(boundStatement, rowMapper)
                .timeout(Duration.of(cassandraReadTimeoutMs, ChronoUnit.MILLIS))
                .switchIfEmpty(Mono.just(DEFAULT_PRODUCT_CREATOR.apply(id)))
                .onErrorResume(e -> {
                    LOGGER.error("Exception encountered calling cassandra for id {}", id, e);
                    return Mono.error(e);
                });
    }

    @Override
    public Mono<Boolean> updateProductPrice(String id, BigDecimal price) {
        BoundStatement boundStatement = updateProductPriceStatement.bind()
                .setString("id", id)
                .setBigDecimal("price", price);
        boundStatement.setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);
        return reactiveCassandraOps.updateOne(boundStatement).onErrorResume(e -> {
            LOGGER.error("Exception encountered updating cassandra for id {}", id, e);
            return Mono.error(e);
        });
    }

}
