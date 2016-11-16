package com.livingprogress.mentorme.remote.consumers;

import com.livingprogress.mentorme.remote.Constant;
import com.livingprogress.mentorme.remote.entities.JobResultEvent;
import com.livingprogress.mentorme.remote.entities.JobResultResponse;
import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.fn.Consumer;

/**
 * This is create index event consumer.
 */
@Service
public class CreateIndexConsumer extends BaseConsumer implements Consumer<Event<Void>> {

    /**
     * Handle create index event.
     *
     * @param ev the event.
     */
    @Override
    public void accept(Event<Void> ev) {
        JobResultResponse result = getHodClient().createIndex();
        getEventBus().notify(Constant.JOB_RESULT,
                Event.wrap(new JobResultEvent(null, result, Constant.CREATE_TEXT_INDEX)));
    }
}
