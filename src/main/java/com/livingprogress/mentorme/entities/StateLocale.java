package com.livingprogress.mentorme.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * The entity persisting localization data of a state.
 */
@Entity
@Setter
@Getter
public class StateLocale extends LocaleEntity {

  @ManyToOne
  private State state;
}
