package com.livingprogress.mentorme.services;

import com.livingprogress.mentorme.entities.Document;
import com.livingprogress.mentorme.entities.DocumentSearchCriteria;

/**
 * The document service. Extends generic service interface.Implementation should be effectively thread-safe.
*/
public interface DocumentService extends GenericService<Document, DocumentSearchCriteria> {
}

