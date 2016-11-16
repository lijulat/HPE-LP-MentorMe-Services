package com.livingprogress.mentorme.remote.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

/**
 * The document.
 */
@Getter
@Setter
public class Document {

    /**
     * The content.
     */
    private String content;

    /**
     * The reference.
     */
    private String reference;

    /**
     * The content type.
     */
    @JsonProperty("content_type")
    private String contentType;

    /**
     * The interest categories.
     */
    private Set<String> interestCategories;

    /**
     * The parent interest categories.
     */
    private Set<String> parentInterestCategories;

    /**
     * The is virtual user flag.
     */
    private String isVirtualUser;

    /**
     * The assigned to institution flag.
     */
    private String assignedToInstitution;

    /**
     * The institution id.
     */
    private String institutionId;

    /**
     * The longitude.
     */
    private BigDecimal lon;

    /**
     * The latitude.
     */
    private BigDecimal lat;
}
