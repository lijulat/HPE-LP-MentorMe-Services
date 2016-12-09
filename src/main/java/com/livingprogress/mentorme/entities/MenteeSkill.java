package com.livingprogress.mentorme.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Represents the mentor skills.
 */
@Getter
@Setter
@Entity
public class MenteeSkill extends IdentifiableEntity {
    /**
     * The user id.
     */
    @Column(name = "user_id", insertable = false, updatable = false)
    private long userId;

    /**
     * The user.
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    /**
     * Represents the skill.
     */
    @ManyToOne
    @JoinColumn(name = "skill_id")
    private Skill skill;
}
