package com.webcodein.workshop.archunit.service;

import com.webcodein.workshop.archunit.domain.Customer;
import com.webcodein.workshop.archunit.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCustomerUseCaseTest {

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private GetCustomerUseCase useCase;

    @Test
    void shouldReturnCustomerWhenFound() {
        Customer customer = new Customer("Charlie Brown");
        ReflectionTestUtils.setField(customer, "id", 101L);

        when(repository.findById(101L)).thenReturn(Optional.of(customer));

        Optional<Customer> result = useCase.getById(101L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Charlie Brown");
        assertThat(result.get().getId()).isEqualTo(101L);
        verify(repository).findById(101L);
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        Optional<Customer> result = useCase.getById(999L);

        assertThat(result).isEmpty();
        verify(repository).findById(999L);
    }
}
