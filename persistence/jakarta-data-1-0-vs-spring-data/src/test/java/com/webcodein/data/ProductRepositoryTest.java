package com.webcodein.data;

import jakarta.data.Order;
import jakarta.data.Sort;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProductRepositoryTest {

    static SessionFactory sessionFactory;
    static StatelessSession session;
    static ProductRepository repository;

    @BeforeAll
    static void setUp() {
        sessionFactory = new Configuration()
                .addAnnotatedClass(Employee.class)
                .addAnnotatedClass(Product.class)
                .setProperty("hibernate.connection.url", "jdbc:h2:mem:product_repo_test;DB_CLOSE_DELAY=-1")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .buildSessionFactory();
        session = sessionFactory.openStatelessSession();
        repository = new ProductRepository_(session);
    }

    @AfterAll
    static void tearDown() {
        if (session != null) {
            session.close();
        }
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    @DisplayName("Pagination and sorting via PageRequest and Order contract")
    void testPaginationWithOrder() {
        repository.insert(new Product(201L, "Laptop Pro", "Hardware", 1999.99));
        repository.insert(new Product(202L, "Mechanical Keyboard", "Hardware", 149.50));
        repository.insert(new Product(203L, "Ergonomic Mouse", "Hardware", 79.99));
        repository.insert(new Product(204L, "4K Monitor", "Hardware", 499.00));

        Page<Product> page = repository.byCategory(
                "Hardware",
                PageRequest.ofPage(1).size(2),
                Order.by(Sort.asc("price"))
        );

        assertThat(page.content()).hasSize(2);
        assertThat(page.content()).extracting(Product::getName).containsExactly("Ergonomic Mouse", "Mechanical Keyboard");
        assertThat(page.pageRequest().page()).isEqualTo(1);
        assertThat(page.totalElements()).isEqualTo(4);
    }

    @Test
    @DisplayName("Declarative compile-time sorting via @OrderBy annotation")
    void testStaticOrderByAnnotation() {
        repository.insert(new Product(205L, "Cloud IDE License", "Software", 299.00));
        repository.insert(new Product(206L, "Profiling Toolkit", "Software", 99.00));
        repository.insert(new Product(207L, "CI Enterprise Agent", "Software", 499.00));

        List<Product> sortedProducts = repository.findByCategoryOrderByPriceAsc("Software");
        assertThat(sortedProducts).extracting(Product::getName)
                .containsExactly("Profiling Toolkit", "Cloud IDE License", "CI Enterprise Agent");
    }

    @Test
    @DisplayName("Count by category custom query and CRUD lifecycle operations")
    void testCountByCategoryAndCrudLifecycle() {
        Product book1 = new Product(208L, "Modern Java 25", "Books", 59.99);
        Product book2 = new Product(209L, "Jakarta EE 11 in Action", "Books", 64.99);
        repository.insert(book1);
        repository.insert(book2);

        long count = repository.countByCategory("Books");
        assertThat(count).isEqualTo(2);

        // Update product price
        book1.setPrice(49.99);
        repository.update(book1);

        var updated = repository.findById(208L);
        assertThat(updated).isPresent();
        assertThat(updated.get().getPrice()).isEqualTo(49.99);

        // Delete product
        repository.delete(book2);
        assertThat(repository.countByCategory("Books")).isEqualTo(1);
    }
}
