package com.livingprogress.mentorme.remote;

import com.livingprogress.mentorme.aop.LogAspect;
import com.livingprogress.mentorme.remote.consumers.AddIndexConsumer;
import com.livingprogress.mentorme.remote.consumers.CreateIndexConsumer;
import com.livingprogress.mentorme.remote.consumers.DeleteIndexConsumer;
import com.livingprogress.mentorme.remote.consumers.JobResultConsumer;
import com.livingprogress.mentorme.remote.consumers.ListResourcesConsumer;
import com.livingprogress.mentorme.remote.consumers.SearchUsersConsumer;
import com.livingprogress.mentorme.utils.CustomMessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import reactor.Environment;
import reactor.bus.Event;
import reactor.bus.EventBus;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static reactor.bus.selector.Selectors.$;

/**
 * The main entrance for remote application.
 */
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAutoConfiguration
@EnableJpaRepositories("com.livingprogress.mentorme.services.springdata")
@EntityScan("com.livingprogress.mentorme.entities")
@ComponentScan(basePackages = {"com.livingprogress.mentorme.aop",
        "com.livingprogress.mentorme.services", "com.livingprogress.mentorme.remote"})
public class RemoteApplication implements CommandLineRunner {

    /**
     * The environment.
     *
     * @return the environment.
     */
    @Bean
    public Environment env() {
        return Environment.initializeIfEmpty()
                          .assignErrorJournal();
    }

    /**
     * The event bus.
     *
     * @param env the environment.
     * @return the event bus
     */
    @Bean
    public EventBus createEventBus(Environment env) {
        return EventBus.create(env, Environment.THREAD_POOL);
    }

    /**
     * The event bus.
     */
    @Autowired
    private EventBus eventBus;

    /**
     * The job result event consumer.
     */
    @Autowired
    private JobResultConsumer jobResultConsumer;

    /**
     * The list resources event consumer.
     */
    @Autowired
    private ListResourcesConsumer listResourcesConsumer;

    /**
     * The delete index event consumer.
     */
    @Autowired
    private DeleteIndexConsumer deleteIndexConsumer;

    /**
     * The create index event consumer.
     */
    @Autowired
    private CreateIndexConsumer createIndexConsumer;

    /**
     * The search user event consumer.
     */
    @Autowired
    private SearchUsersConsumer searchUsersConsumer;

    /**
     * The add index event consumer.
     */
    @Autowired
    private AddIndexConsumer addIndexConsumer;

    /**
     * The countdown latch.
     * @return the countdown latch.
     */
    @Bean
    public CountDownLatch latch() {
        return new CountDownLatch(1);
    }

    /**
     * The countdown latch.
     */
    @Autowired
    private CountDownLatch latch;

    /**
     * Register consumers and start fire list resources event.
     *
     * @param args the command arguments.
     * @throws Exception throws if any error happens
     */
    @Override
    public void run(String... args) throws Exception {
        eventBus.on($(Constant.JOB_RESULT), jobResultConsumer);
        eventBus.on($(Constant.LIST_RESOURCES), listResourcesConsumer);
        eventBus.on($(Constant.DELETE_TEXT_INDEX), deleteIndexConsumer);
        eventBus.on($(Constant.CREATE_TEXT_INDEX), createIndexConsumer);
        eventBus.on($(Constant.SEARCH_USER), searchUsersConsumer);
        eventBus.on($(Constant.ADD_TO_TEXT_INDEX), addIndexConsumer);
        long start = System.currentTimeMillis();
        eventBus.notify(Constant.LIST_RESOURCES, Event.wrap(null));
        latch.await();
        long elapsed = System.currentTimeMillis() - start;
        LogAspect.LOGGER.info(CustomMessageSource.getMessage("remote.elapsedTime", elapsed));
    }

    /**
     * The main entry point of the application.
     *
     * @param args the arguments
     * @throws InterruptedException throws if error to interrupt
     */
    public static void main(String[] args) throws InterruptedException {
        SpringApplication app = new SpringApplication(RemoteApplication.class);
        // no need to use web
        app.setWebEnvironment(false);
        ConfigurableApplicationContext ctx = app.run(args);
        ctx.getBean(CountDownLatch.class)
           .await(1, TimeUnit.SECONDS);
        ctx.getBean(Environment.class)
           .shutdown();
        ctx.close();
    }
}
