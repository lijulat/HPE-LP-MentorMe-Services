package com.livingprogress.mentorme.controllers;

import com.livingprogress.mentorme.exceptions.ConfigurationException;
import com.livingprogress.mentorme.utils.Helper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

import static lombok.AccessLevel.PROTECTED;

/**
 * The BaseUpload REST controller to provide upload related methods.
 */
@NoArgsConstructor(access = PROTECTED)
public abstract class BaseUploadController {

    /**
     * The upload directory.
     */
    @Value("${uploadDirectory}")
    @Getter(value = PROTECTED)
    private String uploadDirectory;

    /**
     * Check if all required fields are initialized properly.
     *
     * @throws ConfigurationException if any required field is not initialized properly.
     */
    @PostConstruct
    protected void checkConfiguration() {
        Helper.checkDirectory(uploadDirectory, "uploadDirectory");
    }
}
