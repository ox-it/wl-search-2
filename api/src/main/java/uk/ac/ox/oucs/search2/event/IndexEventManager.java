package uk.ac.ox.oucs.search2.event;

import org.sakaiproject.event.api.NotificationAction;

/**
 * @author Colin Hebert
 */
public interface IndexEventManager extends NotificationAction {
    void addContentEventHandler(IndexEventHandler indexEventHandler);
}
