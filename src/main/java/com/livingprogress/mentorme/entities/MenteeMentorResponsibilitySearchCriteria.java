package com.livingprogress.mentorme.entities;

import lombok.Getter;
import lombok.Setter;



/**
 * The mentee mentor responsibility search criteria.
 */
@Getter
@Setter
public class MenteeMentorResponsibilitySearchCriteria {
    /**
     * The program id.
     */
    private Long menteeMentorProgramId;

    /**
     * The responsibility id.
     */
    private Long responsibilityId;
}

