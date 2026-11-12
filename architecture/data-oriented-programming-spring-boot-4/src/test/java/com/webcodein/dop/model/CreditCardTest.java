package com.webcodein.dop.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreditCardTest {

    @Test
    void shouldCreateValidCreditCardRecord() {
        CreditCard card = new CreditCard("4111111111111234", "12/28");

        assertThat(card.cardNumber()).isEqualTo("4111111111111234");
        assertThat(card.expiryDate()).isEqualTo("12/28");
    }

    @Test
    void shouldRejectBlankCardNumber() {
        assertThatThrownBy(() -> new CreditCard("", "12/28"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Card number must not be blank");
    }

    @Test
    void shouldRejectNullExpiryDate() {
        assertThatThrownBy(() -> new CreditCard("4111111111111234", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Expiry date must not be blank");
    }
}
