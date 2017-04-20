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
 * The state.
 */
@Getter
@Setter
@Entity
public class State extends LookupEntity {
    @JsonIgnore
    Long countryId;

    /**
     * The localization data.
     */
    @OneToMany(mappedBy = "state")
    @JsonIgnore
    private List<StateLocale> locales;

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

