package com.livingprogress.mentorme.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import java.util.Date;
import java.util.List;

import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * The mentee mentor goal.
 */
@Getter
@Setter
@Entity
public class MenteeMentorGoal extends IdentifiableEntity {
    /**
     * The goal.
     */
    @ManyToOne
    @JoinColumn(name = "goal_id")
    private Goal goal;

    /**
     * The completed flag.
     */
    private boolean completed;

    /**
     * The completed date.
     */
    @Temporal(TIMESTAMP)
    private Date completedOn;

    /**
     * The goal's tasks.
     */
    @OneToMany(mappedBy = "menteeMentorGoalId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenteeMentorTask> tasks;

    /**
     * The mentee-mentor program id.
     */
    @Column(name = "mentee_mentor_program_id", insertable = false, updatable = false)
    private long menteeMentorProgramId;

    /**
     * The mentee mentor program.
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "mentee_mentor_program_id")
    private MenteeMentorProgram menteeMentorProgram;


}

