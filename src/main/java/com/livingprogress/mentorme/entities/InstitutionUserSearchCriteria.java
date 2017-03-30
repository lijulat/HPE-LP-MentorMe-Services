package com.livingprogress.mentorme.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * The institution user search criteria.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class InstitutionUserSearchCriteria {
    /**
     * The institution id.
     */
    private Long institutionId;

    /**
     * The status.
     */
    private UserStatus status;

    /**
     * The min average performance score.
     */
    private Integer minAveragePerformanceScore;

    /**
     * The max average performance score.
     */
    private Integer maxAveragePerformanceScore;

    /**
     * The name.
     */
    private String name;

    /**
     * The personal interest.
     */
    private List<PersonalInterest> personalInterests;

    /**
     * The professional interest.
     */
    private List<ProfessionalInterest> professionalInterests;

    /**
     * The min last modified on.
     */
    private Date minLastModifiedOn;

    /**
     * The max last modified on.
     */
    private Date maxLastModifiedOn;

    /**
     * The user ids.
     */
    private List<Long> ids;

    /**
     * The longitude to check distance.
     */
    @Max(180)
    @Min(-180)
    private BigDecimal longitude;

    /**
     * The latitude to check distance.
     */
    @Max(90)
    @Min(-90)
    private BigDecimal latitude;

    /**
     * The distance, unit is kilometers.
     * Remote service will use DISTSPHERICAL operators in
     * https://dev.havenondemand.com/docs/FieldTextOperators.html
     * Local service will use ST_Distance_Sphere in
     * http://dev.mysql.com/doc/refman/5.7/en/spatial-convenience-functions.html#function_st-distance-sphere
     */
    private BigDecimal distance;
}

