package com.livingprogress.mentorme.remote.services.impl;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.remote.services.GeocodingService;
import com.livingprogress.mentorme.utils.CustomMessageSource;
import com.livingprogress.mentorme.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * The geocoding service the Google Maps Geocoding API
 * (https://developers.google.com/maps/documentation/geocoding/intro)
 * to convert provided address info into Longitude and Latitude Coordinates.
 */
@Service
public class GeocodingServiceImpl implements GeocodingService {

    /**
     * The geo api context.
     */
    private final GeoApiContext context;

    /**
     * The geocoding service constructor.
     *
     * @param apiKey the google api key for Geocoding API.
     */
    @Autowired
    private GeocodingServiceImpl(@Value("${google.geocoding.apiKey}") String apiKey) {
        context = new GeoApiContext().setApiKey(apiKey);
    }

    /**
     * Convert provided address info into Longitude and Latitude Coordinates.
     *
     * @param address the address info.
     * @return the geocoding result.
     * @throws MentorMeException if any other error occurred during operation
     */
    @Override
    public GeocodingResult geocode(String address) throws MentorMeException {
        if (Helper.isNullOrEmpty(address)) {
            return null;
        }
        try {
            GeocodingResult[] results = GeocodingApi.geocode(context, address)
                                                    .await();
            if (results != null && results.length > 0) {
                return results[0];
            }
            return null;
        } catch (Exception e) {
            throw new MentorMeException(CustomMessageSource.getMessage("geocode.error"), e);
        }
    }
}
