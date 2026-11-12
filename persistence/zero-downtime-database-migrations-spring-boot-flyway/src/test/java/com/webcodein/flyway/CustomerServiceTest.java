package com.webcodein.flyway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should create customer successfully")
    void shouldCreateCustomer() {
        CustomerRequest req = new CustomerRequest("dev@example.com", "Alan Turing");
        Customer customer = new Customer(req.email(), req.fullName());

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer created = customerService.createCustomer(req);

        assertThat(created.getEmail()).isEqualTo("dev@example.com");
        assertThat(created.getFullName()).isEqualTo("Alan Turing");
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should find customer by ID")
    void shouldFindCustomerById() {
        Customer customer = new Customer("grace@example.com", "Grace Hopper");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Customer found = customerService.getCustomer(1L);

        assertThat(found.getFullName()).isEqualTo("Grace Hopper");
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when customer ID not found")
    void shouldThrowWhenNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomer(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Customer not found with id: 99");
    }

    @Test
    @DisplayName("Should return all customers")
    void shouldReturnAllCustomers() {
        List<Customer> list = List.of(
                new Customer("a@ex.com", "A Name"),
                new Customer("b@ex.com", "B Name")
        );
        when(customerRepository.findAll()).thenReturn(list);

        List<Customer> result = customerService.getAllCustomers();

        assertThat(result).hasSize(2);
        verify(customerRepository, times(1)).findAll();
    }
}
