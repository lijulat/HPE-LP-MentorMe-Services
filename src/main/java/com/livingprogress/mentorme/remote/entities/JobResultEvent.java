package com.livingprogress.mentorme.remote.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The job id with action request.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobResultEvent {

    /**
     * The job actions.
     */
    private JobActions jobActions;

    /**
     * The job result.
     */
    private JobResultResponse jobResult;

    /**
     * The action.
     */
    private String action;
}
