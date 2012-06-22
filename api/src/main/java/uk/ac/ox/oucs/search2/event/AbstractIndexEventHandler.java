package uk.ac.ox.oucs.search2.event;

import org.sakaiproject.event.api.Event;
import uk.ac.ox.oucs.search2.content.Content;
import uk.ac.ox.oucs.search2.content.ContentProducer;

/**
 * @author Colin Hebert
 */
public abstract class AbstractIndexEventHandler implements IndexEventHandler {
    private ContentProducer contentProducer;

    @Override
    public Content getContent(Event event) {
        String reference = event.getResource();
        return contentProducer.getContent(reference);
    }
}
