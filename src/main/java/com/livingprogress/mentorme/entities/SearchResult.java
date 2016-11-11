package com.livingprogress.mentorme.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The search result.
 * @param <T> the entity type
 */
@Getter
@Setter
public class SearchResult<T> {
    /**
     * The total number.
     */
    private long total;

    /**
     * The total pages number.
     */
    private int totalPages;

    /**
     * The values.
     */
    private List<T> entities;
}

