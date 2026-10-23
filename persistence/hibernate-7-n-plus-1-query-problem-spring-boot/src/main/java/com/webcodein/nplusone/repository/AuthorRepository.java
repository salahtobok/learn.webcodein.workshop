package com.webcodein.nplusone.repository;

import com.webcodein.nplusone.domain.Author;
import com.webcodein.nplusone.dto.AuthorSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    /**
     * Fix 1: JPQL JOIN FETCH.
     * Fetches authors and their books in a single SQL query using an INNER JOIN.
     * DISTINCT avoids duplicate author roots in memory.
     */
    @Query("SELECT DISTINCT a FROM Author a JOIN FETCH a.books")
    List<Author> findAllWithBooksJoinFetch();

    /**
     * Fix 2: Dynamic @EntityGraph.
     * Declaratively tells JPA and Hibernate to eagerly load the specified association
     * for this query only, overriding the default LAZY fetching.
     */
    @EntityGraph(attributePaths = {"books"})
    @Query("SELECT a FROM Author a")
    List<Author> findAllWithBooksEntityGraph();

    /**
     * Fix 3: Direct DTO Projection using Java 25 Record.
     * Selects only required scalar columns into an immutable record.
     * Completely bypasses Hibernate entity hydration and first-level cache overhead.
     */
    @Query("SELECT new com.webcodein.nplusone.dto.AuthorSummaryDto(a.id, a.name, a.email, count(b.id)) " +
           "FROM Author a LEFT JOIN a.books b GROUP BY a.id, a.name, a.email")
    List<AuthorSummaryDto> findAllAuthorSummaries();

    /**
     * Paginated fetch using EntityGraph.
     */
    @EntityGraph(attributePaths = {"books"})
    Page<Author> findAll(Pageable pageable);
}
