package com.webcodein.documents.domain.model;

import jakarta.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    private UUID id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "title", nullable = false))
    private DocumentTitle title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status;

    protected Document() {}

    public Document(UUID id, DocumentTitle title) {
        if (id == null) {
            throw new IllegalArgumentException("Document ID cannot be null");
        }
        if (title == null) {
            throw new IllegalArgumentException("Document title cannot be null");
        }
        this.id = id;
        this.title = title;
        this.status = DocumentStatus.DRAFT;
    }

    public void publish() {
        if (this.status == DocumentStatus.ARCHIVED) {
            throw new IllegalStateException("Cannot publish an archived document");
        }
        this.status = DocumentStatus.PUBLISHED;
    }

    public void archive() {
        if (this.status == DocumentStatus.ARCHIVED) {
            throw new IllegalStateException("Document is already archived");
        }
        this.status = DocumentStatus.ARCHIVED;
    }

    public UUID getId() { return id; }
    public DocumentTitle getTitle() { return title; }
    public DocumentStatus getStatus() { return status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document document)) return false;
        return Objects.equals(id, document.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
