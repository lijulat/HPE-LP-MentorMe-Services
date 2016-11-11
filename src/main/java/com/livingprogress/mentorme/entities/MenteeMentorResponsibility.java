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

/**
 * The mentee mentor responsibility.
 */
@Getter
@Setter
@Entity
public class MenteeMentorResponsibility extends IdentifiableEntity {
    /**
     * The number in the list.
     */
    private int number;

    /**
     * The title.
     */
    private String title;

    /**
     * The date.
     */
    @Temporal(DATE)
    private Date date;

    /**
     * The mentee responsibility.
     */
    private Boolean menteeResponsibility;

    /**
     * The mentor responsibility.
     */
    private Boolean mentorResponsibility;

    /**
     * The responsibility id.
     */
    @JoinColumn(name = "responsibility_id")
    private long responsibilityId;

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

