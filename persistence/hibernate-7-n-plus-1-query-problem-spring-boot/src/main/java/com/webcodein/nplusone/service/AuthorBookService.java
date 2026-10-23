package com.webcodein.nplusone.service;

import com.webcodein.nplusone.domain.Author;
import com.webcodein.nplusone.domain.Publisher;
import com.webcodein.nplusone.dto.AuthorSummaryDto;
import com.webcodein.nplusone.repository.AuthorRepository;
import com.webcodein.nplusone.repository.PublisherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AuthorBookService {

    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;

    public AuthorBookService(AuthorRepository authorRepository, PublisherRepository publisherRepository) {
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
    }

    /**
     * Demonstrates the classic N+1 problem:
     * 1 query to fetch all authors, followed by N queries when accessing author.getBooks().
     */
    public List<String> getAuthorsWithUnoptimizedLazyLoading() {
        List<Author> authors = authorRepository.findAll();
        return authors.stream()
                .map(author -> author.getName() + " authored " + author.getBooks().size() + " books")
                .toList();
    }

    /**
     * Fix 1: Single query with JPQL JOIN FETCH.
     */
    public List<String> getAuthorsWithJoinFetch() {
        List<Author> authors = authorRepository.findAllWithBooksJoinFetch();
        return authors.stream()
                .map(author -> author.getName() + " authored " + author.getBooks().size() + " books")
                .toList();
    }

    /**
     * Fix 2: Single query with dynamic JPA @EntityGraph.
     */
    public List<String> getAuthorsWithEntityGraph() {
        List<Author> authors = authorRepository.findAllWithBooksEntityGraph();
        return authors.stream()
                .map(author -> author.getName() + " authored " + author.getBooks().size() + " books")
                .toList();
    }

    /**
     * Fix 3: Hibernate Batch Fetching via @BatchSize.
     * Triggers 1 query to fetch all publishers, followed by 1 batched IN-clause query
     * fetching all associated books for all publishers at once.
     */
    public List<String> getPublishersWithBatchFetching() {
        List<Publisher> publishers = publisherRepository.findAll();
        return publishers.stream()
                .map(publisher -> publisher.getName() + " published " + publisher.getBooks().size() + " books")
                .toList();
    }

    /**
     * Fix 4: DTO Projections.
     * Executes a single aggregated SQL query without entity lifecycle overhead.
     */
    public List<AuthorSummaryDto> getAuthorSummaries() {
        return authorRepository.findAllAuthorSummaries();
    }
}
