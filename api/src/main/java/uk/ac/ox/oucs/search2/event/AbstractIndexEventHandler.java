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
        IndexAction indexAction = getIndexAction(event);
        String reference = event.getResource();

        switch (indexAction) {
            case INDEX_FILE:
            case UNINDEX_FILE:
                return Collections.singleton(contentProducer.getContent(reference));
            case INDEX_SITE:
            case REINDEX_SITE:
            case UNINDEX_SITE:
                return getSiteContent(reference);
            case INDEX_SITETOOL:
            case REINDEX_SITETOOL:
            case UNINDEX_SITETOOL:
                return getSiteToolContent(reference);
            case INDEX_ALL:
            case REINDEX_ALL:
            case UNINDEX_ALL:
                return getAllContent();
            default:
                //TODO: Log
                return Collections.emptyList();
        }
    }

    @Override
    public String getName() {
        return this.getClass().getCanonicalName();
    }

    protected abstract Iterable<Content> getSiteContent(String siteId);

    protected abstract Iterable<Content> getSiteToolContent(String siteToolId);

    protected abstract Iterable<Content> getAllContent();
}
