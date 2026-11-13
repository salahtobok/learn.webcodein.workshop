package com.webcodein.workshop.httpclient.engine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;

import static org.assertj.core.api.Assertions.assertThat;

class ResilientHttpClientFactoryTest {

    @Test
    @DisplayName("Should create HttpClient configured for HTTP/2 with virtual thread executor")
    void shouldCreateConfiguredHttpClient() {
        HttpClient client = ResilientHttpClientFactory.createVirtualThreadHttpClient();
        assertThat(client).isNotNull();
        assertThat(client.version()).isEqualTo(HttpClient.Version.HTTP_2);
        assertThat(client.connectTimeout()).isPresent();
        assertThat(client.executor()).isPresent();
    }
}
