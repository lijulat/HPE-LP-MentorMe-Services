package com.livingprogress.mentorme.remote.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The job actions request.
 */
@Getter
@Setter
@AllArgsConstructor
public class JobActions {

    /**
     * The job actions.
     */
    private List<JobAction> actions;
}
