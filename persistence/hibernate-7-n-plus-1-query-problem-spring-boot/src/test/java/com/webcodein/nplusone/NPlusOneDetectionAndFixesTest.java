package com.webcodein.nplusone;

import com.webcodein.nplusone.domain.Author;
import com.webcodein.nplusone.domain.Book;
import com.webcodein.nplusone.domain.Publisher;
import com.webcodein.nplusone.dto.AuthorSummaryDto;
import com.webcodein.nplusone.inspector.SqlStatementCountInspector;
import com.webcodein.nplusone.repository.AuthorRepository;
import com.webcodein.nplusone.repository.PublisherRepository;
import com.webcodein.nplusone.service.AuthorBookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class NPlusOneDetectionAndFixesTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private com.webcodein.nplusone.repository.BookRepository bookRepository;

    @Autowired
    private AuthorBookService authorBookService;

    @BeforeEach
    void setupTestData() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        publisherRepository.deleteAll();

        // Seed 5 Authors, each with 3 Books (total 15 books)
        for (int i = 1; i <= 5; i++) {
            Author author = new Author("Author " + i, "author" + i + "@example.com");
            for (int j = 1; j <= 3; j++) {
                Book book = new Book("Author " + i + " - Book " + j, "ISBN-" + i + "-" + j, 29.99 * j);
                author.addBook(book);
            }
            authorRepository.save(author);
        }

        // Seed 3 Publishers, each with 3 Books (total 9 books)
        for (int i = 1; i <= 3; i++) {
            Publisher publisher = new Publisher("Publisher " + i);
            for (int j = 1; j <= 3; j++) {
                Book pubBook = new Book("Publisher " + i + " - Book " + j, "PUB-ISBN-" + i + "-" + j, 39.99 * j);
                publisher.addBook(pubBook);
            }
            publisherRepository.save(publisher);
        }

        SqlStatementCountInspector.reset();
    }

    @Test
    @DisplayName("Scenario 1: Reproduce N+1 query problem with unoptimized lazy iteration (1 + 5 = 6 queries)")
    void reproduceNPlusOneQueryProblem() {
        SqlStatementCountInspector.reset();

        List<String> results = authorBookService.getAuthorsWithUnoptimizedLazyLoading();

        assertThat(results).hasSize(5);

        // 1 query to fetch authors + 5 separate queries to fetch books per author
        int executedQueries = SqlStatementCountInspector.getCount();
        assertThat(executedQueries)
                .as("Unoptimized lazy loading must trigger 1 initial query + 5 secondary queries (N+1)")
                .isEqualTo(6);
    }

    @Test
    @DisplayName("Scenario 2: Fix N+1 with JPQL JOIN FETCH (exactly 1 query)")
    void fixWithJoinFetch() {
        SqlStatementCountInspector.reset();

        List<String> results = authorBookService.getAuthorsWithJoinFetch();

        assertThat(results).hasSize(5);

        // Exactly 1 SQL query with JOIN
        int executedQueries = SqlStatementCountInspector.getCount();
        assertThat(executedQueries)
                .as("JOIN FETCH must fetch all authors and books in a single SQL statement")
                .isEqualTo(1);
    }

    @Test
    @DisplayName("Scenario 3: Fix N+1 with dynamic JPA @EntityGraph (exactly 1 query)")
    void fixWithEntityGraph() {
        SqlStatementCountInspector.reset();

        List<String> results = authorBookService.getAuthorsWithEntityGraph();

        assertThat(results).hasSize(5);

        // Exactly 1 SQL query with LEFT OUTER JOIN
        int executedQueries = SqlStatementCountInspector.getCount();
        assertThat(executedQueries)
                .as("@EntityGraph must fetch authors and books in a single SQL query")
                .isEqualTo(1);
    }

    @Test
    @DisplayName("Scenario 4: Fix N+1 with Hibernate @BatchSize (1 + 1 = 2 queries instead of 1 + 3 = 4 queries)")
    void fixWithBatchFetching() {
        SqlStatementCountInspector.reset();

        List<String> results = authorBookService.getPublishersWithBatchFetching();

        assertThat(results).hasSize(3);

        // 1 query for all publishers + 1 batched IN clause query for all their books
        int executedQueries = SqlStatementCountInspector.getCount();
        assertThat(executedQueries)
                .as("@BatchSize must batch lazy collection loading into a single IN-clause query")
                .isEqualTo(2);
    }

    @Test
    @DisplayName("Scenario 5: Fix N+1 with DTO Projection record (exactly 1 query, 0 entity overhead)")
    void fixWithDtoProjection() {
        SqlStatementCountInspector.reset();

        List<AuthorSummaryDto> summaries = authorBookService.getAuthorSummaries();

        assertThat(summaries).hasSize(5);
        assertThat(summaries.get(0).bookCount()).isEqualTo(3);

        // Exactly 1 query selecting scalar columns into an immutable record
        int executedQueries = SqlStatementCountInspector.getCount();
        assertThat(executedQueries)
                .as("DTO projections must execute a single targeted aggregate query")
                .isEqualTo(1);
    }
}
