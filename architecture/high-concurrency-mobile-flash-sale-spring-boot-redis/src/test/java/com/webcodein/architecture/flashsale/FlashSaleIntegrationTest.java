package com.webcodein.architecture.flashsale;

import com.webcodein.architecture.flashsale.domain.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import java.time.Duration;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestFlashSaleApplication.class)
class FlashSaleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        redisTemplate.opsForValue().set("inventory:IPHONE15", "10");
    }

    @Test
    void shouldPreventOversellingUnderHighConcurrency() throws InterruptedException {
        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numberOfThreads);
        
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            final String userId = "user" + i;
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/flash-sale/IPHONE15/purchase")
                            .param("userId", userId))
                            .andReturn();
                            
                    if (result.getResponse().getStatus() == 200) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();

        assertThat(successCount.get()).isEqualTo(10);
        
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> 
            assertThat(orderRepository.count()).isEqualTo(10)
        );
        
        assertThat(redisTemplate.opsForValue().get("inventory:IPHONE15")).isEqualTo("0");
    }
}

