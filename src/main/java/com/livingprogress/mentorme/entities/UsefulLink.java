package com.livingprogress.mentorme.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * The useful link.
 */
@Getter
@Setter
@Entity
public class UsefulLink extends IdentifiableEntity {
    /**
     * The title.
     */
    private String title;

    /**
     * The link address.
     */
    private String address;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User author;

    private Date createdOn;
}

