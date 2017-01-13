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
     * The documents.
     */
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "task_document",
            joinColumns = {@JoinColumn(name = "task_id")},
            inverseJoinColumns = {@JoinColumn(name = "document_id")})
    private List<Document> documents;

    /**
     * The useful links.
     */
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "task_useful_link",
            joinColumns = {@JoinColumn(name = "task_id")},
            inverseJoinColumns = {@JoinColumn(name = "useful_link_id")})
    private List<UsefulLink> usefulLinks;

    /**
     * Flag for the custom task (the custom goal created for the mentee-mentor pair).
     */
    private boolean custom;

    /**
     * Custom assigned task data.
     */
    @OneToOne(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private CustomAssignedTaskData customData;

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

