package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * The image repository.
 */
public interface ImageRepository extends JpaRepository<Image, Long>, JpaSpecificationExecutor<Image> {
}

