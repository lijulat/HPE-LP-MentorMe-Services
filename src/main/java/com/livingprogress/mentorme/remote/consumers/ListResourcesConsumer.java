package com.livingprogress.mentorme.remote.consumers;

import com.livingprogress.mentorme.remote.Constant;
import com.livingprogress.mentorme.remote.entities.JobResultResponse;
import com.livingprogress.mentorme.remote.entities.JobResultEvent;
import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.fn.Consumer;

/**
 * This is list resources event consumer.
 */
@Service
public class ListResourcesConsumer extends BaseConsumer implements Consumer<Event<Void>> {

    /**
     * Handle list resources event.
     *
     * @param ev the event.
     */
    @Override
    public void accept(Event<Void> ev) {
        JobResultResponse result = getHodClient().listResources();
        getEventBus().notify(Constant.JOB_RESULT,
                Event.wrap(new JobResultEvent(null, result, Constant.LIST_RESOURCES)));
    }
}
