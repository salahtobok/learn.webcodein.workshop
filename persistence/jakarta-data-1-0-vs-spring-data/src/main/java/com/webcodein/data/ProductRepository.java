package com.webcodein.data;

import jakarta.data.Order;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.By;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Find;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

    // Paginated results; the sort order is a separate parameter in Jakarta Data 1.0
    @Find
    Page<Product> byCategory(@By("category") String category, PageRequest pageRequest, Order<Product> order);

    // Filter by category with static compile-time sorting annotation
    @Find
    @OrderBy("price")
    List<Product> findByCategoryOrderByPriceAsc(@By("category") String category);

    // Custom query for counting products in category
    @Query("SELECT count(p) FROM Product p WHERE p.category = ?1")
    long countByCategory(String category);
}
