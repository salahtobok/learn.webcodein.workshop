package com.webcodein.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AdminResourceTest {

    @Test
    @DisplayName("AdminResource getAdminData returns enterprise secret payload")
    void testGetAdminData() {
        AdminResource resource = new AdminResource();
        String result = resource.getAdminData();
        assertThat(result).isEqualTo("Top Secret Enterprise Data");
    }
}
