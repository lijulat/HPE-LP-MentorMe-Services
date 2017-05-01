package com.livingprogress.mentorme.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import java.util.Date;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.TemporalType.DATE;

/**
 * The institutional program.
 */
@Getter
@Setter
@Entity
public class InstitutionalProgram extends AuditableEntity {
    /**
     * The program name.
     */
    private String programName;

    private String description;

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
     * The institution.
     */
    @ManyToOne
    @JoinColumn(name = "institution_id")
    private Institution institution;

    /**
     * The goals.
     */
    @OneToMany(mappedBy = "institutionalProgram", cascade = ALL)
    @OrderBy("number")
    private List<Goal> goals;

    /**
     * The responsibilities.
     */
    @OneToMany(mappedBy = "institutionalProgram", cascade = ALL)
    private List<Responsibility> responsibilities;

    /**
     * The responsibilities.
     */
    @OneToMany(mappedBy = "institutionalProgram", cascade = ALL)
    private List<InstitutionalProgramSkill> skills;

    /**
     * The documents.
     */
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "institutional_program_document",
            joinColumns = {@JoinColumn(name = "institutional_program_id")},
            inverseJoinColumns = {@JoinColumn(name = "document_id")})
    private List<Document> documents;

    /**
     * The useful links.
     */
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "institutional_program_link",
            joinColumns = {@JoinColumn(name = "institutional_program_id")},
            inverseJoinColumns = {@JoinColumn(name = "useful_link_id")})
    private List<UsefulLink> usefulLinks;

    /**
     * The duration in days.
     */
    private int durationInDays;

    /**
     * The program url.
     */
    private String programImageUrl;

    /**
     * The locale;
     */
    @ManyToOne
    @JoinColumn(name = "locale_id", nullable = false)
    private Locale locale;
}

