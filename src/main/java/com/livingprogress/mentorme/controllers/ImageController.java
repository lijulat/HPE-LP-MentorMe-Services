package com.livingprogress.mentorme.controllers;

import com.livingprogress.mentorme.entities.Document;
import com.livingprogress.mentorme.entities.Image;
import com.livingprogress.mentorme.entities.ImageSearchCriteria;
import com.livingprogress.mentorme.entities.SearchResult;
import com.livingprogress.mentorme.exceptions.ConfigurationException;
import com.livingprogress.mentorme.exceptions.EntityNotFoundException;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.services.ImageService;
import com.livingprogress.mentorme.utils.Helper;
import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.*;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The Images upload/request controller. Is effectively thread safe.
 */
@RestController
@RequestMapping("/images")
@NoArgsConstructor
public class ImageController extends BaseUploadController {

    /**
     * Represents the logger
     */
    public static final Logger LOGGER = Logger.getLogger("com.livingprogress.mentorme");

    @Autowired
    private ImageService imageService;

    /**
     * Check if all required fields are initialized properly.
     *
     * @throws ConfigurationException if any required field is not initialized properly.
     */
    @PostConstruct
    protected void checkConfiguration() {
        super.checkConfiguration();
    }



    /**
     * This method is used to create an entity.
     *
     * @param documents the documents
     * @return the created entity
     * @throws IllegalArgumentException if entity is null or not valid
     * @throws MentorMeException if any other error occurred during operation
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public List<Image> create(@RequestParam("files") MultipartFile[] documents) throws MentorMeException  {
        List<Document> docs = Helper.uploadDocuments(getUploadDirectory(), documents);
        List<Image> images = new ArrayList<>();
        for (Document d : docs) {
            Image image = new Image();
            image.setPath(d.getPath());
            image.setUrl("/images/" + UUID.randomUUID().toString());
            image = imageService.create(image);
            images.add(image);
        }
        return images;
    }


    /**
     * Gets the image data.
     * @param imageUrl the url of the image.
     * @return the binary data of the image.
     * @throws MentorMeException if there are any errors.
     * @throws FileNotFoundException if there are any errors.
     */
    @RequestMapping(value = "/{imageUrl}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<InputStreamResource> downloadUserAvatarImage(@PathVariable String imageUrl) throws MentorMeException, FileNotFoundException {

        ImageSearchCriteria criteria = new ImageSearchCriteria();
        criteria.setUrl("/images/" + imageUrl);
        SearchResult<Image> result = imageService.search(criteria, null);

        if (result.getEntities() == null | result.getEntities().size() == 0) {
            throw new EntityNotFoundException("Image not found for the imageUrl: " + imageUrl);
        }

        Image imageInfo = result.getEntities().get(0);
        File file = new File(imageInfo.getPath());
        if (!file.exists() || !file.isFile()) {
            throw new EntityNotFoundException("Image not found in the path: " + imageInfo.getPath());
        }

        long fileLength = file.length();
        String contentType = getContentType(file);

        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        InputStream is = new FileInputStream(file);
        return ResponseEntity.ok()
                .contentLength(fileLength)
                .contentType(MediaType.parseMediaType(contentType))
                .body(new InputStreamResource(is));
    }

    /**
     * Gets the content type.
     * @param file the file.
     * @return the content type.
     */
    private String getContentType(File file) {
        final String signature = this.getClass().getCanonicalName() + ".getContentType";
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            return URLConnection.guessContentTypeFromStream(is);
        } catch (FileNotFoundException e) {
            Helper.logException(LOGGER, signature, e);
            return null;
        } catch (IOException e) {
            Helper.logException(LOGGER, signature, e);
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // ignore
                    Helper.logException(LOGGER, signature, e);
                }
            }
        }

    }

}

