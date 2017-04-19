package com.livingprogress.mentorme.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * The entity persisting localization data of a professional consultant area.
 */
@Entity
@Setter
@Getter
public class ProfessionalConsultantAreaLocale extends LocaleEntity {

  @ManyToOne
  private ProfessionalConsultantArea professionalConsultantArea;
}
