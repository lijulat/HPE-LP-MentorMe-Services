package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The document type repository.
 */
public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {
}

