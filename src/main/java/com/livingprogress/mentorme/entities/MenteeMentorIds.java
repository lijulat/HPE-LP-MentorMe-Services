package com.livingprogress.mentorme.entities;



import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The mentee mentor user ids used to clone the InstitutionalProgram
 */
@Getter
@Setter
@NoArgsConstructor
public class MenteeMentorIds {
    /**
     * The mentee user id
     */
    private long menteeId;
    
    /**
     * The mentor user id
     */
    private long mentorId;

}
