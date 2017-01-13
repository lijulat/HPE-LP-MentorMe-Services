package com.livingprogress.mentorme.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.util.Date;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * Created by wangjinggang on 2016/12/27.
 */
@Getter
@Setter
@Entity
public class MenteeMentorProgramRequest extends IdentifiableEntity {
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

    @Temporal(TIMESTAMP)
    private Date requestTime;


    @Enumerated(EnumType.STRING)
    private MenteeMentorProgramRequestStatus status;

    @Temporal(TIMESTAMP)
    private Date approvedOrRejectedTime;

}
