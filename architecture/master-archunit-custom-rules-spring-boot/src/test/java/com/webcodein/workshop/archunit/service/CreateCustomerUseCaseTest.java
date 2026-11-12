package com.webcodein.workshop.archunit.service;

import com.webcodein.workshop.archunit.domain.Customer;
import com.webcodein.workshop.archunit.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCustomerUseCaseTest {

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CreateCustomerUseCase useCase;

    @Test
    void shouldSaveCustomerAndReturnId() {
        Customer savedCustomer = new Customer("Bob Johnson");
        ReflectionTestUtils.setField(savedCustomer, "id", 42L);

        when(repository.save(any(Customer.class))).thenReturn(savedCustomer);

        Long id = useCase.create("Bob Johnson");

        assertThat(id).isEqualTo(42L);
        verify(repository).save(any(Customer.class));
    }
}
