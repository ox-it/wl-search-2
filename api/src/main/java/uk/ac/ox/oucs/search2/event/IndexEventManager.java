package uk.ac.ox.oucs.search2.event;

import org.sakaiproject.event.api.*;
import org.w3c.dom.Element;
import uk.ac.ox.oucs.search2.IndexingService;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author Colin Hebert
 */
public class IndexEventManager implements NotificationAction {
    private IndexingService indexingService;
    private Map<String, Collection<IndexEventHandler>> indexEventHandlers;
    private NotificationEdit notification;

    public IndexEventManager(NotificationService notificationService) {
        notification = notificationService.addTransientNotification();
        // set the filter to any site related resource
        notification.setResourceFilter("/");
        notification.setAction(this);
    }

    public void addContentEventHandler(IndexEventHandler indexEventHandler) {
        for (String eventName : indexEventHandler.getSupportedEventTypes()) {
            notification.addFunction(eventName);

            Collection<IndexEventHandler> eventHandlers = indexEventHandlers.get(eventName);
            if (eventHandlers == null) {
                eventHandlers = new LinkedList<IndexEventHandler>();
                indexEventHandlers.put(eventName, eventHandlers);
            }
            eventHandlers.add(indexEventHandler);
        }
    }

    @Override
    public void set(Element element) {
    }

    @Override
    public void set(NotificationAction notificationAction) {
    }

    @Override
    public NotificationAction getClone() {
        return null;
    }

    @Override
    public void toXml(Element element) {
    }

    @Override
    public void notify(Notification notification, Event event) {
        for (IndexEventHandler eventHandler : indexEventHandlers.get(event.getEvent())) {
            handleEvent(event, eventHandler);
        }
    }

    private void handleEvent(Event event, IndexEventHandler eventHandler) {
        if (!eventHandler.isHandled(event))
            return;

        switch (eventHandler.getIndexAction(event)) {
            case INDEX_FILE:
                indexingService.indexContent(eventHandler.getContent(event));
                break;
            case UNINDEX_FILE:
                indexingService.unindexContent(eventHandler.getContent(event));
                break;
            case INDEX_ALL:
                indexingService.indexEverything();
                break;
            case UNINDEX_ALL:
                indexingService.unindexEverything();
                break;
            case INDEX_SITE:
                indexingService.indexSite(eventHandler.getSite(event));
                break;
            case UNINDEX_SITE:
                indexingService.unindexSite(eventHandler.getSite(event));
                break;
            case INDEX_SITETOOL:
                indexingService.unindexSiteTool(eventHandler.getSiteTool(event));
                break;
            case UNINDEX_SITETOOL:
                indexingService.unindexSiteTool(eventHandler.getSiteTool(event));
                break;
            default:
                //TODO: Log that
        }
    }
}
