package com.livingprogress.mentorme.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import java.util.Date;

import static javax.persistence.TemporalType.DATE;
import static javax.persistence.TemporalType.TIMESTAMP;


/**
 * The mentee mentor task.
 */
@Getter
@Setter
@Entity
public class MenteeMentorTask extends IdentifiableEntity {
    /**
     * The task.
     */
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    /**
     * The completed flag.
     */
    private boolean completed;

    /**
     * The completed on.
     */
    @Temporal(TIMESTAMP)
    private Date completedOn;

    /**
     * The mentee mentor goal id.
     */
    @Column(name = "mentee_mentor_goal_id", insertable = false, updatable = false)
    private long menteeMentorGoalId;

    /**
     * The start date.
     */
    @Temporal(DATE)
    private Date startDate;

    /**
     * The end date.
     */
    @Temporal(DATE)
    private Date endDate;


    /**
     * The mentee mentor goal.
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "mentee_mentor_goal_id")
    private MenteeMentorGoal menteeMentorGoal;
}

