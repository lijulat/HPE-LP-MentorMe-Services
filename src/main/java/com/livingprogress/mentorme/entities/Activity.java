package com.livingprogress.mentorme.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.EAGER;

/**
 * The activity.
 */
@Getter
@Setter
@Entity
public class Activity extends AuditableUserEntity {
    /**
     * The institutional program id.
     */
    @JoinColumn(name = "institutional_program_id")
    private Long institutionalProgramId;

    /**
     * The activity type.
     */
    @Enumerated(EnumType.STRING)
    private ActivityType activityType;

    /**
     * The object id.
     */
    private long objectId;

    /**
     * The description.
     */
    private String description;

    /**
     * Mentee id.
     */
    @Column(name = "mentee_id")
    private Long menteeId;

    /**
     * Mentor id.
     */
    @Column(name = "mentor_id")
    private Long mentorId;


    /**
     * The global flag.
     */
    private boolean global;

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "created_by", insertable = false, updatable = false)
    private User author;
}

