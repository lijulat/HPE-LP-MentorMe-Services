package com.livingprogress.mentorme.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * The entity persisting localization data of a locale.
 */
@Entity
@Setter
@Getter
public class SkillLocale extends LocaleEntity {

  @ManyToOne
  private Skill skill;
}
