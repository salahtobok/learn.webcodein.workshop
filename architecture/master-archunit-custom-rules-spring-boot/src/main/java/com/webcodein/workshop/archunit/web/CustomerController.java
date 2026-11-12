package com.webcodein.workshop.archunit.web;

import com.webcodein.workshop.archunit.service.CreateCustomerUseCase;
import com.webcodein.workshop.archunit.service.GetCustomerUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;

    public CustomerController(CreateCustomerUseCase createCustomerUseCase, GetCustomerUseCase getCustomerUseCase) {
        this.createCustomerUseCase = createCustomerUseCase;
        this.getCustomerUseCase = getCustomerUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody CustomerCreationRequest request) {
        Long id = createCustomerUseCase.create(request.name());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getById(@PathVariable Long id) {
        return getCustomerUseCase.getById(id)
                .map(customer -> ResponseEntity.ok(new CustomerResponse(
                        customer.getId(),
                        customer.getName(),
                        customer.getCreatedAt())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
