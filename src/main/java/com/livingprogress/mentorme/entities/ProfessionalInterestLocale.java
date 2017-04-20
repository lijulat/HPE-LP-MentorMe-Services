package com.livingprogress.mentorme.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * The entity persisting localization data of a professional interest.
 */
@Entity
@Setter
@Getter
public class ProfessionalInterestLocale extends LocaleEntity {

  @ManyToOne
  private ProfessionalInterest professionalInterest;
}
