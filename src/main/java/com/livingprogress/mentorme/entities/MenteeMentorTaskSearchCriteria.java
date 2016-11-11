package com.livingprogress.mentorme.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * The mentee mentor task search criteria.
 */
@Getter
@Setter
public class MenteeMentorTaskSearchCriteria {
    /**
     * The program id.
     */
    private Long menteeMentorProgramId;

    /**
     * The completed flag.
     */
    private Boolean completed;

    /**
     * The mentee id.
     */
    private Long menteeId;

    /**
     * The mentor assignment.
     */
    private Boolean mentorAssignment;

    /**
     * The mentee assignment.
     */
    private Boolean menteeAssignment;

    /**
     * The completed on.
     */
    private Date completedOn;

    /**
     * The mentee mentor goal id.
     */
    private Long menteeMentorGoalId;

    /**
     * The start date.
     */
    private Date startDate;

    /**
     * The end date.
     */
    private Date endDate;
}

