package uk.ac.ox.oucs.search2.event;

import org.sakaiproject.event.api.*;
import org.w3c.dom.Element;

/**
 * @author Colin Hebert
 */
public abstract class IndexEventManager implements NotificationAction {
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
        notify(event);
    }

    protected abstract void notify(Event event);
}
