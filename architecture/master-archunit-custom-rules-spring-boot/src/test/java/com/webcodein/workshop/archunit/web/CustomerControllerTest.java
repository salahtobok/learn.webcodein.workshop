package com.webcodein.workshop.archunit.web;

import com.webcodein.workshop.archunit.domain.Customer;
import com.webcodein.workshop.archunit.service.CreateCustomerUseCase;
import com.webcodein.workshop.archunit.service.GetCustomerUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateCustomerUseCase createCustomerUseCase;

    @MockitoBean
    private GetCustomerUseCase getCustomerUseCase;

    @Test
    void shouldCreateCustomer() throws Exception {
        when(createCustomerUseCase.create("Delta Force")).thenReturn(77L);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Delta Force\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/customers/77"));

        verify(createCustomerUseCase).create("Delta Force");
    }

    @Test
    void shouldGetCustomerById() throws Exception {
        Customer customer = new Customer("Delta Force");
        ReflectionTestUtils.setField(customer, "id", 77L);

        when(getCustomerUseCase.getById(77L)).thenReturn(Optional.of(customer));

        mockMvc.perform(get("/api/customers/77"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(77))
                .andExpect(jsonPath("$.name").value("Delta Force"))
                .andExpect(jsonPath("$.createdAt").exists());

        verify(getCustomerUseCase).getById(77L);
    }

    @Test
    void shouldReturn404WhenNotFound() throws Exception {
        when(getCustomerUseCase.getById(88L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/customers/88"))
                .andExpect(status().isNotFound());

        verify(getCustomerUseCase).getById(88L);
    }
}
