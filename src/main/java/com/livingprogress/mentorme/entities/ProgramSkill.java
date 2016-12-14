package com.livingprogress.mentorme.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Represents the program skill.
 */
@Getter
@Setter
@Entity
public class ProgramSkill extends IdentifiableEntity {
    /**
     * The user id.
     */
    @Column(name = "institutional_program_id", insertable = false, updatable = false)
    private long institutionalProgramId;

    /**
     * The user.
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "institutional_program_id")
    private InstitutionalProgram institutionalProgram;


    /**
     * Represents the skill.
     */
    @ManyToOne
    @JoinColumn(name = "skill_id")
    private Skill skill;
}
