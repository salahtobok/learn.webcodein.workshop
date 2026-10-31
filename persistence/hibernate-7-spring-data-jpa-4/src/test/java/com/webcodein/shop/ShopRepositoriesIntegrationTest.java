package com.webcodein.shop;

import static org.assertj.core.api.Assertions.assertThat;

import com.webcodein.shop.employee.Employee;
import com.webcodein.shop.employee.EmployeeRepository;
import com.webcodein.shop.order.Discount;
import com.webcodein.shop.order.LineItem;
import com.webcodein.shop.order.Order;
import com.webcodein.shop.order.OrderRepository;
import com.webcodein.shop.order.OrderStatus;
import com.webcodein.shop.product.Product;
import com.webcodein.shop.product.ProductMetadata;
import com.webcodein.shop.product.ProductRepository;
import com.webcodein.shop.user.AppUser;
import com.webcodein.shop.user.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest
@Testcontainers
class ShopRepositoriesIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired ProductRepository products;
    @Autowired EmployeeRepository employees;
    @Autowired OrderRepository orders;
    @Autowired UserRepository users;

    @Test
    void queriesInsideJsonColumn() {
        products.save(new Product(1L, "Laptop", new ProductMetadata("Acme", 2, List.of("pc"))));

        assertThat(products.findByManufacturer("Acme")).hasSize(1);
        assertThat(products.findByManufacturer("UnknownBrand")).isEmpty();
    }

    @Test
    void ranksEmployeesWithWindowFunction() {
        employees.save(new Employee(1L, "Ada", "R&D", new BigDecimal("200")));
        employees.save(new Employee(2L, "Grace", "R&D", new BigDecimal("300")));
        employees.save(new Employee(3L, "Bob", "Sales", new BigDecimal("400")));

        List<Object[]> ranks = employees.findEmployeesWithDepartmentRank();
        assertThat(ranks).hasSize(3);
    }

    @Test
    void fetchesTwoListsWithEntityGraph() {
        Order order = new Order(OrderStatus.PAID);
        order.getLineItems().add(new LineItem("sku-1"));
        order.getDiscounts().add(new Discount("SAVE10"));
        orders.save(order);

        assertThat(orders.findByStatus(OrderStatus.PAID)).isNotEmpty();
    }

    @Test
    void projectsToRecord() {
        users.save(new AppUser(1L, "ada", "ada@example.com", true));

        assertThat(users.findByIsActiveTrue()).extracting("username").contains("ada");
    }
}
