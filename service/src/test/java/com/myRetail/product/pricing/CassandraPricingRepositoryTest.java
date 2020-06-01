package com.myRetail.product.pricing;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import com.datastax.oss.driver.api.core.connection.ClosedConnectionException;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.Statement;
import com.myRetail.product.ProductServiceProperties;
import com.myRetail.product.cassandra.ReactiveCassandraOperations;
import com.myRetail.product.pricing.entity.PricingProductEntity;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class CassandraPricingRepositoryTest {

    @Mock
    private ReactiveCassandraOperations reactiveCassandraOperations;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private BoundStatement boundStatement;

    private CassandraPricingRepository cassandraPricingRepo;

    private PricingProductEntity mockPricingProduct = new PricingProductEntity("mockId", BigDecimal.valueOf(19.99));

    private PricingProductEntity nullPricingProduct = new PricingProductEntity("mockId", null);

    @BeforeEach
    void setup() {
        ProductServiceProperties properties = new ProductServiceProperties();
        properties.setCassandraReadTimeoutMs(10000);
        when(reactiveCassandraOperations.createPreparedStatement(anyString())).thenReturn(preparedStatement);

        cassandraPricingRepo = new CassandraPricingRepository(reactiveCassandraOperations, properties);
    }

    @Test
    @DisplayName("given valid id, then return entity")
    public void testGetProduct_happyPath() {
        when(reactiveCassandraOperations.getOne(any(Statement.class), any())).thenReturn(Mono.just(mockPricingProduct));
        when(preparedStatement.bind("mockId")).thenReturn(boundStatement);

        Mono<PricingProductEntity> resultMono = cassandraPricingRepo.getProduct("mockId");
        StepVerifier.create(resultMono).expectNext(mockPricingProduct).verifyComplete();
    }

    @Test
    @DisplayName("given valid id, when exception is encountered, propagate exception")
    public void testGetProduct_exceptionPath() {
        when(reactiveCassandraOperations.getOne(any(Statement.class), any()))
                .thenReturn(Mono.error(new ClosedConnectionException("Fail")));
        when(preparedStatement.bind("mockId")).thenReturn(boundStatement);

        Mono<PricingProductEntity> resultMono = cassandraPricingRepo.getProduct("mockId");
        StepVerifier.create(resultMono).expectError(ClosedConnectionException.class).verify();
    }

    @Test
    @DisplayName("given valid id, when response takes too long, then return default product with null price")
    public void testGetProduct_timeoutException() {
        when(reactiveCassandraOperations.getOne(any(Statement.class), any()))
                .thenReturn(Mono.never().timeout(Duration.ofMillis(100)));
        when(preparedStatement.bind("mockId")).thenReturn(boundStatement);

        Mono<PricingProductEntity> resultMono = cassandraPricingRepo.getProduct("mockId");
        StepVerifier.create(resultMono).expectError(TimeoutException.class).verify();
    }

    @Test
    @DisplayName("given valid id, when value doesn't exist, then return default product with null price")
    public void testGetProduct_noRecordFound() {
        when(reactiveCassandraOperations.getOne(any(Statement.class), any())).thenReturn(Mono.empty());
        when(preparedStatement.bind("mockId")).thenReturn(boundStatement);

        Mono<PricingProductEntity> resultMono = cassandraPricingRepo.getProduct("mockId");
        StepVerifier.create(resultMono).expectNext(nullPricingProduct).verifyComplete();
    }

    @Test
    @DisplayName("given valid id and price, then record should be updated return true")
    public void testUpdatePrice_happyPath() {
        when(reactiveCassandraOperations.updateOne(any(Statement.class))).thenReturn(Mono.just(true));
        when(preparedStatement.bind()).thenReturn(boundStatement);
        when(boundStatement.setString(eq("id"), eq("mockId"))).thenReturn(boundStatement);
        when(boundStatement.setBigDecimal(eq("price"), eq(BigDecimal.valueOf(100.00)))).thenReturn(boundStatement);

        Mono<Boolean> resultMono = cassandraPricingRepo.updateProductPrice("mockId", BigDecimal.valueOf(100.00));
        StepVerifier.create(resultMono).expectNext(true).verifyComplete();
    }

    @Test
    @DisplayName("given valid id and price, when no record is found to update, return false")
    public void testUpdatePrice_noRecordToUpdate() {
        when(reactiveCassandraOperations.updateOne(any(Statement.class))).thenReturn(Mono.just(false));
        when(preparedStatement.bind()).thenReturn(boundStatement);
        when(boundStatement.setString(eq("id"), eq("mockId"))).thenReturn(boundStatement);
        when(boundStatement.setBigDecimal(eq("price"), eq(BigDecimal.valueOf(100.00)))).thenReturn(boundStatement);

        Mono<Boolean> resultMono = cassandraPricingRepo.updateProductPrice("mockId", BigDecimal.valueOf(100.00));
        StepVerifier.create(resultMono).expectNext(false).verifyComplete();
    }
}
