package com.livingprogress.mentorme.services;

import com.livingprogress.mentorme.entities.Goal;
import com.livingprogress.mentorme.entities.GoalSearchCriteria;

/**
 * The goal service. Extends generic service interface.Implementation should be effectively thread-safe.
*/
public interface GoalService extends GenericService<Goal, GoalSearchCriteria> {
}

