package com.livingprogress.mentorme.remote.consumers;

import com.livingprogress.mentorme.remote.Constant;
import com.livingprogress.mentorme.remote.entities.JobActions;
import com.livingprogress.mentorme.remote.entities.JobResultResponse;
import com.livingprogress.mentorme.remote.entities.JobResultEvent;
import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.fn.Consumer;

/**
 * This is delete index event consumer.
 */
@Service
public class DeleteIndexConsumer extends BaseConsumer implements Consumer<Event<JobActions>> {

    /**
     * Handle delete index event.
     *
     * @param ev the event.
     */
    @Override
    public void accept(Event<JobActions> ev) {
        JobActions actions = ev.getData();
        JobResultResponse result = getHodClient().submitJob(actions);
        getEventBus().notify(Constant.JOB_RESULT,
                Event.wrap(new JobResultEvent(actions, result, Constant.DELETE_TEXT_INDEX)));
    }
}
