package com.livingprogress.mentorme.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.livingprogress.mentorme.aop.LogAspect;
import com.livingprogress.mentorme.entities.*;
import com.livingprogress.mentorme.exceptions.ConfigurationException;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.security.CustomUserDetails;
import com.livingprogress.mentorme.security.UserAuthentication;
import com.livingprogress.mentorme.services.GenericService;
import com.livingprogress.mentorme.services.springdata.ActivityRepository;
import com.livingprogress.mentorme.services.springdata.MenteeMentorProgramRepository;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * This class provides help methods used in this application.
 */
public class Helper {
    /**
     * Mysql ST_Distance_Sphere will return distance in meters so we have to multiply 1000 to compare as kilometers.
     */
    public static final String KILOMETERS = "1000";

    /**
     * Represents the mysql function to calculate distance between two points.
     */
    private static final String CALCULATE_DISTANCE = "calculate_distance";

    /**
     * Represents the classes that there is no need to log.
     */
    private static final List<Class> NOLOGS = Arrays.asList(HttpServletRequest.class,
            HttpServletResponse.class, ModelAndView.class, NewPassword.class, MultipartFile[].class, ResponseEntity.class);

    /**
     * The object mapper.
     */
    public static final ObjectMapper MAPPER = new Jackson2ObjectMapperBuilder()
            .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();

    /**
     * Represents the utf8 encoding name.
     */
    public static final String UTF8 = "UTF-8";

    /**
     * The private constructor.
     */
    private Helper() { }

    /**
     * It checks whether a given object is null.
     *
     * @param object the object to be checked
     * @param name the name of the object, used in the exception message
     * @throws IllegalArgumentException the exception thrown when the object is null
     */
    public static void checkNull(Object object, String name) throws IllegalArgumentException {
        if (object == null) {
            throw new IllegalArgumentException(CustomMessageSource.getMessage("checkNull.error", name));
        }
    }

    /**
     * It checks whether a given identifiable entity is valid.
     *
     * @param object the object to be checked
     * @param name the name of the object, used in the exception message
     * @param <T> the entity class
     * @throws IllegalArgumentException the exception thrown when the object is null or id of object is not positive
     */
    public static <T extends IdentifiableEntity> void checkEntity(T object, String name)
            throws IllegalArgumentException {
        checkNull(object, name);
        checkPositive(object.getId(), name + ".id");
    }

    /**
     * It checks whether a given string is valid email address.
     *
     * @param str the string to be checked
     * @return true if a given string is valid email address
     */
    public static boolean isEmail(String str) {
        return new EmailValidator().isValid(str, null);
    }

    /**
     * It checks whether a given string is null or empty.
     *
     * @param str the string to be checked
     * @return true if a given string is null or empty
     * @throws IllegalArgumentException throws if string is null or empty
     */
    public static boolean isNullOrEmpty(String str) throws IllegalArgumentException {
        return str == null || str.trim().isEmpty();
    }

    /**
     * It checks whether a given string is null or empty.
     *
     * @param str the string to be checked
     * @param name the name of the string, used in the exception message
     * @throws IllegalArgumentException the exception thrown when the given string is null or empty
     */
    public static void checkNullOrEmpty(String str, String name) throws IllegalArgumentException {
        if (isNullOrEmpty(str)) {
            throw new IllegalArgumentException(
                    CustomMessageSource.getMessage("checkNullOrEmpty.error", name));
        }
    }

