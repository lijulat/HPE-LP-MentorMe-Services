package com.livingprogress.mentorme.entities;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * The match mentee/mentor search criteria.
 */
@Getter
@Setter
public class MatchSearchCriteria {
    /**
     * The personal interest.
     */
    private List<PersonalInterest> personalInterests;

    /**
     * The professional interest.
     */
    private List<ProfessionalInterest> professionalInterests;

    /**
     * The max count.
     */
    private Integer maxCount;

    /**
     * The distance, unit is kilometers.
     * Remote service will use DISTSPHERICAL operators in
     * https://dev.havenondemand.com/docs/FieldTextOperators.html
     * Local service will use ST_Distance_Sphere in
     * http://dev.mysql.com/doc/refman/5.7/en/spatial-convenience-functions.html#function_st-distance-sphere
     */
    private BigDecimal distance;
}
