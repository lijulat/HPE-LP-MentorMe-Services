package com.livingprogress.mentorme.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * The task.
 */
@Getter
@Setter
@Entity
public class Task extends IdentifiableEntity {
    /**
     * The number of the task in the tasks list.
     */
    private int number;

    /**
     * The description.
     */
    private String description;

    /**
     * The duration in days.
     */
    private int durationInDays;

    /**
     * The mentor assignment.
     */
    private Boolean mentorAssignment;

    /**
     * The mentee assignment.
     */
    private Boolean menteeAssignment;

    /**
     * The goal id.
     */
    @Column(name = "goal_id", insertable = false, updatable = false)
    private long goalId;

    /**
     * The goal.
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "goal_id")
    private Goal goal;
}

