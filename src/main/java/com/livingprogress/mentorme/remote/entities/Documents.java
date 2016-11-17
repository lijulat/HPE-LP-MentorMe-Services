package com.livingprogress.mentorme.remote.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * The documents to index.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Documents {

    /**
     * The documents.
     */
    private List<Document> documents;
}
