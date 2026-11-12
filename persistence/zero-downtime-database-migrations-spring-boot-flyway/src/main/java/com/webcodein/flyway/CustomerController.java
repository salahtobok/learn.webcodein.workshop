package com.webcodein.flyway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CustomerRequest request) {
        Customer customer = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerResponse.from(customer));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable Long id) {
        try {
            Customer customer = customerService.getCustomer(id);
            return ResponseEntity.ok(CustomerResponse.from(customer));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        List<CustomerResponse> list = customerService.getAllCustomers().stream()
                .map(CustomerResponse::from)
                .toList();
        return ResponseEntity.ok(list);
    }
}
