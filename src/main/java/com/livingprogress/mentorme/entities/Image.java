package com.livingprogress.mentorme.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * Represents the uploaded image.
 */
@Getter
@Setter
@Entity
public class Image extends AuditableUserEntity {
    /**
     * Represents the url.
     */
    private String url;

    /**
     * Represents the path.
     */
    private String path;
}