    /**
     * Check if the value is positive.
     *
     * @param value the value to be checked
     * @param name the name of the value, used in the exception message
     * @throws IllegalArgumentException if the value is not positive
     */
    public static void checkPositive(long value, String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(CustomMessageSource.getMessage("checkPositive.error", name));
        }
    }

    /**
     * Check if the value is valid email.
     *
     * @param value the value to be checked
     * @param name the name of the value, used in the exception message
     * @throws IllegalArgumentException if the value is not email
     */
    public static void checkEmail(String value, String name) {
        checkNullOrEmpty(value, name);
        if (!isEmail(value)) {
            throw new IllegalArgumentException(CustomMessageSource.getMessage("checkEmail.error", name));
        }
    }

    /**
     * Check if the configuration state is true.
     *
     * @param state the state
     * @param message the error message if the state is not true
     * @throws ConfigurationException if the state is not true
     */
    public static void checkConfigState(boolean state, String message) {
        if (!state) {
            throw new ConfigurationException(message);
        }
    }

    /**
     * Check if the configuration is null or not.
     *
     * @param object the object
     * @param name the name
     * @throws ConfigurationException if the configuration is null
     */
    public static void checkConfigNotNull(Object object, String name) {
        if (object == null) {
            throw new ConfigurationException(CustomMessageSource.getMessage("checkNull.error", name));
        }
    }

    /**
     * Check if the directory configuration is valid.
     *
     * @param path the path
     * @param name the name
     * @throws ConfigurationException if the configuration is null or empty or valid directory not exist.
     */
    public static void checkDirectory(String path, String name) {
        if (Helper.isNullOrEmpty(path)) {
            throw new ConfigurationException(CustomMessageSource.getMessage("checkNullOrEmpty.error", name));
        }
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            throw new ConfigurationException(CustomMessageSource.getMessage("checkDirectory.error", name));
        }
    }

    /**
     * Check if the configuration is positive or not.
     *
     * @param value the configuration  value
     * @param name the name
     * @throws ConfigurationException if the configuration value is  not positive
     */
    public static void checkConfigPositive(long value, String name) {
        if (value <= 0) {
            throw new ConfigurationException(CustomMessageSource.getMessage("checkPositive.error", name));
        }
    }

    /**
     * Logs message with <code>DEBUG</code> level.
     *
     * @param logger the logger.
     * @param message the log message.
     */
    public static void logDebugMessage(Logger logger, String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
    }

    /**
     * Logs for entrance into public methods at <code>DEBUG</code> level.
     *
     * @param logger the logger.
     * @param signature the signature.
     * @param paramNames the names of parameters to log (not Null).
     * @param params the values of parameters to log (not Null).
     */
    public static void logEntrance(Logger logger, String signature, String[] paramNames, Object[] params) {
        if (logger.isDebugEnabled()) {
            String msg = CustomMessageSource.getMessage("log.entering", signature) + toString(paramNames, params);
            logger.debug(msg);
        }
    }

    /**
     * Logs for exit from public methods at <code>DEBUG</code> level.
     *
     * @param logger the logger.
     * @param signature the signature of the method to be logged.
     * @param value the return value to log.
     */
    public static void logExit(Logger logger, String signature, Object value) {
        if (logger.isDebugEnabled()) {
            String msg = CustomMessageSource.getMessage("log.exiting", signature);
            if (value != null) {
                msg += CustomMessageSource.getMessage("log.output") + toString(value);
            }
            logger.debug(msg);
        }
    }

    /**
     * Logs the given exception and message at <code>ERROR</code> level.
     *
     * @param <T> the exception type.
     * @param logger the logger.
     * @param signature the signature of the method to log.
     * @param ex the exception to log.
     */
    public static <T extends Throwable> void logException(Logger logger, String signature, T ex) {
        StringBuilder sw = new StringBuilder();
        sw.append(CustomMessageSource.getMessage("log.error", signature)).append(": ").append(ex.getMessage());
        logger.error(sw.toString(), ex);
    }

    /**
     * Converts the parameters to string.
     *
     * @param paramNames the names of parameters.
     * @param params the values of parameters.
     * @return the string
     */
    private static String toString(String[] paramNames, Object[] params) {
        StringBuilder sb = new StringBuilder(CustomMessageSource.getMessage("log.input"));
        sb.append("{");
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(paramNames[i]).append(":").append(toString(params[i]));
            }
        }
        sb.append("}.");
        return sb.toString();
    }

    /**
     * Converts the object to string.
     *
     * @param obj the object
     * @return the string representation of the object.
     */
    public static String toString(Object obj) {
        String result;
        try {
            if (NOLOGS.stream().anyMatch(s -> s.isInstance(obj))) {
                result = obj.getClass().getSimpleName();
            } else {
                result = MAPPER.writeValueAsString(obj);
            }
        } catch (JsonProcessingException e) {
            Helper.logException(LogAspect.LOGGER, "com.livingprogress.mentorme.utils"
                    + ".Helper#toString", e);
            
            result = CustomMessageSource.getMessage("json.error", e.getMessage());
        }
        return result;
    }

    /**
     * Get password encoder.
     *
     * @return the BC crypt password encoder
     */
    public static PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Encode password for user.
     * @param user the user entity.
     * @param isUpdating the updating flag.
     * @return the user with encrypted password field.
     */
    public static User encodePassword(User user, boolean isUpdating) {
        Helper.checkNull(user, "user");
        String rawPassword = user.getPassword();
        boolean checkPassword = !isUpdating || rawPassword != null;
        if (checkPassword) {
            Helper.checkNullOrEmpty(rawPassword, "user.password");
            System.out.println("The raw password is: " + rawPassword);
            PasswordEncoder encoder = getPasswordEncoder();
            user.setPassword(encoder.encode(rawPassword));
            System.out.println("The encoded password is: " + user.getPassword());
        }
        return user;
    }


    /**
     * Build predicate to match ids in identifiable entity list.
     *
     * @param val the list value
     * @param pd the predicate
     * @param path the path
     * @param cb the criteria builder.
     * @param <T> the identifiable entity
     * @return the match predicate
     */
    public static <T extends IdentifiableEntity> Predicate
    buildInPredicate(List<T> val, Predicate pd, Path<?> path, CriteriaBuilder cb) {
        if (val != null && !val.isEmpty()) {
            List<Long> ids = val.stream().map(IdentifiableEntity::getId).collect(Collectors.toList());
            return cb.and(pd, path.in(ids));
        }
        
        return pd;
    }

    /**
     * Build >= predicate.
     *
     * @param val the value
     * @param pd the predicate
     * @param path the path
     * @param cb the criteria builder.
     * @param <Y> the comparable entity
     * @return the match predicate
     */
    public static <Y extends Comparable<? super Y>> Predicate
    buildGreaterThanOrEqualToPredicate(Y val, Predicate pd, Path<? extends Y> path, CriteriaBuilder cb) {
        if (val != null) {
            return cb.and(pd, cb.greaterThanOrEqualTo(path, val));
        }
        return pd;
    }

    /**
     * Build <= predicate.
     *
     * @param val the value
     * @param pd the predicate
     * @param path the path
     * @param cb the criteria builder.
     * @param <Y> the comparable entity
     * @return the match predicate
     */
    public static <Y extends Comparable<? super Y>> Predicate
    buildLessThanOrEqualToPredicate(Y val, Predicate pd, Path<? extends Y> path, CriteriaBuilder cb) {
        if (val != null) {
            return cb.and(pd, cb.lessThanOrEqualTo(path, val));
        }
        return pd;
    }

    /**
     * Build equal predicate for object value.
     *
     * @param val the value
     * @param pd the predicate
     * @param path the path
     * @param cb the criteria builder.
     * @return the match predicate
     */
    public static Predicate buildEqualPredicate(Object val, Predicate pd, Path<?> path, CriteriaBuilder cb) {
        if (val != null) {
            return cb.and(pd, cb.equal(path, val));
        }
        return pd;
    }

    /**
     * Build equal predicate for string value.
     *
     * @param val the value
     * @param pd the predicate
     * @param path the path
     * @param cb the criteria builder.
     * @return the match predicate
     */
    public static Predicate buildEqualPredicate(String val, Predicate pd, Path<?> path, CriteriaBuilder cb) {
        if (!isNullOrEmpty(val)) {
            return cb.and(pd, cb.equal(path, val));
        }
        return pd;
    }

    /**
     * Build like predicate for string value.
     *
     * @param val the value
     * @param pd the predicate
     * @param path the path
     * @param cb the criteria builder.
     * @return the match predicate
     */
    public static Predicate buildLikePredicate(String val, Predicate pd, Path<String> path, CriteriaBuilder cb) {
        if (!isNullOrEmpty(val)) {
            return cb.and(pd, buildLike(val, path, cb));
        }
        return pd;
    }

    /**
     * Build like predicate for string value.
     *
     * @param val the value
     * @param path the path
     * @param cb the criteria builder.
     * @return the match predicate
     */
    public static Predicate buildLike(String val, Path<String> path, CriteriaBuilder cb) {
        return cb.like(path, "%" + val + "%");
    }

    /**
     * Build name predicate..
     *
     * @param name the name
     * @param pd the predicate
     * @param root the root
     * @param cb the criteria builder.
     * @return the match predicate
     */
    public static Predicate buildNamePredicate(String name, Predicate pd, Root<?> root, CriteriaBuilder cb) {
        if (!isNullOrEmpty(name)) {
            return cb.and(pd, cb.or(Helper.buildLike(name,
                    root.get("firstName"), cb), Helper.buildLike(name, root.get("lastName"), cb)));
        }
        return pd;
    }

    /**
     * Build predicate for institution user.
     *
     * @param criteria the institution user criteria
     * @param pd the predicate
     * @param root the root
     * @param cb the criteria builder.
     * @return the match predicate
     */
    public static Predicate
    buildPredicate(InstitutionUserSearchCriteria criteria, Predicate pd, Root<?> root, CriteriaBuilder cb) {
        Predicate resultPD = Helper.buildEqualPredicate(criteria.getInstitutionId(), pd, root.get("institution").get("id"), cb);
        resultPD = Helper.buildEqualPredicate(criteria.getStatus(), resultPD, root.get("status"), cb);
        resultPD = Helper.buildGreaterThanOrEqualToPredicate(criteria.getMinAveragePerformanceScore(),
                resultPD, root.get("averagePerformanceScore"), cb);
        resultPD = Helper.buildLessThanOrEqualToPredicate(criteria.getMaxAveragePerformanceScore(),
                resultPD, root.get("averagePerformanceScore"), cb);
        resultPD = Helper.buildNamePredicate(criteria.getName(), resultPD, root, cb);
        resultPD = Helper.buildInPredicate(criteria.getPersonalInterests(), resultPD,
                root.join("personalInterests", JoinType.LEFT).get("personalInterest").get("id"), cb);
        resultPD = Helper.buildInPredicate(criteria.getProfessionalInterests(), resultPD,
                root.join("professionalInterests", JoinType.LEFT).get("professionalInterest").get("id"), cb);
        resultPD = Helper.buildEqualPredicate(criteria.getAssignedToInstitution(), resultPD,
                root.get("assignedToInstitution"), cb);
        resultPD = Helper.buildGreaterThanOrEqualToPredicate(criteria.getMinLastModifiedOn(),
                resultPD, root.get("lastModifiedOn"), cb);
        resultPD = Helper.buildLessThanOrEqualToPredicate(criteria.getMaxLastModifiedOn(),
                resultPD, root.get("lastModifiedOn"), cb);
        if (criteria.getIds() != null && !criteria.getIds().isEmpty()) {
            resultPD = cb.and(resultPD, root.get("id")
                                .in(criteria.getIds()));
        }
        if (criteria.getDistance() != null
                && criteria.getLatitude() != null
                && criteria.getLongitude() != null) {
            resultPD = cb.and(resultPD, cb.or(cb.equal(root.get("isVirtualUser"), true),
                    cb.lessThanOrEqualTo(cb.function(CALCULATE_DISTANCE,
                                    BigDecimal.class,
                                    root.get("longitude"),
                                    root.get("latitude"),
                                    cb.literal(criteria.getLongitude()),
                                    cb.literal(criteria.getLatitude())),
                            criteria.getDistance()
                                    .multiply(new BigDecimal(KILOMETERS)))));
        }
        return resultPD;
    }

    /**
     * Get id of entity.
     *
     * @param entity the entity.
     * @param <T> the entity class
     * @return if of entity if exists otherwise null.
     */
    public static <T extends IdentifiableEntity> Long getId(T entity) {
        Long id = null;
        if (entity != null) {
            id = entity.getId();
        }
        return id;
    }

    /**
     * Check whether value has been updated.
     *
     * @param oldValue the old value
     * @param newValue the new value.
     * @return true if value has been updated.
     */
    public static boolean isUpdated(Object oldValue, Object newValue) {
        return (oldValue != null && !oldValue.equals(newValue)) || (newValue != null && !newValue.equals(oldValue));
    }

    /**
     * Check whether BigDecimal value has been updated.
     *
     * @param oldValue the old value
     * @param newValue the new value.
     * @return true if value has been updated.
     */
    public static boolean isUpdated(BigDecimal oldValue, BigDecimal newValue) {
        return (oldValue != null && (newValue == null || oldValue.compareTo(newValue) != 0))
                || (newValue != null && (oldValue == null || newValue.compareTo(oldValue) != 0));
    }

    /**
     * Check whether both values is null.
     *
     * @param oldValue the old value
     * @param newValue the new value.
     * @return true if both values is null
     */
    public static boolean isBothNull(Object oldValue, Object newValue) {
        return oldValue == null && newValue == null;
    }

    /**
     * Check whether identifiable entity list has been updated.
     *
     * @param oldValues the old values
     * @param newValues the new values.
     * @param <T> the entity class
     * @return true if value has been updated.
     */
    public static <T extends IdentifiableEntity> boolean isUpdated(List<T> oldValues, List<T> newValues) {
        List<Long> oldIds = oldValues == null ? Collections.emptyList()
                : oldValues.stream().map(IdentifiableEntity::getId).collect(Collectors.toList());
        return newValues == null && !oldIds.isEmpty()
                || newValues != null
                && (oldIds.size() != newValues.size() || newValues.stream().anyMatch(a -> !oldIds.contains(a.getId())));
    }

    /**
     * Check whether weighted personal interest entity list has been updated.
     *
     * @param oldValues the old values
     * @param newValues the new values.
     * @param <T> the entity class
     * @return true if value has been updated.
     */
    public static <T extends WeightedPersonalInterest> boolean
    isUpdatedWeightedPersonalInterests(List<T> oldValues, List<T> newValues) {
        return isUpdated(oldValues, newValues) || oldValues.stream().anyMatch(w -> {
            T match = newValues.stream().filter(n -> n.getId() == w.getId()).findFirst().get();
            return isUpdated(w.getWeight(), match.getWeight())
                    || isUpdated(w.getPersonalInterest().getId(), match.getPersonalInterest().getId());
        });
    }

    /**
     * Check whether weighted professional interest entity list has been updated.
     *
     * @param oldValues the old values
     * @param newValues the new values.
     * @param <T> the entity class
     * @return true if value has been updated.
     */
    public static <T extends WeightedProfessionalInterest> boolean
    isUpdatedWeightedProfessionalInterests(List<T> oldValues, List<T> newValues) {
        return isUpdated(oldValues, newValues) || oldValues.stream().anyMatch(w -> {
            T match = newValues.stream().filter(n -> n.getId() == w.getId()).findFirst().get();
            return isUpdated(w.getWeight(), match.getWeight())
                    || isUpdated(w.getProfessionalInterest().getId(), match.getProfessionalInterest().getId());
        });
    }

    public static <T extends MenteeSkill> boolean
    isUpdatedSkills(List<T> oldValues, List<T> newValues) {
        return isUpdated(oldValues, newValues) || oldValues.stream().anyMatch(w -> {
            T match = newValues.stream().filter(n -> n.getId() == w.getId()).findFirst().get();
            return isUpdated(w.getSkill().getId(), match.getSkill().getId());
        });
    }

    /**
     * Check whether user entity has been updated.
     *
     * @param oldEntity the old entity
     * @param newEntity the new entity.
     * @param <T> the entity class
     * @return true if value has been updated.
     */
    public static <T extends User> boolean isUpdated(T oldEntity, T newEntity) {
        if (isBothNull(oldEntity, newEntity)) {
            return false;
        }
        boolean updated = false;
        if (newEntity.getPassword() != null) {
            updated = true;
            User encodedUser = Helper.encodePassword(newEntity, true);
            oldEntity.setPassword(encodedUser.getPassword());
        }
        if (isUpdated(oldEntity.getFirstName(), newEntity.getFirstName())) {
            updated = true;
            oldEntity.setFirstName(newEntity.getFirstName());
        }
        if (isUpdated(oldEntity.getLastName(), newEntity.getLastName())) {
            updated = true;
            oldEntity.setLastName(newEntity.getLastName());
        }
        if (isUpdated(oldEntity.getRoles(), newEntity.getRoles())) {
            updated = true;
            oldEntity.setRoles(newEntity.getRoles());
        }

        if (isUpdated(oldEntity.getEmail(), newEntity.getEmail())) {
            updated = true;
            oldEntity.setEmail(newEntity.getEmail());
        }
        if (isUpdated(oldEntity.getProfilePicturePath(), newEntity.getProfilePicturePath())) {
            updated = true;
            oldEntity.setProfilePicturePath(newEntity.getProfilePicturePath());
        }
        if (isUpdated(oldEntity.getStatus(), newEntity.getStatus())) {
            updated = true;
            oldEntity.setStatus(newEntity.getStatus());
        }
        if (isUpdated(oldEntity.isVirtualUser(), newEntity.isVirtualUser())) {
            updated = true;
            oldEntity.setVirtualUser(newEntity.isVirtualUser());
        }
        if (isUpdated(oldEntity.isAgreedAgreement(), newEntity.isAgreedAgreement())) {
            updated = true;
            oldEntity.setAgreedAgreement(newEntity.isAgreedAgreement());
        }
        if (isUpdated(oldEntity.getStreetAddress(), newEntity.getStreetAddress())) {
            updated = true;
            oldEntity.setStreetAddress(newEntity.getStreetAddress());
        }
        if (isUpdated(oldEntity.getCity(), newEntity.getCity())) {
            updated = true;
            oldEntity.setCity(newEntity.getCity());
        }
        if (isUpdated(oldEntity.getState(), newEntity.getState())) {
            updated = true;
            oldEntity.setState(newEntity.getState());
        }
        if (isUpdated(oldEntity.getCountry(), newEntity.getCountry())) {
            updated = true;
            oldEntity.setCountry(newEntity.getCountry());
        }
        if (isUpdated(oldEntity.getPostalCode(), newEntity.getPostalCode())) {
            updated = true;
            oldEntity.setPostalCode(newEntity.getPostalCode());
        }
        if (isUpdated(oldEntity.getLatitude(), newEntity.getLatitude())) {
            updated = true;
            oldEntity.setLatitude(newEntity.getLatitude());
        }
        if (isUpdated(oldEntity.getLongitude(), newEntity.getLongitude())) {
            updated = true;
            oldEntity.setLongitude(newEntity.getLongitude());
        }
        return updated;
    }


    /**
     * Check whether institution user entity has been updated.
     *
     * @param oldEntity the old entity
     * @param newEntity the new entity.
     * @param <T> the entity class
     * @return true if value has been updated.
     */
    public static <T extends InstitutionUser> boolean isUpdated(T oldEntity, T newEntity) {
        if (isBothNull(oldEntity, newEntity)) {
            return false;
        }
        boolean updated = isUpdated((User) oldEntity, (User) newEntity);
        if (isUpdated(oldEntity.getInstitution(), newEntity.getInstitution())) {
            updated = true;
            oldEntity.setInstitution(newEntity.getInstitution());
        }
        if (isUpdated(oldEntity.isAssignedToInstitution(), newEntity.isAssignedToInstitution())) {
            updated = true;
            oldEntity.setAssignedToInstitution(newEntity.isAssignedToInstitution());
        }
        if (isUpdated(oldEntity.getBirthDate(), newEntity.getBirthDate())) {
            updated = true;
            oldEntity.setBirthDate(newEntity.getBirthDate());
        }
        if (isUpdated(oldEntity.getPhone(), newEntity.getPhone())) {
            updated = true;
            oldEntity.setPhone(newEntity.getPhone());
        }
        if (isUpdated(oldEntity.getSkypeUsername(), newEntity.getSkypeUsername())) {
            updated = true;
            oldEntity.setSkypeUsername(newEntity.getSkypeUsername());
        }

        if (isUpdated(oldEntity.getIntroVideoLink(), newEntity.getIntroVideoLink())) {
            updated = true;
            oldEntity.setIntroVideoLink(newEntity.getIntroVideoLink());
        }
        if (isUpdated(oldEntity.getDescription(), newEntity.getDescription())) {
            updated = true;
            oldEntity.setDescription(newEntity.getDescription());
        }
        if (isUpdatedWeightedPersonalInterests(oldEntity.getPersonalInterests(), newEntity.getPersonalInterests())) {
            updated = true;
            oldEntity.getPersonalInterests().clear();
            if (newEntity.getPersonalInterests() != null) {
                oldEntity.getPersonalInterests().addAll(newEntity.getPersonalInterests());
                oldEntity.getPersonalInterests().forEach(c -> c.setUser(oldEntity));
            }
        }
        if (isUpdatedWeightedProfessionalInterests(oldEntity.getProfessionalInterests(),
                newEntity.getProfessionalInterests())) {
            updated = true;
            oldEntity.getProfessionalInterests().clear();
            if (newEntity.getProfessionalInterests() != null) {
                oldEntity.getProfessionalInterests().addAll(newEntity.getProfessionalInterests());
                oldEntity.getProfessionalInterests().forEach(c -> c.setUser(oldEntity));
            }
        }
        if (isUpdated(oldEntity.getAveragePerformanceScore(), newEntity.getAveragePerformanceScore())) {
            updated = true;
            oldEntity.setAveragePerformanceScore(newEntity.getAveragePerformanceScore());
        }
        return updated;
    }

    /**
     * Check whether professional experience entity has been updated.
     *
     * @param oldValues the old values
     * @param newValues the new values.
     * @param <T> the entity class
     * @return true if value has been updated.
     */
    public static <T extends ProfessionalExperienceData> boolean
    isUpdatedProfessionalExperienceDatas(List<T> oldValues, List<T> newValues) {
        return isUpdated(oldValues, newValues) || oldValues.stream().anyMatch(w -> {
            T match = newValues.stream().filter(n -> n.getId() == w.getId()).findFirst().get();
            return isUpdated(w.getPosition(), match.getPosition())
                    || isUpdated(w.getWorkLocation(), match.getWorkLocation())
                    || isUpdated(w.getStartDate(), match.getStartDate())
                    || isUpdated(w.getEndDate(), match.getEndDate())
                    || isUpdated(w.getDescription(), match.getDescription());
        });
    }

    /**
     * Check whether mentor entity has been updated.
     *
     * @param oldEntity the old entity
     * @param newEntity the new entity.
     * @param <T> the entity class
     * @return true if value has been updated.
     */
    public static <T extends Mentor> boolean isUpdated(T oldEntity, T newEntity) {
        if (isBothNull(oldEntity, newEntity)) {
            return false;
        }
        boolean updated = isUpdated((InstitutionUser) oldEntity, (InstitutionUser) newEntity);
        if (isUpdatedProfessionalExperienceDatas(oldEntity.getProfessionalExperiences(),
                newEntity.getProfessionalExperiences())) {
            updated = true;
            oldEntity.getProfessionalExperiences().clear();
            if (newEntity.getProfessionalExperiences() != null) {
                oldEntity.getProfessionalExperiences().addAll(newEntity.getProfessionalExperiences());
                oldEntity.getProfessionalExperiences().forEach(c -> c.setMentor(oldEntity));
            }
        }
        if (isUpdated(oldEntity.getProfessionalAreas(), newEntity.getProfessionalAreas())) {
            updated = true;
            oldEntity.setProfessionalAreas(newEntity.getProfessionalAreas());
        }
        if (isUpdated(oldEntity.getMentorType(), newEntity.getMentorType())) {
            updated = true;
            oldEntity.setMentorType(newEntity.getMentorType());
        }
        if (isUpdated(oldEntity.getCompanyName(), newEntity.getCompanyName())) {
            updated = true;
            oldEntity.setCompanyName(newEntity.getCompanyName());
        }
        if (isUpdated(oldEntity.getLinkedInUrl(), newEntity.getLinkedInUrl())) {
            updated = true;
            oldEntity.setLinkedInUrl(newEntity.getLinkedInUrl());
        }
        return updated;
    }

    /**
     * Check whether parent consent entity has been updated.
     *
     * @param oldEntity the old entity
     * @param newEntity the new entity.
     * @return true if value has been updated.
     */
    public static boolean isUpdatedParentConsent(ParentConsent oldEntity, ParentConsent newEntity) {
        if (isBothNull(oldEntity, newEntity)) {
            return false;
        }
        return isUpdated(oldEntity, newEntity)
                || isUpdated(oldEntity.getParentName(), newEntity.getParentEmail())
                || isUpdated(oldEntity.getSignatureFilePath(), oldEntity.getSignatureFilePath())
                || isUpdated(oldEntity.getParentEmail(), oldEntity.getParentEmail())
                || isUpdated(oldEntity.getToken(), oldEntity.getToken());

    }

    /**
     * Check whether institution affiliation code entity has been updated.
     *
     * @param oldEntity the old entity
     * @param newEntity the new entity.
     * @return true if value has been updated.
     */
    public static boolean
    isUpdatedInstitutionAffiliationCode(InstitutionAffiliationCode oldEntity, InstitutionAffiliationCode newEntity) {
        if (isBothNull(oldEntity, newEntity)) {
            return false;
        }
        return isUpdated(oldEntity, newEntity)
                || isUpdated(oldEntity.getCode(), newEntity.getCode())
                || isUpdated(oldEntity.isUsed(), oldEntity.isUsed());

    }

    /**
     * Check whether mentee entity has been updated.
     *
     * @param oldEntity the old entity
     * @param newEntity the new entity.
     * @param <T> the entity class
     * @return true if value has been updated.
     */
    public static <T extends Mentee> boolean isUpdated(T oldEntity, T newEntity) {
        if (isBothNull(oldEntity, newEntity)) {
            return false;
        }
        boolean updated = isUpdated((InstitutionUser) oldEntity, (InstitutionUser) newEntity);
        if (isUpdated(oldEntity.getFamilyIncome(), newEntity.getFamilyIncome())) {
            updated = true;
            oldEntity.setFamilyIncome(newEntity.getFamilyIncome());
        }
        if (isUpdated(oldEntity.getSchool(), newEntity.getSchool())) {
            updated = true;
            oldEntity.setSchool(newEntity.getSchool());
        }
        if (isUpdatedSkills(oldEntity.getSkills(), newEntity.getSkills())) {
            updated = true;
            oldEntity.getSkills().clear();
            if (newEntity.getSkills() != null) {
                oldEntity.getSkills().addAll(newEntity.getSkills());
                oldEntity.getSkills().forEach(c -> c.setUser(oldEntity));
            }
        }
        if (isUpdatedInstitutionAffiliationCode(oldEntity.getInstitutionAffiliationCode(),
                newEntity.getInstitutionAffiliationCode())) {
            updated = true;
            oldEntity.setInstitutionAffiliationCode(newEntity.getInstitutionAffiliationCode());
        }

        if (isUpdatedParentConsent(oldEntity.getParentConsent(), newEntity.getParentConsent())) {
            updated = true;
            oldEntity.setParentConsent(newEntity.getParentConsent());
        }
        if (isUpdated(oldEntity.getFacebookUrl(), newEntity.getFacebookUrl())) {
            updated = true;
            oldEntity.setFacebookUrl(newEntity.getFacebookUrl());
        }
        return updated;
    }

    /**
     * Get parent category from weighted personal interest.
     *
     * @param entity the weighted personal interest entity.
     * @return parent category
     */
    public static PersonalInterest getParentCategoryFromWeightedPersonalInterest(WeightedPersonalInterest entity) {
        if (entity != null && entity.getPersonalInterest() != null
                && entity.getPersonalInterest().getParentCategory() != null) {
            return entity.getPersonalInterest().getParentCategory();
        }
        return null;
    }

    /**
     * Get parent category from weighted professional interest.
     *
     * @param entity the weighted professional interest entity.
     * @return parent category
     */
    public static ProfessionalInterest
    getParentCategoryFromWeightedProfessionalInterest(WeightedProfessionalInterest entity) {
        if (entity != null && entity.getProfessionalInterest() != null
                && entity.getProfessionalInterest().getParentCategory() != null) {
            return entity.getProfessionalInterest().getParentCategory();
        }
        return null;
    }

    /**
     * Get match score.
     *
     * @param directMatchingPoints the direct matching points.
     * @param parentCategoryMatchingPoints the parent category matching points
     * @param interests1 the interest list1.
     * @param interests2 the interest list2.
     * @param weightExtractor the weight extractor
     * @param interestExtractor the interest extractor
     * @param parentCategoryExtractor the parent category extractor
     * @param <T> the identifiable entity
     * @return the matching score.
     */
    public static <T extends IdentifiableEntity> int getScore(int directMatchingPoints,
            int parentCategoryMatchingPoints, List<T> interests1, List<T> interests2,
            Function<? super T, Integer> weightExtractor,
            Function<? super T, IdentifiableEntity> interestExtractor,
            Function<? super T, IdentifiableEntity> parentCategoryExtractor) {
        int score = 0;
        while (!interests1.isEmpty()) {
            // gets the most weighted interest from the list
            T maxWeightInterest = interests1.stream().max(Comparator.comparing(weightExtractor)).get();
            Map<T, Integer> scoresForMatching = new HashMap<>();
            for (T interest : interests2) {
                int matchingScore = 0;
                IdentifiableEntity interest1 = interestExtractor.apply(maxWeightInterest);
                IdentifiableEntity interest2 = interestExtractor.apply(interest);
                IdentifiableEntity parentCategory1 = parentCategoryExtractor.apply(maxWeightInterest);
                IdentifiableEntity parentCategory2 = parentCategoryExtractor.apply(interest);
                // check if direct matching or parent category matching applies
                if (interest1 != null && interest2 != null && interest1.getId() == interest2.getId()) {
                    matchingScore = directMatchingPoints;
                } else if (parentCategory1 != null && parentCategory2 != null
                        && parentCategory1.getId() == parentCategory2.getId()) {
                    matchingScore = parentCategoryMatchingPoints;
                }
                // only add for matching found
                if (matchingScore > 0) {
                    int weightedMatchingScore =
                            weightExtractor.apply(maxWeightInterest) * weightExtractor.apply(interest) * matchingScore;
                    scoresForMatching.put(interest, weightedMatchingScore);
                }
            }
            //get interest with max score from scoresForMatching;
            Optional<Map.Entry<T, Integer>> maxScoreInterest =
                    scoresForMatching.entrySet().stream().max(Comparator.comparing(Map.Entry<T, Integer>::getValue));
            if (maxScoreInterest.isPresent() && maxScoreInterest.get().getValue() > 0) {
                score += maxScoreInterest.get().getValue();
                //  remove the maxMentorInterest from interests2
                interests2.remove(maxScoreInterest.get().getKey());
            }
            //remove from interests1
            interests1.remove(maxWeightInterest);
        }
        return score;
    }

    /**
     * Get user from authentication.
     * @return user if exists valid user authentication otherwise null
     */
    public static User getAuthUser()  {
        User user = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof UserAuthentication) {
            UserAuthentication userAuth = (UserAuthentication) auth;
            user = (User) userAuth.getPrincipal();
        } else if (auth != null  && auth.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            user = userDetails.getUser();
        }
        return user;
    }

    public static boolean isMentor() {
        User user = getAuthUser();
        if (user == null || user.getRoles() == null) {
            return false;
        }
        for (UserRole r : user.getRoles()) {
            if ("mentor".equalsIgnoreCase(r.getValue())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMentee() {
        User user = getAuthUser();
        if (user == null || user.getRoles() == null) {
            return false;
        }
        for (UserRole r : user.getRoles()) {
            if ("mentee".equalsIgnoreCase(r.getValue())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Audit with entity with created by and createdOn, last modified on/by information.
     * @param entity the  entity
     * @param <T> the auditable entity
     */
    public static <T extends AuditableUserEntity> void audit(T entity)  {
        User user = getAuthUser();
        if (user != null) {
            Date now = new Date();
            entity.setCreatedOn(now);
            entity.setLastModifiedOn(now);
            entity.setCreatedBy(user.getId());
            entity.setLastModifiedBy(user.getId());
        }
    }

    /**
     * Get user id match given role .
     * @param role the role name
     * @return the user id if exist valid user role otherwise null.
     */
    public static Long getUserRoleId(String role)  {
        Long id = null;
        User user = getAuthUser();
        if (user != null && user.getRoles().stream().anyMatch(r -> role.equals(r.getValue()))) {
           id = user.getId();
        }
        return id;
    }

    /**
     * Get mentor role user id.
     * @return the mentor id if exist valid mentor role user otherwise null.
     */
    public static Long getMentorId()  {
        return getUserRoleId("MENTOR");
    }

    /**
     * Get mentee role user id.
     * @return the mentor id if exist valid mentee role user otherwise null.
     */
    public static Long getMenteeId()  {
        return getUserRoleId("MENTEE");
    }

    /**
     * Audit with activity entity.
     * @param activityRepository the activity repository
     * @param activityType the activity type
     * @param objectId the object id
     * @param description the description
     * @param institutionalProgramId the institutional program id
     * @param menteeId the mentee id
     * @param mentorId  mentor id
     * @param global the global flag
     */
    public static  void audit(ActivityRepository activityRepository,
            ActivityType activityType, long objectId,
            String description, Long institutionalProgramId, Long menteeId, Long mentorId, boolean global)  {
        User user = getAuthUser();
        if (user != null) {
            Activity activity = new Activity();
            audit(activity);
            activity.setObjectId(objectId);
            activity.setActivityType(activityType);
            activity.setDescription(description);
            activity.setInstitutionalProgramId(institutionalProgramId);
            activity.setMenteeId(menteeId);
            activity.setMentorId(mentorId);
            activity.setGlobal(global);
            activityRepository.save(activity);
        }
    }


    /**
     * Audit with activity entity.
     * @param activityRepository the activity repository
     * @param menteeMentorProgramRepository the mentee mentor program repository
     * @param activityType the activity type
     * @param objectId the object id
     * @param description the description
     * @param menteeMentorProgramId the mentee mentor program id
     * @param global the global flag
     */
    public static  void audit(ActivityRepository activityRepository,
            MenteeMentorProgramRepository menteeMentorProgramRepository,
            ActivityType activityType, long objectId,
            String description, Long menteeMentorProgramId, boolean global)  {
        User user = getAuthUser();
        if (user != null) {
            Long institutionalProgramId = null;
            Long menteeId = getMenteeId();
            Long mentorId = getMentorId();
            if (menteeMentorProgramId != null) {
                MenteeMentorProgram program = menteeMentorProgramRepository.findOne(menteeMentorProgramId);
                institutionalProgramId = program.getInstitutionalProgram().getId();
                if (menteeId == null) {
                    menteeId = program.getMentee().getId();
                }
                if (mentorId == null) {
                    mentorId = program.getMentor().getId();
                }
            }
            audit(activityRepository, activityType, objectId, description,
                    institutionalProgramId, menteeId, mentorId, global);
        }
    }

    /**
     * Handle upload documents request.
     * @param uploadDirectory the upload directory.
     * @param documents the documents.
     * @return the saved documents list.
     * @throws MentorMeException throws if error to save uploaded document.
     */
    public static List<Document> uploadDocuments(String uploadDirectory,
            MultipartFile[] documents) throws MentorMeException {
        List<Document> docs = new ArrayList<>();
        if (documents != null && documents.length > 0) {
            for (MultipartFile document: documents) {
                try {
                    String outFolder = FilenameUtils.concat(uploadDirectory, UUID.randomUUID()
                                                                                 .toString());
                    FileUtils.forceMkdir(new File(outFolder));
                    String path = FilenameUtils.concat(outFolder, document.getOriginalFilename());
                    FileUtils.writeByteArrayToFile(new File(path), document.getBytes());
                    Document doc = new Document();
                    doc.setName(document.getOriginalFilename());
                    doc.setPath(path);
                    audit(doc);
                    docs.add(doc);
                } catch (IOException e) {
                    throw new MentorMeException(CustomMessageSource.getMessage("uploadDocument.error"), e);
                }
            }
        }
        return docs;
    }


    /**
     * Check id and entity for update method.
     *
     * @param id the id of the entity to update
     * @param entity the entity to update
     * @param <T> the entity class
     * @throws IllegalArgumentException if id is not positive or entity is null or id of entity is not positive
     * or id of  entity not match id
     */
    public static <T extends IdentifiableEntity> void checkUpdate(long id, T entity) {
        checkPositive(id, "id");
        checkNull(entity, "entity");
        checkPositive(entity.getId(), "entity.id");
        if (entity.getId() != id) {
            throw new IllegalArgumentException(CustomMessageSource.getMessage("update.notSameId.error"));
        }
    }

    /**
     * Search match mentor/mentee according to match criteria.
     * @param entity the entity
     * @param criteria the search criteria
     * @param matchSearchCriteria the match criteria
     * @param service the mentor/mentee service
     * @param <T> the search criteria class
     * @param <S> the entity class
     * @param <R> the entity class
     * @return the match entities
     * @throws IllegalArgumentException if match criteria is invalid
     * @throws MentorMeException if any error happens during searching
     */
    public static <T extends InstitutionUserSearchCriteria, S extends InstitutionUser, R extends InstitutionUser>
     List<R> searchMatchEntities(S entity, T criteria, MatchSearchCriteria matchSearchCriteria,
            GenericService<R, T> service) throws MentorMeException {
        if (entity.isAssignedToInstitution()) {
            criteria.setInstitutionId(entity.getInstitution().getId());
        } else {
            criteria.setAssignedToInstitution(false);
        }
        Paging paging = null;
        // copy match criteria properties
        if (matchSearchCriteria != null) {
            criteria.setPersonalInterests(matchSearchCriteria.getPersonalInterests());
            criteria.setProfessionalInterests(matchSearchCriteria.getProfessionalInterests());
            criteria.setDistance(matchSearchCriteria.getDistance());
            if (matchSearchCriteria.getDistance() !=  null) {
                criteria.setLongitude(entity.getLongitude());
                criteria.setLatitude(entity.getLatitude());
            }
            if (matchSearchCriteria.getMaxCount() != null) {
                checkPositive(matchSearchCriteria.getMaxCount(), "matchSearchCriteria.maxCount");
                paging = new Paging();
                paging.setPageSize(matchSearchCriteria.getMaxCount());
            }
        }
        return service.search(criteria, paging).getEntities();
    }
}
