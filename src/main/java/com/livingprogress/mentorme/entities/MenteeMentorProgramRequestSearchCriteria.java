package com.livingprogress.mentorme.entities;

import lombok.Getter;
import lombok.Setter;


/**
 * The mentee mentor goal search criteria.
 */
@Getter
@Setter
public class MenteeMentorProgramRequestSearchCriteria {
    /**
     * The mentee id.
     */
    private Long menteeId;

    private Long mentorId;

    private MenteeMentorProgramRequestStatus status;
}

