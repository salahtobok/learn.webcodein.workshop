package com.webcodein.workshop.chaos;

import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.ToxiproxyClient;
import eu.rekawek.toxiproxy.model.ToxicDirection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.ToxiproxyContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class ChaosEngineeringIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(ChaosEngineeringIntegrationTest.class);
    private static final Network network = Network.newNetwork();

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withNetwork(network)
            .withNetworkAliases("postgres");

    @Container
    public static ToxiproxyContainer toxiproxy = new ToxiproxyContainer("ghcr.io/shopify/toxiproxy:2.9.0")
            .withNetwork(network);

    private static Proxy proxy;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) throws Exception {
        ToxiproxyClient client = new ToxiproxyClient(toxiproxy.getHost(), toxiproxy.getControlPort());
        proxy = client.createProxy("postgresql", "0.0.0.0:8666", "postgres:5432");

        String proxyUrl = "jdbc:postgresql://" + toxiproxy.getHost() + ":" + toxiproxy.getMappedPort(8666) + "/" + postgres.getDatabaseName() + "?socketTimeout=2";
        
        registry.add("spring.datasource.url", () -> proxyUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldSuccessfullyPlaceOrderWhenDatabaseIsHealthy() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .param("product", "MacBook Pro")
                        .param("quantity", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldRecoverAndFailWhenDatabaseLatencyIsTooHigh() throws Exception {
        // Inject latency of 3000ms (timeout usually occurs earlier in Hikari or JDBC depending on config,
        // but to ensure the Spring Retry is exhausted, we can just cut the connection entirely or set huge latency)
        
        // Simulating an outage by closing connection
        proxy.toxics().bandwidth("cut-connection", ToxicDirection.DOWNSTREAM, 0);

        try {
            mockMvc.perform(post("/api/orders")
                            .param("product", "iPhone 15")
                            .param("quantity", "2"))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(jsonPath("$.status").value("FAILED_NETWORK_ISSUE"));
        } finally {
            // Remove toxic so other tests can pass if added
            proxy.toxics().get("cut-connection").remove();
        }
    }
}
