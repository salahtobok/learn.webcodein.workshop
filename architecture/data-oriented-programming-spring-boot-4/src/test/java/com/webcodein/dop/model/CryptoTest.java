package com.webcodein.dop.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CryptoTest {

    @Test
    void shouldCreateValidCryptoRecord() {
        Crypto crypto = new Crypto("1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa", "BTC");

        assertThat(crypto.walletAddress()).isEqualTo("1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa");
        assertThat(crypto.currency()).isEqualTo("BTC");
    }

    @Test
    void shouldRejectBlankWalletAddress() {
        assertThatThrownBy(() -> new Crypto("", "BTC"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Wallet address must not be blank");
    }

    @Test
    void shouldRejectBlankCurrency() {
        assertThatThrownBy(() -> new Crypto("0x71C...897", "  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Currency must not be blank");
    }
}
