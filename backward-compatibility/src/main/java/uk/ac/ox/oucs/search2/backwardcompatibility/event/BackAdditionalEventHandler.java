package uk.ac.ox.oucs.search2.backwardcompatibility.event;

import org.sakaiproject.event.api.Event;
import uk.ac.ox.oucs.search2.content.Content;
import uk.ac.ox.oucs.search2.event.IndexEventHandler;

import java.util.Arrays;
import java.util.Collection;

/**
 * This implementation MUST NOT be used outside the backward compatible project.
 *
 * @author Colin Hebert
 */
public class BackAdditionalEventHandler implements IndexEventHandler {
    private final static String INDEX_SITE = "search2.index.site";
    private final static String REINDEX_SITE = "search2.reindex.site";
    private final static String INDEX_ALL = "search2.index.all";
    private final static String REINDEX_ALL = "search2.reindex.all";

    @Override
    public Collection<String> getSupportedEventTypes() {
        return Arrays.asList(INDEX_SITE, REINDEX_SITE, INDEX_ALL, REINDEX_ALL);
    }

    @Override
    public IndexAction getIndexAction(Event event) {
        String eventName = event.getEvent();
        if (INDEX_SITE.equals(eventName)) {
            return IndexAction.INDEX_SITE;
        } else if (REINDEX_SITE.equals(eventName)) {
            return IndexAction.REINDEX_SITE;
        } else if (INDEX_ALL.equals(eventName)) {
            return IndexAction.INDEX_ALL;
        } else if (REINDEX_ALL.equals(eventName)) {
            return IndexAction.REINDEX_ALL;
        } else {
            return null;
        }
    }

    @Override
    public Iterable<Content> getContent(Event event) {
        //Doesn't matter
        return null;
    }

    @Override
    public String getSite(Event event) {
        return event.getResource();
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isHandled(Event event) {
        String eventName = event.getEvent();
        return INDEX_SITE.equals(eventName) || REINDEX_SITE.equals(eventName) || INDEX_ALL.equals(eventName) || REINDEX_ALL.equals(eventName);
    }
}
