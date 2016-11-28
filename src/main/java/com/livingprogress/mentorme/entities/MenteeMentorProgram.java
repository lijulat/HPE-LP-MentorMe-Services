package com.livingprogress.mentorme.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import java.util.Date;
import java.util.List;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.TemporalType.DATE;
import static javax.persistence.TemporalType.TIMESTAMP;
import static javax.persistence.CascadeType.ALL;

/**
 * The mentee-mentor program.
 */
@Getter
@Setter
@Entity
public class MenteeMentorProgram extends IdentifiableEntity {
    /**
     * The mentor.
     */
    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "mentor_id")
    private Mentor mentor;

    /**
     * The mentee.
     */
    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "mentee_id")
    private Mentee mentee;

    /**
     * The institutional program.
     */
    @ManyToOne
    @JoinColumn(name = "institutional_program_id")
    private InstitutionalProgram institutionalProgram;

    /**
     * The mentor feedback.
     */
    @OneToOne(cascade = {MERGE, REMOVE})
    @JoinColumn(name = "mentor_feedback_id")
    private MentorFeedback mentorFeedback;

    /**
     * The mentee feedback.
     */
    @OneToOne(cascade = {MERGE, REMOVE})
    @JoinColumn(name = "mentee_feedback_id")
    private MenteeFeedback menteeFeedback;

    /**
     * The start date.
     */
    @Temporal(DATE)
    private Date startDate;

    /**
     * The end date.
     */
    @Temporal(DATE)
    private Date endDate;

    /**
     * The mentee mentor goals.
     */
    @OneToMany(mappedBy = "menteeMentorProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenteeMentorGoal> goals;

    /**
     * The mentee mentor responsibilities.
     */
    @OneToMany(mappedBy = "menteeMentorProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("number")
    private List<MenteeMentorResponsibility> responsibilities;

    /**
     * The completed flag.
     */
    private boolean completed;

    /**
     * The completed on date.
     */
    @Temporal(TIMESTAMP)
    private Date completedOn;

    /**
     * The request status.
     */
    @Enumerated(STRING)
    private MenteeMentorProgramRequestStatus requestStatus;
    
    /**
     * The documents.
     */
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "mentee_mentor_program_document",
            joinColumns = {@JoinColumn(name = "mentee_mentor_program_id")},
            inverseJoinColumns = {@JoinColumn(name = "document_id")})
    private List<Document> documents;

    /**
     * The useful links.
     */
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "mentee_mentor_program_useful_link",
            joinColumns = {@JoinColumn(name = "mentee_mentor_program_id")},
            inverseJoinColumns = {@JoinColumn(name = "useful_link_id")})
    private List<UsefulLink> usefulLinks;
}

