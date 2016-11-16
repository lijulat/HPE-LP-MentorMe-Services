package com.livingprogress.mentorme.remote.entities;

import lombok.Getter;
import lombok.Setter;

/**
 * The create index response.
 */
@Getter
@Setter
public class CreateIndexResponse {
    /**
     * The name of the new index.
     */
    private String index;

    /**
     * Indicates that the index was created.
     */
    private String message;
}
