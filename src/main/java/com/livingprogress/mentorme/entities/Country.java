package com.livingprogress.mentorme.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.livingprogress.mentorme.utils.Helper;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * The country.
 */
@Entity
public class Country extends LookupEntity {

  /**
   * The localization data.
   */
  @OneToMany(mappedBy = "country")
  @JsonIgnore
  private List<CountryLocale> locales;

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

