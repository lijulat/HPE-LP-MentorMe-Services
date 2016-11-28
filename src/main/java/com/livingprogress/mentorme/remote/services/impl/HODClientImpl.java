package com.livingprogress.mentorme.remote.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.livingprogress.mentorme.entities.InstitutionUser;
import com.livingprogress.mentorme.entities.MatchSearchCriteria;
import com.livingprogress.mentorme.entities.Mentee;
import com.livingprogress.mentorme.entities.Mentor;
import com.livingprogress.mentorme.exceptions.ConfigurationException;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.remote.Constant;
import com.livingprogress.mentorme.remote.entities.Documents;
import com.livingprogress.mentorme.remote.entities.FindSimilarResponse;
import com.livingprogress.mentorme.remote.entities.InterestCategory;
import com.livingprogress.mentorme.remote.entities.JobActions;
import com.livingprogress.mentorme.remote.entities.JobResultResponse;
import com.livingprogress.mentorme.remote.services.HODClient;
import com.livingprogress.mentorme.remote.utils.RemoteHelper;
import com.livingprogress.mentorme.utils.Helper;
import org.apache.commons.validator.UrlValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * The hod client to access havenondemand api.
 */
@Service
public class HODClientImpl implements HODClient {

    /**
     * The api url.
     */
    @Value("${havenondemand.apiUrl}")
    private String apiUrl;

    /**
     * The api key.
     */
    @Value("${havenondemand.apiKey}")
    private String apiKey;

    /**
     * The api version.
     */
    @Value("${havenondemand.version}")
    private String version;

    /**
     * The default max count.
     */
    @Value("${havenondemand.defaultMaxCount}")
    private long defaultMaxCount;

    /**
     * The index name.
     */
    @Value("${havenondemand.indexName}")
    private String indexName;

    /**
     * The flavor.
     */
    @Value("${havenondemand.flavor}")
    private String flavor;

    /**
     * The rest template.
     */
    private final RestTemplate restTemplate;

    /**
     * The default constructor.
     */
    public HODClientImpl() {
        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    }

    /**
     * Check if all required fields are initialized properly.
     *
     * @throws ConfigurationException if any required field is not initialized properly.
     */
    @PostConstruct
    protected void checkConfiguration() {
        Helper.checkConfigState(new UrlValidator().isValid(apiUrl), "apiUrl should be valid url");
        Helper.checkConfigState(!Helper.isNullOrEmpty(apiKey), "apiKey should be not null nor empty");
        Helper.checkConfigState(!Helper.isNullOrEmpty(version), "version should be not null nor empty");
        Helper.checkConfigState(!Helper.isNullOrEmpty(indexName), "indexName should be not null nor empty");
        Helper.checkConfigState(!Helper.isNullOrEmpty(flavor), "flavor");
        Helper.checkConfigPositive(defaultMaxCount, "defaultMaxCount");
    }


    /**
     * List resources.
     *
     * @return the job id result.
     * @throws IllegalStateException throws if any error happens.
     */
    @Override
    public JobResultResponse listResources() {
        Map<String, String> vars = getVars(Constant.LIST_RESOURCES);
        return getForObject(
                "{apiUrl}/api/async/{action}/v1?apikey={apiKey}", JobResultResponse.class, vars);
    }


    /**
     * Create index.
     * @return the job id result.
     * @throws IllegalStateException throws if any error happens.
     */
    @Override
    public JobResultResponse createIndex() {
        MultiValueMap<String, Object> vars = getMultiValueMap();
        vars.add(Constant.INDEX, indexName);
        vars.add(Constant.FLAVOR, flavor);
        // have to use parametric_fields for institutionId too
        vars.put("parametric_fields",
                Arrays.asList("assignedToInstitution", "isVirtualUser",
                        "institutionId",
                        "interestCategories",
                        "parentInterestCategories"));
        return postForEntity("{apiUrl}/api/async/createtextindex/v1", JobResultResponse.class, vars, apiUrl);
    }

    /**
     * Add index.
     *
     * @param index the index name.
     * @param documents the document.
     * @return the job id result.
     * @throws IllegalArgumentException if any argument is invalid.
     * @throws IllegalStateException throws if any error happens.
     */
    @Override
    public JobResultResponse addIndex(String index, Documents documents) {
        Helper.checkNullOrEmpty(index, "index");
        Helper.checkNull(documents, "documents");
        MultiValueMap<String, Object> vars = getMultiValueMap();
        vars.add(Constant.INDEX, indexName);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Documents> xPart = new HttpEntity<>(documents, header);
        vars.add("json", xPart);
        return postForEntity("{apiUrl}/api/async/addtotextindex/v1", JobResultResponse.class, vars, apiUrl);
    }

    /**
     * Get job result.
     *
     * @param jobId the job id.
     * @return the json node with job result.
     * @throws IllegalArgumentException if any argument is invalid.
     * @throws IllegalStateException throws if any error happens.
     */
    @Override
    public JsonNode getJobResult(String jobId) {
        Helper.checkNullOrEmpty(jobId, "jobId");
        Map<String, String> vars = getVars(Constant.JOB_RESULT);
        vars.put("jobId", jobId);
        return getForObject("{apiUrl}/job/status/{jobId}?apikey={apiKey}", JsonNode.class, vars);
    }


    /**
     * Get matching mentees.
     *
     * @param entity the mentor id.
     * @param criteria the remote criteria
     * @return the match mentee ids.
     * @throws IllegalArgumentException if any argument is invalid.
     * @throws MentorMeException throws if any error happens.
     */
    @Override
    public List<Long> getMatchingMentees(Mentor entity, MatchSearchCriteria criteria)
            throws MentorMeException {
        Helper.checkNull(entity, "entity");
        Helper.checkNull(criteria, "criteria");
        return getMatchingUsers(Constant.MENTEE, entity, criteria);
    }

