package com.webcodein.workshop.archunit.repository;

import com.webcodein.workshop.archunit.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Rule to enforce: Repositories should only be accessed by services/use cases
// Rule to enforce: Repository interfaces should end with 'Repository' and be package-private when possible
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
