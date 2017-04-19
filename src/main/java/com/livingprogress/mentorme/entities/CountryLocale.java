package com.livingprogress.mentorme.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * The entity persisting localization data of a country.
 */
@Entity
@Setter
@Getter
public class CountryLocale extends LocaleEntity {

  @ManyToOne
  private Country country;
}
