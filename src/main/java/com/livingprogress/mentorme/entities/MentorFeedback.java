package com.livingprogress.mentorme.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import java.util.Date;

/**
 * The mentor feedback.
 */
@Getter
@Setter
@Entity
public class MentorFeedback extends IdentifiableEntity {
    /**
     * The mentee score.
     */
    private Integer menteeScore;

    /**
     * The comment.
     */
    private String comment;

    private Date createdOn;
}

