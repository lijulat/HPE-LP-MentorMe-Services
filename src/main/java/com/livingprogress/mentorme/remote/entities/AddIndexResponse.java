package com.livingprogress.mentorme.remote.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The add index response.
 */
@Getter
@Setter
public class AddIndexResponse {
    /**
     * The index name.
     */
    private String index;

    /**
     * The references.
     */
    private List<References> references;

    /**
     * The Reference.
     */
    @Getter
    @Setter
    public static class References {
        /**
         * The id.
         */
        private Integer id;

        /**
         * The reference.
         */
        private String reference;
    }
}
