package com.livingprogress.mentorme;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.livingprogress.mentorme.entities.*;
import com.livingprogress.mentorme.services.LookupService;
import com.livingprogress.mentorme.utils.Helper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.Filter;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * The base test class for all tests.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes ={Application.class})
@WebAppConfiguration
@EnableWebSecurity
@TestPropertySource(locations = "classpath:test.properties")
public abstract class BaseTest {
    /**
     * The sql folder.
     */
    private static final String SQL_FOLDER = "sqls";

    /**
     * The sql names list.
     */
    private static final List<String> SQLS = Arrays.asList("clear.sql", "testdata.sql");

    /**
     * The token auth header name.
     */
    protected static final String AUTH_HEADER_NAME = "X-AUTH-TOKEN";

    /**
     * The mock multi part file1
     */
    protected static MockMultipartFile FILE1 = new MockMultipartFile("files", "test1.txt", "application/octet-stream", "doc1".getBytes());

    /**
     * The mock multi part file2
     */
    protected static MockMultipartFile FILE2 = new MockMultipartFile("files", "test2.txt", "application/octet-stream", "doc2".getBytes());

    /**
     * The lookup service used to perform operations.
     */
    @Autowired
    protected LookupService lookupService;

    /**
     * The entity manager.
     */
    @Autowired
    protected EntityManager entityManager;

    /**
     * The platform transaction manager.
     */
    @Autowired
    private PlatformTransactionManager txManager;

    /**
     * The email server port.
     */
    @Value("${spring.mail.port}")
    private int port;

    /**
     * The from email address.
     */
    @Value("${mail.from}")
    private String fromAddress;

    /**
     * The upload directory.
     */
    @Value("${uploadDirectory}")
    private String uploadDirectory;

    /**
     * The upload directory cleanup flag.
     */
    @Value("${cleanupUploadDirectory}")
    private boolean cleanupUploadDirectory;

    /**
     * The web application context.
     */
    @Autowired
    protected WebApplicationContext context;

    /**
     * The object mapper.
     */
    protected ObjectMapper objectMapper = TestConfig.buildObjectMapper();

    /**
     * The mock mvc object.
     */
    protected MockMvc mockMvc;

    /**
     * The spring security filter chain.
     */
    @Autowired
    private Filter springSecurityFilterChain;

    /**
     * The mock mvc object with security support.
     */
    protected MockMvc mockAuthMvc;

    /**
     * The wiser email server.
     */
    protected Wiser wiser;

    /**
     * The sample future date.
     */
    protected Date sampleFutureDate;

    /**
     * The system admin token.
     */
    protected String systemAdminToken;

    /**
     * The institution admin token.
     */
    protected String institutionAdminToken;

    /**
     * The mentor token.
     */
    protected String mentorToken;

    /**
     * The mentee token.
     */
    protected String menteeToken;

    /**
     * The default locale.
    */
    private Locale defaultLocale;

    /**
     * Setup test.
     *
     * @throws Exception throws if any error happen
     */
    @Before
    public void setupTest() throws Exception {
        wiser = new Wiser(port);
        wiser.start();
        this.mockMvc = webAppContextSetup(context).build();
        this.mockAuthMvc = webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();
        systemAdminToken = readFile("sytemAdminToken.txt");
        institutionAdminToken = readFile("institutionAdminToken.txt");
        mentorToken = readFile("mentorToken.txt");
        menteeToken = readFile("menteeToken.txt");
        for (String sql : SQLS) {
            runSQL(sql);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 2);
        sampleFutureDate = calendar.getTime();
        defaultLocale = Locale.getDefault();
        // use english as default locale during test
        Locale.setDefault(Locale.ENGLISH);
        cleanupUploadDirectory();
    }

    /**
     * Tear down test.
     *
     * @throws Exception throws if any error happen
     */
    @After
    public void tearDown() throws Exception {
        wiser.stop();
        if (defaultLocale != null) {
            Locale.setDefault(defaultLocale);
        }
        cleanupUploadDirectory();
    }

    /**
     * Clean up upload directory.
     *
     * @throws IOException throws if any io error happen
     */
    protected void cleanupUploadDirectory() throws IOException {
       File dir = new File(uploadDirectory);
        if(!dir.exists()){
            FileUtils.forceMkdir(dir);
        } else if(cleanupUploadDirectory){
            FileUtils.cleanDirectory(dir);
        }
    }

