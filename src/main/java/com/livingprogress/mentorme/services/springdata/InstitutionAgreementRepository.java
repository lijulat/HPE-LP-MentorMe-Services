package com.livingprogress.mentorme.services.springdata;

import com.livingprogress.mentorme.entities.InstitutionAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * The InstitutionAgreement repository.
 */
public interface InstitutionAgreementRepository
        extends JpaRepository<InstitutionAgreement, Long>, JpaSpecificationExecutor<InstitutionAgreement> {
}

