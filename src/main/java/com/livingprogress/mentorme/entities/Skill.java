package com.livingprogress.mentorme.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * The skill.
 */
@Getter
@Setter
@Entity
public class Skill extends LookupEntity {
    /**
     * Represents the description of the skill.
     */
    private String description;

    /**
     * Represents the image path.
     */
    private String imagePath;
}