    /**
     * Read file.
     *
     * @param name the file name.
     * @return the file content.
     * @throws IOException throws if any io error happen
     */
    protected static String readFile(String name) throws IOException {
        File file = new File(BaseTest.class.getResource("/data/" + name).getFile());
        return FileUtils.readFileToString(file, Helper.UTF8);
    }

    /**
     * Run sql.
     *
     * @param name the sql file name
     * @throws IOException throws if any io error happen
     */
    @Transactional
    protected void runSQL(String name) throws IOException {
        Stream<String> lines = FileUtils.readLines(new File(SQL_FOLDER, name), Helper.UTF8).stream()
                                        .filter(c -> !Helper.isNullOrEmpty(c) && !c.trim()
                                                                                   .startsWith("-"));
        new TransactionTemplate(txManager).execute(status -> {
            lines.forEach(sql -> entityManager.createNativeQuery(sql).executeUpdate());
            return null;
        });
    }

    /**
     * Verify email.
     *
     * @param subject the email subject contains string.
     * @param body the email body contains string.
     * @param toAddress the to email address
     * @throws Exception throws if any error happen
     */
    protected void verifyEmail(String subject, String body, String toAddress) throws Exception {
        assertEquals(1, wiser.getMessages().size());
        WiserMessage message = wiser.getMessages().get(0);
        assertEquals(fromAddress, message.getEnvelopeSender());
        assertEquals(toAddress, message.getEnvelopeReceiver());
        assertTrue(message.getMimeMessage().getSubject().contains(subject));
        assertTrue(((String) message.getMimeMessage().getContent()).contains(body));
    }

    /**
     * Read search result from json.
     *
     * @param json the json.
     * @param target the target class
     * @param <T> the class name
     * @return the search result.
     * @throws Exception throws if any error happen
     */
    protected <T> SearchResult<T> readSearchResult(String json, Class<T> target) throws Exception {
        JavaType type = objectMapper.getTypeFactory().constructParametrizedType(SearchResult.class, SearchResult.class, target);
        return objectMapper.readValue(json, type);
    }

