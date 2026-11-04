package com.marketplace.common.error;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DomainExceptionsTest {


    @Test
    void resourceNotFoundCarriesResourceAndId() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Product", 123L);
        assertThat(ex).hasMessage("Product with id 123 not found");
        assertThat(ex.getResource()).isEqualTo("Product");
        assertThat(ex.getId()).isEqualTo(123L);
    }

    @Test
    void insufficientStockCarriesQuantities() {
        InsufficientStockException ex = new InsufficientStockException(7L, 5, 3);
        assertThat(ex).hasMessageContaining("requested 5, available 3");
        assertThat(ex).hasMessageContaining("product 7");
        assertThat(ex.getProductId()).isEqualTo(7L);
        assertThat(ex.getRequested()).isEqualTo(5);
        assertThat(ex.getAvailable()).isEqualTo(3);
    }

    enum OrderStatus {CREATED, PAID, SHIPPED}

    @Test
    void illegalTransitionCarriesStates() {
        IllegalStateTransitionException ex =
                new IllegalStateTransitionException(OrderStatus.CREATED, OrderStatus.SHIPPED);
        assertThat(ex).hasMessage("Illegal state transition from CREATED to SHIPPED");
        assertThat(ex.getFrom()).isEqualTo(OrderStatus.CREATED);
        assertThat(ex.getTo()).isEqualTo(OrderStatus.SHIPPED);
    }
}