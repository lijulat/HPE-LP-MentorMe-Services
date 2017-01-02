package com.livingprogress.mentorme.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

/**
 * The goal.
 */
@Getter
@Setter
@Entity
public class Goal extends IdentifiableEntity {
    /**
     * The number of the goal in the goals list.
     */
    private int number;

    /**
     * The subject.
     */
    private String subject;

    /**
     * The description.
     */
    private String description;

    /**
     * The goal category.
     */
    @ManyToOne
    @JoinColumn(name = "goal_category_id")
    private GoalCategory goalCategory;

    /**
     * The duration in days.
     */
    private int durationInDays;

    /**
     * The tasks.
     */
    @OneToMany(mappedBy = "goalId", cascade = ALL)
    @OrderBy("number")
    private List<Task> tasks;

    /**
     * The institutional program id.
     */
    @Column(name = "institutional_program_id", insertable = false, updatable = false)
    private Long institutionalProgramId;

    /**
     * The institutional program.
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "institutional_program_id")
    private InstitutionalProgram institutionalProgram;

    /**
     * The useful links.
     */
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "goal_useful_link",
            joinColumns = {@JoinColumn(name = "goal_id")},
            inverseJoinColumns = {@JoinColumn(name = "useful_link_id")})
    private List<UsefulLink> usefulLinks;

    /**
     * The documents.
     */
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "goal_document",
            joinColumns = {@JoinColumn(name = "goal_id")},
            inverseJoinColumns = {@JoinColumn(name = "document_id")})
    private List<Document> documents;

    /**
     * Flag for the custom goal (the custom goal created for the mentee-mentor pair).
     */
    private boolean custom;

    /**
     * Custom assigned goal data.
     */
    @OneToOne(mappedBy = "goal", cascade = ALL, orphanRemoval = true)
    private CustomAssignedGoalData customData;
}

