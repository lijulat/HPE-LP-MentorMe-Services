package com.livingprogress.mentorme.remote.consumers;

import com.livingprogress.mentorme.remote.Constant;
import com.livingprogress.mentorme.remote.entities.Document;
import com.livingprogress.mentorme.remote.entities.Documents;
import com.livingprogress.mentorme.remote.entities.JobResultResponse;
import com.livingprogress.mentorme.remote.entities.JobResultEvent;
import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.fn.Consumer;

import javax.transaction.Transactional;
import java.util.List;

/**
 * This is add index event consumer.
 */
@Service
public class AddIndexConsumer extends BaseConsumer implements Consumer<Event<List<Document>>> {
    /**
     * Handle add index event.
     *
     * @param ev the event.
     */
    @Transactional
    @Override
    public void accept(Event<List<Document>> ev) {
        List<Document> documents = ev.getData();
        JobResultResponse result = getHodClient().addIndex(getIndexName(), new Documents(documents));
        getEventBus().notify(Constant.JOB_RESULT,
                Event.wrap(new JobResultEvent(null, result, Constant.ADD_TO_TEXT_INDEX)));
    }
}
