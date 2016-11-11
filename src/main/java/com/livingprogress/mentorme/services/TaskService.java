package com.livingprogress.mentorme.services;

import com.livingprogress.mentorme.entities.Task;
import com.livingprogress.mentorme.entities.TaskSearchCriteria;

/**
 * The task service. Extends generic service interface.Implementation should be effectively thread-safe.
*/
public interface TaskService extends GenericService<Task, TaskSearchCriteria> {
}

