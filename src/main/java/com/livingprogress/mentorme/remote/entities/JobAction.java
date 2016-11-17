package com.livingprogress.mentorme.remote.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * The job action.
 */
@Getter
@Setter
public  class JobAction {
    /**
     * The action name.
     */
    private String name;

    /**
     * The version.
     */
    private String version;

    /**
     * The action params.
     */
    private Map<String, String> params;
}
