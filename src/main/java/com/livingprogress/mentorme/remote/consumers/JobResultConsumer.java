package com.livingprogress.mentorme.remote.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.livingprogress.mentorme.aop.LogAspect;
import com.livingprogress.mentorme.remote.Constant;
import com.livingprogress.mentorme.remote.entities.AddIndexResponse;
import com.livingprogress.mentorme.remote.entities.CreateIndexResponse;
import com.livingprogress.mentorme.remote.entities.DeleteIndexResponse;
import com.livingprogress.mentorme.remote.entities.JobAction;
import com.livingprogress.mentorme.remote.entities.JobActions;
import com.livingprogress.mentorme.remote.entities.JobResultEvent;
import com.livingprogress.mentorme.remote.entities.ListResourcesResponse;
import com.livingprogress.mentorme.utils.CustomMessageSource;
import com.livingprogress.mentorme.utils.Helper;
import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.fn.Consumer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This is check job result event consumer.
 */
@Service
public class JobResultConsumer extends BaseConsumer implements Consumer<Event<JobResultEvent>> {

    /**
     * The result field name.
     */
    private static final String RESULT = "result";

    /**
     * Handle check job result event.
     *
     * @param ev the event.
     */
    @Override
    public void accept(final Event<JobResultEvent> ev) {
        JobResultEvent event = ev.getData();
        String action = event.getAction();
        JsonNode res = getHodClient().getJobResult(event.getJobResult()
                                                        .getJobID());
        JsonNode status = res.get("status");
        LogAspect.LOGGER.info(CustomMessageSource.getMessage("jobResult.status", status.asText()));
        if (Constant.JOB_FINISHED.equals(status.asText())) {
            JsonNode actionsNode = res.withArray("actions");
            List<JobAction> actions = new ArrayList<>();
            try {
                if (Constant.LIST_RESOURCES.equals(action)) {
                    JsonNode result = actionsNode.get(0)
                                                 .get(RESULT);
                    ListResourcesResponse response = Helper.MAPPER.treeToValue(result, ListResourcesResponse.class);
                    List<String> resources = response.getPrivateResources()
                                                     .stream()
                                                     .map(ListResourcesResponse.PrivateResource::getResource)
                                                     .map(String::toLowerCase)
                                                     .collect(Collectors.toList());
                    boolean addIndex = false;
                    boolean deleteIndex = false;
                    if (!resources.contains(getIndexName().toLowerCase())) {
                        addIndex = true;
                    } else if (isForceDeleteIndex()) {
                        deleteIndex = true;
                    }
                    if (deleteIndex) {
                        // remove indexes
                        JobAction jobAction = new JobAction();
                        jobAction.setName(Constant.DELETE_TEXT_INDEX);
                        jobAction.setVersion(Constant.VERSION1);
                        jobAction.setParams(Collections.singletonMap(Constant.INDEX, getIndexName()));
                        actions.add(jobAction);
                        getEventBus().notify(Constant.DELETE_TEXT_INDEX, Event.wrap(new JobActions(actions)));
                    } else if (addIndex) {
                        // add new indexes
                        getEventBus().notify(Constant.CREATE_TEXT_INDEX, Event.wrap(null));
                    } else {
                        // do nothing and should check user updates without new indexes.
                        getEventBus().notify(Constant.SEARCH_USER, Event.wrap(Collections.emptyList()));
                    }
                } else if (Constant.DELETE_TEXT_INDEX.equals(action)) {
                    JsonNode result = actionsNode.get(0)
                                                 .get(RESULT);
                    DeleteIndexResponse response = Helper.MAPPER.treeToValue(result, DeleteIndexResponse.class);
                    // have to confirm to delete or invoke delete api twice to delete index rightly
                    if (!Boolean.TRUE.equals(response.getDeleted())) {
                        JobAction jobAction = new JobAction();
                        jobAction.setName(Constant.DELETE_TEXT_INDEX);
                        jobAction.setVersion(Constant.VERSION1);
                        Map<String, String> params = new HashMap<>();
                        params.put(Constant.INDEX, getIndexName());
                        params.put(Constant.CONFIRM, response.getConfirm());
                        jobAction.setParams(params);
                        actions.add(jobAction);
                    }
                    if (!actions.isEmpty()) {
                        // should confirm to delete
                        getEventBus().notify(Constant.DELETE_TEXT_INDEX, Event.wrap(new JobActions(actions)));
                    } else {
                        // now create new index
                        getEventBus().notify(Constant.CREATE_TEXT_INDEX, Event.wrap(getIndexName()));
                    }
                } else if (Constant.CREATE_TEXT_INDEX.equals(action)) {
                    List<String> indexes = new ArrayList<>();
                    for (int i = 0; i < actionsNode.size(); i++) {
                        JsonNode result = actionsNode.get(i)
                                                     .get(RESULT);
                        CreateIndexResponse response = Helper.MAPPER.treeToValue(result, CreateIndexResponse.class);
                        Helper.logDebugMessage(LogAspect.LOGGER,
                                CustomMessageSource.getMessage("jobResult.createIndex",
                                        response.getIndex(), response.getMessage()));
                        indexes.add(response.getIndex());
                    }
                    getEventBus().notify(Constant.SEARCH_USER, Event.wrap(indexes));
                } else if (Constant.ADD_TO_TEXT_INDEX.equals(action)) {
                    JsonNode result = actionsNode.get(0)
                                                 .get(RESULT);
                    AddIndexResponse response = Helper.MAPPER.treeToValue(result, AddIndexResponse.class);
                    Helper.logDebugMessage(LogAspect.LOGGER, CustomMessageSource.getMessage("jobResult.addIndex",
                            response.getReferences().stream().map(AddIndexResponse.References::getReference)
                                      .collect(Collectors.joining(Constant.COMMA))));
                    // exit successfully
                    getLatch().countDown();
                }
            } catch (JsonProcessingException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        } else if (Constant.JOB_FAILED.equals(status.asText())) {
            LogAspect.LOGGER.error(CustomMessageSource.getMessage("jobResult.failed", action, res.toString()));
            // break entire process
            getLatch().countDown();
        } else {
            LogAspect.LOGGER.info(CustomMessageSource.getMessage("jobResult.queuedOrProgress", action));
            try {
                Thread.sleep(getWaitTime());
            } catch (InterruptedException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            // recheck job result
            accept(ev);
        }
    }
}
