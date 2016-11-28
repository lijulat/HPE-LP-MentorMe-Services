package com.livingprogress.mentorme.remote.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The list resources response.
 */
@Getter
@Setter
public class ListResourcesResponse {

    /**
     * The private resources.
     */
    @JsonProperty("private_resources")
    private List<PrivateResource> privateResources;

    /**
     * The private resource.
     */
    @Getter
    @Setter
    public static class PrivateResource {
        /**
         * The flavor.
         */
        private String flavor;

        /**
         * The resource.
         */
        private String resource;

        /**
         * The type.
         */
        private String type;
    }
}
