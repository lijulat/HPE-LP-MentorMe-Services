package com.livingprogress.mentorme.remote.consumers;

import com.google.maps.model.GeocodingResult;
import com.livingprogress.mentorme.aop.LogAspect;
import com.livingprogress.mentorme.entities.InstitutionUser;
import com.livingprogress.mentorme.entities.Mentee;
import com.livingprogress.mentorme.entities.MenteeSearchCriteria;
import com.livingprogress.mentorme.entities.Mentor;
import com.livingprogress.mentorme.entities.MentorSearchCriteria;
import com.livingprogress.mentorme.entities.SearchResult;
import com.livingprogress.mentorme.exceptions.ConfigurationException;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.remote.Constant;
import com.livingprogress.mentorme.remote.entities.Document;
import com.livingprogress.mentorme.remote.entities.InterestCategory;
import com.livingprogress.mentorme.remote.services.GeocodingService;
import com.livingprogress.mentorme.remote.utils.RemoteHelper;
import com.livingprogress.mentorme.services.MenteeService;
import com.livingprogress.mentorme.services.MentorService;
import com.livingprogress.mentorme.utils.CustomMessageSource;
import com.livingprogress.mentorme.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.fn.Consumer;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * This is search users event consumer.
 */
@Service
public class SearchUsersConsumer extends BaseConsumer implements Consumer<Event<List<String>>> {

    /**
     * The mentor service.
     */
    @Autowired
    private MentorService mentorService;

    /**
     * The mentee service.
     */
    @Autowired
    private MenteeService menteeService;

    /**
     * The geocoding service.
     */
    @Autowired
    private GeocodingService geocodingService;

    /**
     * The last hours to check updates of user.
     */
    @Value("${havenondemand.lastHours}")
    private int lastHours;

    /**
     * The entity manager.
     */
    @Autowired
    private EntityManager entityManager;

    /**
     * Check if all required fields are initialized properly.
     *
     * @throws ConfigurationException if any required field is not initialized properly.
     */
    @PostConstruct
    protected void checkConfiguration() {
        super.checkConfiguration();
        Helper.checkConfigNotNull(menteeService, "menteeService");
        Helper.checkConfigNotNull(mentorService, "mentorService");
        Helper.checkConfigNotNull(geocodingService, "geocodingService");
        Helper.checkPositive(lastHours, "lastHours");
        Helper.checkNull(entityManager, "entityManager");
    }

    /**
     * Handle search user event.
     *
     * @param ev the event.
     */
    @Transactional
    @Override
    public void accept(Event<List<String>> ev) {
        // new created index names.
        List<String> newIndexes = ev.getData();
        if (!newIndexes.isEmpty()) {
            Helper.logDebugMessage(LogAspect.LOGGER, CustomMessageSource.getMessage("jobResult.newIndex",
                    String.join(Constant.COMMA, newIndexes)));
        }
        MentorSearchCriteria mentorSearchCriteria = new MentorSearchCriteria();
        MenteeSearchCriteria menteeSearchCriteria = new MenteeSearchCriteria();
        // if new indexes exist will search all mentees/mentors in database
        // if no new indexes exists will search for updates of user in last 24 hours by default
        if (newIndexes.isEmpty()) {
            Date now = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.HOUR_OF_DAY, -lastHours);
            Date min = calendar.getTime();
            mentorSearchCriteria.setMaxLastModifiedOn(now);
            mentorSearchCriteria.setMinLastModifiedOn(min);
            menteeSearchCriteria.setMaxLastModifiedOn(now);
            menteeSearchCriteria.setMinLastModifiedOn(min);
        }
        try {
            SearchResult<Mentor> result = mentorService.search(mentorSearchCriteria, null);
            Map<String, GeocodingResult> cache = new HashMap<>();
            List<Document> documents = buildDocuments(Constant.MENTOR, result.getEntities(), cache);
            SearchResult<Mentee> mentees = menteeService.search(menteeSearchCriteria, null);
            documents.addAll(buildDocuments(Constant.MENTEE, mentees.getEntities(), cache));
            if (documents.isEmpty()) {
                // no need to index
                getLatch().countDown();
            } else {
                getEventBus().notify(Constant.ADD_TO_TEXT_INDEX, Event.wrap(documents));
            }
        } catch (MentorMeException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * Build documents for index.
     *
     * @param type the mentee or mentor type.
     * @param entities the entity list.
     * @param cache the google address cache.
     * @param <T> the entity class
     * @return the document list.
     * @throws MentorMeException throws if error to geocode or update user.
     */
    private <T extends InstitutionUser> List<Document> buildDocuments(String type,
            List<T> entities, Map<String, GeocodingResult> cache) throws MentorMeException {
        List<Document> documents = new ArrayList<>();
        for (T entity : entities) {
            Document document = new Document();
            // cache address/result to avoid to send same requests
            String address = RemoteHelper.getAddress(entity);
            GeocodingResult geocodingResult = null;
            if (address != null) {
                geocodingResult = cache.containsKey(address) ? cache.get(address)
                        : geocodingService.geocode(address);
            }
            BigDecimal lat = null;
            BigDecimal lng  = null;
            if (geocodingResult != null) {
                cache.putIfAbsent(address, geocodingResult);
                lat = new BigDecimal(Double.toString(geocodingResult.geometry.location.lat));
                lng = new BigDecimal(Double.toString(geocodingResult.geometry.location.lng));
            }
            if (Helper.isUpdated(entity.getLatitude(), lat) || Helper.isUpdated(entity.getLongitude(), lng)) {
                entity.setLatitude(lat);
                entity.setLongitude(lng);
                entityManager.merge(entity);
            }

            if (entity.getLatitude() != null && entity.getLongitude() != null) {
                document.setLon(entity.getLongitude());
                document.setLat(entity.getLatitude());
            }
            InterestCategory interestCategory = RemoteHelper.getCategories(entity);
            document.setContent(String.join(Constant.COMMA, interestCategory.getCategories()));
            document.setReference(Long.toString(entity.getId()));
            if (entity.getInstitution() != null) {
                document.setInstitutionId(Long.toString(entity.getInstitution()
                                                .getId()));
            }
            document.setAssignedToInstitution(Boolean.toString(entity.isAssignedToInstitution()));
            document.setContentType(type);
            document.setInterestCategories(
                    new HashSet<>(interestCategory.getInterestCategories()));
            document.setParentInterestCategories(
                    new HashSet<>(interestCategory.getParentInterestCategories()));
            document.setIsVirtualUser(Boolean.toString(entity.isVirtualUser()));
            documents.add(document);
        }
        return documents;
    }
}

