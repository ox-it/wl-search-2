package uk.ac.ox.oucs.search2.event;

import org.sakaiproject.event.api.Event;
import uk.ac.ox.oucs.search2.content.Content;
import uk.ac.ox.oucs.search2.content.ContentProducer;

import java.util.Collections;

/**
 * @author Colin Hebert
 */
public abstract class AbstractIndexEventHandler implements IndexEventHandler {
    private ContentProducer contentProducer;

    protected AbstractIndexEventHandler(ContentProducer contentProducer) {
        this.contentProducer = contentProducer;
    }

    @Override
    public Iterable<Content> getContent(Event event) {
        String reference = event.getResource();
        return Collections.singleton(contentProducer.getContent(reference));
    }

    @Override
    public String getName() {
        return this.getClass().getCanonicalName();
    }
}
