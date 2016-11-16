package com.livingprogress.mentorme.remote.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The find similar response.
 */
@Getter
@Setter
public class FindSimilarResponse {
    /**
     * The details of the returned documents.
     */
    private List<SimilarDocument> documents;

    /**
     * The similar document response.
     */
    @Getter
    @Setter
    public static class SimilarDocument {
        /**
         *  The reference string that identifies the result document.
         */
        private String reference;
    }
}
