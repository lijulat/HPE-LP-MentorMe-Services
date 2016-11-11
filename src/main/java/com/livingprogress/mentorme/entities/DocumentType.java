package com.livingprogress.mentorme.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * The document type.
 */
@Getter
@Setter
@Entity
public class DocumentType extends LookupEntity {

    /**
     * The icon path.
     */
    private String iconPath;
}

