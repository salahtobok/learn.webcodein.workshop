package com.webcodein.hikaricp.repository;

import com.webcodein.hikaricp.entity.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long> {

    List<ProductOrder> findByStatus(String status);

    @Query(value = "SELECT pg_sleep(:seconds)", nativeQuery = true)
    void executeSimulatedDatabaseDelay(@Param("seconds") double seconds);
}
