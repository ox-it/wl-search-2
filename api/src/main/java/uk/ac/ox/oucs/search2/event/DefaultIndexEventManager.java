package uk.ac.ox.oucs.search2.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.api.NotificationService;
import uk.ac.ox.oucs.search2.IndexingService;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author Colin Hebert
 */
public class DefaultIndexEventManager extends IndexEventManager {
    private final static Log logger = LogFactory.getLog(DefaultIndexEventManager.class);
    private IndexingService indexingService;
    private Map<String, Collection<IndexEventHandler>> indexEventHandlers;

    public DefaultIndexEventManager(NotificationService notificationService) {
        super(notificationService);
        indexEventHandlers = new HashMap<String, Collection<IndexEventHandler>>();
    }

    public void addContentEventHandler(IndexEventHandler indexEventHandler) {
        super.addContentEventHandler(indexEventHandler);
        for (String eventName : indexEventHandler.getSupportedEventTypes()) {
            Collection<IndexEventHandler> eventHandlers = indexEventHandlers.get(eventName);
            if (eventHandlers == null) {
                eventHandlers = new LinkedList<IndexEventHandler>();
                indexEventHandlers.put(eventName, eventHandlers);
            }
            eventHandlers.add(indexEventHandler);
        }
    }

    @Override
    protected void notify(Event event) {
        for (IndexEventHandler eventHandler : indexEventHandlers.get(event.getEvent())) {
            handleEvent(event, eventHandler);
        }
    }

    private void handleEvent(Event event, IndexEventHandler eventHandler) {
        if (!eventHandler.isHandled(event))
            return;

        switch (eventHandler.getIndexAction(event)) {
            case INDEX_FILE:
                indexingService.indexContent(eventHandler.getName(), eventHandler.getContent(event));
                break;
            case UNINDEX_FILE:
                indexingService.unindexContent(eventHandler.getName(), eventHandler.getContent(event));
                break;

            case INDEX_SITE:
                indexingService.indexSite(eventHandler.getName(), eventHandler.getContent(event), eventHandler.getSite(event));
                break;
            case REINDEX_SITE:
                indexingService.reindexSite(eventHandler.getName(), eventHandler.getContent(event), eventHandler.getSite(event));
                break;
            case UNINDEX_SITE:
                indexingService.unindexSite(eventHandler.getName(), eventHandler.getSite(event));
                break;

            case INDEX_ALL:
                indexingService.indexAll(eventHandler.getName(), eventHandler.getContent(event));
                break;
            case REINDEX_ALL:
                indexingService.reindexAll(eventHandler.getName(), eventHandler.getContent(event));
                break;
            case UNINDEX_ALL:
                indexingService.unindexAll(eventHandler.getName());
                break;
            default:
                logger.warn("Action '" + eventHandler.getIndexAction(event) + "' not supported, nothing has been done.");
        }
    }

    public void setIndexingService(IndexingService indexingService) {
        this.indexingService = indexingService;
    }
}
