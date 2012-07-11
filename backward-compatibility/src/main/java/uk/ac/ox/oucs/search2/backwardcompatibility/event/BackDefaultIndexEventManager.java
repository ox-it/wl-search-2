package uk.ac.ox.oucs.search2.backwardcompatibility.event;

import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.api.NotificationService;
import uk.ac.ox.oucs.search2.event.DefaultIndexEventManager;
import uk.ac.ox.oucs.search2.event.IndexEventHandler;

import java.util.ArrayList;
import java.util.Collection;

/**
 * IndexEventManager handling events for the previous SearchAPI events
 * <p>
 * As the previous search API can't associate an {@link Event} code to am {@link org.sakaiproject.search.api.EntityContentProducer}
 * this IndexEventManager will consider every {@link IndexEventHandler} without supported event types as handlers
 * applying for every handled event.
 * </p>
 *
 * @author Colin Hebert
 */
public class BackDefaultIndexEventManager extends DefaultIndexEventManager {
    /**
     * Collection of {@link IndexEventHandler} without supported events
     */
    private final Collection<IndexEventHandler> indexEventHandlers;

    public BackDefaultIndexEventManager(NotificationService notificationService) {
        super(notificationService);
        indexEventHandlers = new ArrayList<IndexEventHandler>();
    }

    @Override
    public void addContentEventHandler(IndexEventHandler indexEventHandler) {
        super.addContentEventHandler(indexEventHandler);
        if (indexEventHandler.getSupportedEventTypes().size() <= 0) {
            indexEventHandlers.add(indexEventHandler);
        }
    }

    @Override
    protected void notify(Event event) {
        super.notify(event);
        for (IndexEventHandler eventHandler : indexEventHandlers) {
            handleEvent(event, eventHandler);
        }
    }
}
