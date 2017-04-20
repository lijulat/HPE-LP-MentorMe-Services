package com.livingprogress.mentorme.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.livingprogress.mentorme.utils.Helper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * The personal interest.
 */
@Getter
@Setter
@Entity
public class PersonalInterest extends LookupEntity {
    /**
     * The picture path.
     */
    private String picturePath;

    /**
     * The parent category.
     */
    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private PersonalInterest parentCategory;

    /**
     * The localization data.
     */
    @OneToMany(mappedBy = "personalInterest")
    @JsonIgnore
    private List<PersonalInterestLocale> locales;

    /**
     * Return the localized representation.
     *
     * @return the value
     */
    @JsonProperty("value")
    public String getLocaleValue() {
        return Helper.getLocaleString(this, locales);
    }
}

