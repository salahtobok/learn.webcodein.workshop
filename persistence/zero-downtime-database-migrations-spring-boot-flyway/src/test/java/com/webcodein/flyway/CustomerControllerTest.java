package com.webcodein.flyway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @Test
    @DisplayName("POST /api/customers should return 201 Created")
    void shouldCreateCustomer() throws Exception {
        Customer customer = new Customer("ada@example.com", "Ada Lovelace");
        when(customerService.createCustomer(any(CustomerRequest.class))).thenReturn(customer);

        String json = """
                {
                    "email": "ada@example.com",
                    "fullName": "Ada Lovelace"
                }
                """;

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ada@example.com"))
                .andExpect(jsonPath("$.fullName").value("Ada Lovelace"));
    }

    @Test
    @DisplayName("GET /api/customers/{id} should return 200 OK")
    void shouldGetCustomerById() throws Exception {
        Customer customer = new Customer("ada@example.com", "Ada Lovelace");
        when(customerService.getCustomer(1L)).thenReturn(customer);

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ada@example.com"))
                .andExpect(jsonPath("$.fullName").value("Ada Lovelace"));
    }

    @Test
    @DisplayName("GET /api/customers/{id} should return 404 when not found")
    void shouldReturn404WhenNotFound() throws Exception {
        when(customerService.getCustomer(999L)).thenThrow(new NoSuchElementException("Not found"));

        mockMvc.perform(get("/api/customers/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/customers should return customer list")
    void shouldGetAllCustomers() throws Exception {
        List<Customer> list = List.of(
                new Customer("c1@example.com", "Customer One"),
                new Customer("c2@example.com", "Customer Two")
        );
        when(customerService.getAllCustomers()).thenReturn(list);

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
