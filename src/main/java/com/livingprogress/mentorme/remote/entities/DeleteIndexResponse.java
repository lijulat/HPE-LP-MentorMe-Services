package com.livingprogress.mentorme.remote.entities;

import lombok.Getter;
import lombok.Setter;

/**
 * The delete index response.
 */
@Getter
@Setter
public class DeleteIndexResponse {
    /**
     * The confirmation hash required for deletion.
     */
    private String confirm;

    /**
     * Whether or not the index was deleted.
     */
    private Boolean deleted;

    /**
     *  The index name.
     */
    private String index;
}
