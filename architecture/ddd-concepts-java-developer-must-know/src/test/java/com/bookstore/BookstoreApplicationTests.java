package com.bookstore;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BookstoreApplicationTests {

    @Test
    @DisplayName("Spring ApplicationContext loads successfully with H2 in-memory profile")
    void contextLoads() {
    }
}
