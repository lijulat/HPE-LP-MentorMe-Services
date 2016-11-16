package com.livingprogress.mentorme.remote.consumers;

import com.livingprogress.mentorme.exceptions.ConfigurationException;
import com.livingprogress.mentorme.remote.services.HODClient;
import com.livingprogress.mentorme.utils.Helper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import reactor.bus.EventBus;

import javax.annotation.PostConstruct;
import java.util.concurrent.CountDownLatch;

import static lombok.AccessLevel.PROTECTED;

/**
 * This is base consumer for all consumers.
 */
@NoArgsConstructor(access = PROTECTED)
public abstract class BaseConsumer {
    /**
     * The event bus.
     */
    @Autowired
    @Getter(value = PROTECTED)
    private EventBus eventBus;

    /**
     * The hod client.
     */
    @Autowired
    @Getter(value = PROTECTED)
    private HODClient hodClient;

    /**
     * The count down latch.
     */
    @Autowired
    @Getter(value = PROTECTED)
    private CountDownLatch latch;

    /**
     * The index name.
     */
    @Value("${havenondemand.indexName}")
    @Getter(value = PROTECTED)
    private String indexName;

    /**
     * The force delete index flag.
     */
    @Value("${havenondemand.forceDeleteIndex}")
    @Getter(value = PROTECTED)
    private boolean forceDeleteIndex;

    /**
     * The wait time.
     */
    @Value("${havenondemand.waitTime}")
    @Getter(value = PROTECTED)
    private long waitTime;

    /**
     * Check if all required fields are initialized properly.
     *
     * @throws ConfigurationException if any required field is not initialized properly.
     */
    @PostConstruct
    protected void checkConfiguration() {
        Helper.checkConfigNotNull(hodClient, "hodClient");
        Helper.checkConfigNotNull(eventBus, "eventBus");
        Helper.checkConfigNotNull(latch, "latch");
        Helper.checkPositive(waitTime, "waitTime");
        Helper.checkConfigState(!Helper.isNullOrEmpty(indexName), "indexName");
    }
}
