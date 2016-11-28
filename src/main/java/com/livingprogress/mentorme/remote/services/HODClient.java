package com.livingprogress.mentorme.remote.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.livingprogress.mentorme.entities.Mentee;
import com.livingprogress.mentorme.entities.Mentor;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.remote.entities.Documents;
import com.livingprogress.mentorme.remote.entities.JobActions;
import com.livingprogress.mentorme.remote.entities.JobResultResponse;
import com.livingprogress.mentorme.entities.MatchSearchCriteria;

import java.util.List;

/**
 * The hod client.Implementation should be effectively thread-safe.
 */
public interface HODClient {

    /**
     * List resources.
     * @return the job id result.
     * @throws IllegalStateException throws if any error happens.
     */
    JobResultResponse listResources();

    /**
     * Create index.
     * @return the job id result.
     * @throws IllegalStateException throws if any error happens.
     */
    JobResultResponse createIndex();

    /**
     * Add index.
     * @param index the index name.
     * @param documents the document.
     * @return the job id result.
     * @throws IllegalArgumentException if any argument is invalid.
     * @throws IllegalStateException throws if any error happens.
     */
    JobResultResponse addIndex(String index, Documents documents);

    /**
     * Get job result.
     * @param jobId the job id.
     * @return the json node with job result.
     * @throws IllegalArgumentException if any argument is invalid.
     * @throws IllegalStateException throws if any error happens.
     */
    JsonNode getJobResult(String jobId);

    /**
     * Submit job.
     * @param jobActions the job actions.
     * @return the job id result.
     * @throws IllegalArgumentException if any argument is invalid.
     * @throws IllegalStateException throws if any error happens.
     */
    JobResultResponse submitJob(JobActions jobActions);

    /**
     * Get matching mentees.
     * @param entity the mentor.
     * @param criteria the remote criteria
     * @return the match mentee ids.
     * @throws IllegalArgumentException if any argument is invalid.
     * @throws MentorMeException throws if any error happens.
     */
    List<Long> getMatchingMentees(Mentor entity, MatchSearchCriteria criteria) throws MentorMeException;

    /**
     * Get matching mentors.
     * @param entity the mentee.
     * @param criteria the remote criteria
     * @return the match mentor ids.
     * @throws IllegalArgumentException if any argument is invalid.
     * @throws MentorMeException throws if any error happens.
     */
    List<Long> getMatchingMentors(Mentee entity, MatchSearchCriteria criteria) throws MentorMeException;
}
