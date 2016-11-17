package com.livingprogress.mentorme.remote.services;

import com.google.maps.model.GeocodingResult;
import com.livingprogress.mentorme.exceptions.MentorMeException;

/**
 * The geocoding service.Implementation should be effectively thread-safe.
 */
public interface GeocodingService {

    /**
     * Convert provided address info into Longitude and Latitude Coordinates.
     * @param address the address info.
     * @return the geocoding result.
     * @throws MentorMeException if any other error occurred during operation
     */
    GeocodingResult geocode(String address) throws MentorMeException;
}