    /**
     * Get matching mentors.
     *
     * @param entity the mentee id.
     * @param criteria the remote criteria
     * @return the match mentor ids.
     * @throws IllegalArgumentException if any argument is invalid.
     * @throws MentorMeException throws if any error happens.
     */
    @Override
    public List<Long> getMatchingMentors(Mentee entity, MatchSearchCriteria criteria)
            throws MentorMeException {
        Helper.checkNull(entity, "entity");
        Helper.checkNull(criteria, "criteria");
        return getMatchingUsers(Constant.MENTOR, entity, criteria);
    }

    /**
     * Get match users.
     *
     * @param category the category.
     * @param entity the entity.
     * @param criteria the remote criteria.
     * @param <T> the entity class.
     * @return the match user ids.
     */
    private <T extends InstitutionUser> List<Long> getMatchingUsers(String category, T entity,
            MatchSearchCriteria criteria) {
        InterestCategory interestCategory = RemoteHelper.getCategories(entity);
        Set<String> orCategories = new HashSet<>(interestCategory.getCategories());
        Set<String> andCategories = new HashSet<>();
        if (criteria.getProfessionalInterests() != null) {
            criteria.getProfessionalInterests()
                    .forEach(p -> andCategories.add(p.getValue()));
        }

        if (criteria.getPersonalInterests() != null) {
            criteria.getPersonalInterests()
                    .forEach(p -> andCategories.add(p.getValue()));
        }
        MultiValueMap<String, Object> vars = getMultiValueMap();
        vars.add("indexes", indexName);
        vars.add("text", orCategories.stream()
                                     .map(x -> "\"" + x + "\"")
                                     .collect(Collectors.joining(" OR ", "(", ")")));
        vars.add("absolute_max_results", Long.toString(criteria.getMaxCount() != null
                ? criteria.getMaxCount() : defaultMaxCount));
        List<String> fields = new ArrayList<>();
        fields.add("MATCH{" + category + "}:content_type");
        if (entity.isAssignedToInstitution()) {
            fields.add("MATCH{" + entity.getInstitution()
                                        .getId() + "}:institutionId");
        } else {
            fields.add("MATCH{false}:assignedToInstitution");
        }
        if (!andCategories.isEmpty()) {
            fields.add("MATCHALL{"
                    + String.join(Constant.COMMA, andCategories)
                    + "}:interestCategories");
        }
        // See DISTSPHERICAL operators in https://dev.havenondemand.com/docs/FieldTextOperators.html
        if (criteria.getDistance() != null && entity.getLongitude() != null && entity.getLatitude() != null) {
            fields.add("(MATCH{true}:isVirtualUser OR  DISTSPHERICAL{"
                    + entity.getLatitude() + "," + entity.getLongitude()
                    + "," + criteria.getDistance() + "}:lat:lon)");
        }

        vars.add("field_text", String.join(" AND ", fields));
        FindSimilarResponse res = postForEntity("{apiUrl}/api/sync/{action}/v1",
                FindSimilarResponse.class, vars, apiUrl, Constant.FIND_SIMILAR);
        return res.getDocuments()
                  .stream()
                  .map(FindSimilarResponse.SimilarDocument::getReference)
                  .map(Long::parseLong)
                  .collect(Collectors.toList());
    }

    /**
     * Submit job.
     *
     * @param jobActions the job actions.
     * @return the job id.
     * @throws IllegalStateException throws if any error happens.
     */
    @Override
    public JobResultResponse submitJob(JobActions jobActions) {
        MultiValueMap<String, Object> vars = getMultiValueMap();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<JobActions> job = new HttpEntity<>(jobActions, headers);
        vars.add("job", job);
        return postForEntity("{apiUrl}/job", JobResultResponse.class, vars, apiUrl);
    }

    /**
     * Get object from havenondemand api.
     *
     * @param url the url.
     * @param responseType the response type.
     * @param vars the url variables.
     * @param <T> the entity class.
     * @return the entity.
     * @throws IllegalStateException throws if any error happens.
     */
    private <T> T getForObject(String url, Class<T> responseType, Map<String, ?> vars) {
        try {
            return restTemplate
                    .getForObject(url, responseType, vars);
        } catch (HttpStatusCodeException e) {
            throw new IllegalStateException(e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * Post object from havenondemand api.
     *
     * @param url the url.
     * @param responseType the response type.
     * @param vars the body variables.
     * @param uriVariables the url variables.
     * @param <T> the entity class.
     * @return the entity.
     * @throws IllegalStateException throws if any error happens.
     */
    private <T> T postForEntity(String url, Class<T> responseType, MultiValueMap<String, Object> vars, Object...
            uriVariables) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(vars, headers);
        try {
            return restTemplate
                    .postForObject(url, request, responseType, uriVariables);
        } catch (HttpStatusCodeException e) {
            throw new IllegalStateException(e.getResponseBodyAsString(), e);
        } catch (RestClientException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * Build map with api url,key, version,action information.
     *
     * @param action the action.
     * @return the url variables.
     */
    private Map<String, String> getVars(String action) {
        Map<String, String> vars = new HashMap<>();
        vars.put("apiUrl", apiUrl);
        vars.put("apiKey", apiKey);
        vars.put("version", version);
        vars.put("action", action);
        return vars;
    }

    /**
     * Build map with api key information.
     *
     * @return the body variables.
     */
    private MultiValueMap<String, Object> getMultiValueMap() {
        MultiValueMap<String, Object> vars = new LinkedMultiValueMap<>();
        vars.add("apiKey", apiKey);
        return vars;
    }
}
