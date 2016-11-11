package com.livingprogress.mentorme.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * The document.
 */
@Getter
@Setter
@Entity
public class Document extends AuditableUserEntity {
    /**
     * The name.
     */
    private String name;

    /**
     * The path to the document.
     */
    private String path;
}

