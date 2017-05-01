package com.livingprogress.mentorme.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.livingprogress.mentorme.utils.Helper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * The skill.
 */
@Getter
@Setter
@Entity
public class Skill extends LookupEntity {
    /**
     * Represents the description of the skill.
     */
    private String description;

    /**
     * Represents the image path.
     */
    private String imagePath;

    /**
     * The localization data.
     */
    @OneToMany(mappedBy = "skill")
    @JsonIgnore
    private List<SkillLocale> locales;

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

