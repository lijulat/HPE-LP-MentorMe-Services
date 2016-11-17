package com.livingprogress.mentorme.remote.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The interest category information.
 */
@Getter
@Setter
public class InterestCategory {
    /**
     * The interest categories.
     */
    private List<String> interestCategories;

    /**
     * The parent interest categories.
     */
    private List<String> parentInterestCategories;

    /**
     * The categories.
     */
    private List<String> categories;
}
