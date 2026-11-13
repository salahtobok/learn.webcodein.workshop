package com.webcodein.workshop.ai.hybrid.model;

public record SearchResult(
        String documentId,
        String title,
        String matchedSnippet,
        double score,
        String matchType
) implements Comparable<SearchResult> {

    @Override
    public int compareTo(SearchResult other) {
        return Double.compare(other.score, this.score); // descending order
    }
}