    /**
     * Get search result with reading content from url.
     *
     * @param url the url.
     * @param target the target class
     * @param <T> the class name
     * @return the search result.
     * @throws Exception throws if any error happen
     */
    protected <T> SearchResult<T> getSearchResult(String url, Class<T> target) throws Exception {
        String json = mockMvc.perform(MockMvcRequestBuilders.get(url)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return readSearchResult(json, target);
    }

    /**
     * Get total pages.
     *
     * @param total the total number.
     * @param pageSize the page size.
     * @return the total pages.
     * @throws Exception throws if any error happen
     */
    protected static int getTotalPages(long total, long pageSize) throws Exception {
        return (int) ((total + pageSize - 1) / pageSize);
    }

    /**
     * Check identifiable entity.
     *
     * @param entity the entity
     * @param <T> the entity class
     */
    protected static <T extends IdentifiableEntity> void checkEntity(T entity) {
        assertEquals(0, entity.getId());
    }

    /**
     * Verify entity.
     *
     * @param oldEntity the old entity
     * @param newEntity the new entity
     * @param <T> the entity class
     */
    protected static <T extends IdentifiableEntity> void verifyEntity(T oldEntity, T newEntity) {
        long id = newEntity.getId();
        assertTrue(id > 0);
        oldEntity.setId(id);
    }

    /**
     * Check entities.
     *
     * @param entities the entities
     * @param <T> the entity class
     */
    protected static <T extends IdentifiableEntity> void checkEntities(List<T> entities) {
        assertTrue(entities.size() > 0);
        entities.forEach(BaseTest::checkEntity);
    }

    /**
     * Verify entities.
     *
     * @param oldEntities the old entities
     * @param newEntities the new entities
     * @param <T> the entity class
     */
    protected static <T extends IdentifiableEntity> void verifyEntities(List<T> oldEntities, List<T> newEntities){
        assertTrue(oldEntities.size() > 0);
        assertEquals(oldEntities.size(), newEntities.size());
        IntStream.range(0, oldEntities.size()).forEach(idx -> {
            long id = newEntities.get(idx).getId();
            assertTrue(id > 0);
            oldEntities.get(idx).setId(id);
        });
    }

    /**
     * Verify documents.
     */
    protected  void verifyDocuments(long count) throws Exception {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Document> root = query.from(Document.class);
        Predicate pd = cb.or(
                cb.equal(root.get("name"), FILE1.getOriginalFilename()),
                cb.equal(root.get("name"), FILE2.getOriginalFilename()));
      query.select(cb.count(root))
             .where(pd);
        assertEquals(count, (long) entityManager.createQuery(query)
                                             .getSingleResult());
    }

    /**
     * Verify activity entity.
     * @param activityType the activity type
     * @param objectId the object id
     * @param description the description
     * @param institutionalProgramId the institutional program id
     * @param menteeId the mentee id
     * @param mentorId  mentor id
     * @param global the global flag
     */
    protected void verifyActivity(
            ActivityType activityType, long objectId,
            String description, Long institutionalProgramId,
            Long menteeId, Long mentorId, boolean global)  {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Activity> root = query.from(Activity.class);
        Predicate pd = cb.and(cb.equal(root.get("objectId"), objectId),
                cb.equal(root.get("activityType"), activityType),
                cb.equal(root.get("description"), description),
                cb.equal(root.get("institutionalProgramId"), institutionalProgramId),
                cb.equal(root.get("menteeId"), menteeId),
                cb.equal(root.get("mentorId"), mentorId),
                cb.equal(root.get("global"), global));
        query.select(cb.count(root)).where(pd);
        assertEquals(1L, (long) entityManager.createQuery(query)
                                             .getSingleResult());
    }

    /**
     * Get multi value map for institutional program.
     * @param entity the entity.
     * @return the match multi value map.
     * @throws Exception throws if any error happens
     */
    protected MultiValueMap<String, String> getInstitutionalProgramParams(InstitutionalProgram entity) throws Exception{
        List<String> args = new ArrayList<>();
        args.addAll(Arrays.asList("programName", "startDate", "endDate",
                "institution.id", "programCategory.id",
                 "programCategory.value","durationInDays", "programImageUrl"));
        if(entity.getUsefulLinks() != null){
            IntStream.range(0, entity.getUsefulLinks().size()).forEach(idx -> {
                args.add("usefulLinks[" + idx + "].title");
                args.add("usefulLinks[" + idx + "].address");
                args.add("usefulLinks[" + idx + "].author.id");
                args.add("usefulLinks[" + idx + "].createdOn");
            });
        }
        if(entity.getResponsibilities() != null){
            IntStream.range(0, entity.getResponsibilities().size()).forEach(idx -> {
                args.add("responsibilities[" + idx + "].number");
                args.add("responsibilities[" + idx + "].title");
                args.add("responsibilities[" + idx + "].date");
                args.add("responsibilities[" + idx + "].menteeResponsibility");
                args.add("responsibilities[" + idx + "].mentorResponsibility");
            });
        }
        if(entity.getGoals() != null){
            IntStream.range(0, entity.getGoals().size()).forEach(idx -> {
                args.add("goals[" + idx + "].number");
                args.add("goals[" + idx + "].subject");
                args.add("goals[" + idx + "].description");
                args.add("goals[" + idx + "].goalCategory.id");
                args.add("goals[" + idx + "].goalCategory.value");
                args.add("goals[" + idx + "].durationInDays");
                args.add("goals[" + idx + "].custom");
                args.add("goals[" + idx + "].customData.mentor.id");
                args.add("goals[" + idx + "].customData.mentee.id");
                if(entity.getGoals().get(idx).getUsefulLinks() != null){
                    IntStream.range(0, entity.getUsefulLinks().size()).forEach(idy -> {
                        args.add("goals[" + idx + "].usefulLinks[" + idy + "].title");
                        args.add("goals[" + idx + "].usefulLinks[" + idy + "].address");
                        args.add("goals[" + idx + "].usefulLinks[" + idy + "].author.id");
                        args.add("goals[" + idx + "].usefulLinks[" + idy + "].createdOn");
                    });
                }
                if(entity.getGoals().get(idx).getTasks() != null){
                    IntStream.range(0, entity.getGoals().get(idx).getTasks().size()).forEach(idy -> {
                        args.add("goals[" + idx + "].tasks[" + idy + "].number");
                        args.add("goals[" + idx + "].tasks[" + idy + "].description");
                        args.add("goals[" + idx + "].tasks[" + idy + "].durationInDays");
                        args.add("goals[" + idx + "].tasks[" + idy + "].mentorAssignment");
                        args.add("goals[" + idx + "].tasks[" + idy + "].menteeAssignment");
                        args.add("goals[" + idx + "].tasks[" + idy + "].menteeAssignment");
                        args.add("goals[" + idx + "].tasks[" + idy + "].custom");
                        args.add("goals[" + idx + "].tasks[" + idy + "].custom");
                        args.add("goals[" + idx + "].tasks[" + idy + "].customData.mentee.id");
                        args.add("goals[" + idx + "].tasks[" + idy + "].customData.mentor.id");
                    });
                }
            });
        }
        return postForm(entity, args);
    }

    /**
     * Get multi value map for goal.
     * @param entity the entity.
     * @return the match multi value map.
     * @throws Exception throws if any error happens
     */
    protected MultiValueMap<String, String> getGoalParams(Goal entity) throws Exception{
        List<String> args = new ArrayList<>();
        args.addAll(Arrays.asList("subject", "description",
                "goalCategory.id","goalCategory.value",
                "durationInDays", "institutionalProgramId",
                "custom", "number"));
        if(entity.getUsefulLinks() != null){
            IntStream.range(0, entity.getUsefulLinks().size()).forEach(idx -> {
                args.add("usefulLinks[" + idx + "].title");
                args.add("usefulLinks[" + idx + "].address");
                args.add("usefulLinks[" + idx + "].author.id");
                args.add("usefulLinks[" + idx + "].createdOn");
            });
        }
        if(entity.getTasks() != null){
            IntStream.range(0, entity.getTasks().size()).forEach(idx -> {
                args.add("tasks[" + idx + "].number");
                args.add("tasks[" + idx + "].description");
                args.add("tasks[" + idx + "].durationInDays");
                args.add("tasks[" + idx + "].mentorAssignment");
                args.add("tasks[" + idx + "].menteeAssignment");
                args.add("tasks[" + idx + "].menteeAssignment");
                args.add("tasks[" + idx + "].custom");
                args.add("tasks[" + idx + "].custom");
                args.add("tasks[" + idx + "].customData.mentee.id");
                args.add("tasks[" + idx + "].customData.mentor.id");
            });
        }
        if(entity.getCustomData() != null){
            args.add("customData.mentor.id");
            args.add("customData.mentee.id");
        }
        return postForm(entity, args);
    }

    /**
     * Get multi value map for task.
     * @param entity the entity.
     * @return the match multi value map.
     * @throws Exception throws if any error happens
     */
    protected MultiValueMap<String, String> getTaskParams(Task entity) throws Exception{
        List<String> args = new ArrayList<>();
        args.addAll(Arrays.asList("description","durationInDays",
                "custom", "mentorAssignment", "menteeAssignment", "goalId", "number"));
        if(entity.getUsefulLinks() != null){
            IntStream.range(0, entity.getUsefulLinks().size()).forEach(idx -> {
                args.add("usefulLinks[" + idx + "].title");
                args.add("usefulLinks[" + idx + "].address");
                args.add("usefulLinks[" + idx + "].author.id");
                args.add("usefulLinks[" + idx + "].createdOn");
            });
        }
        if(entity.getCustomData() != null){
            args.add("customData.mentor.id");
            args.add("customData.mentee.id");
        }
        return postForm(entity, args);
    }

    /**
     * Post form with object params.
     * @param obj the object.
     * @param props the properties
     * @param <T> the entity type
     * @return the multi value map
     * @throws Exception throws if any error happens.
     */
    public static <T extends IdentifiableEntity> MultiValueMap<String, String> postForm(T obj, List<String> props) throws Exception{
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        if(obj.getId() > 0){
            props.add("id");
        }
        for (String path : props) {
            Object prop = PropertyUtils.getProperty(obj, path);
            if (prop instanceof Date) {
                // special handle for date type to avoid locale issue
                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                params.set(path, format.format(prop));
            } else {
                params.set(path, BeanUtils.getProperty(obj, path));
            }
        }
        return params;
    }
}
