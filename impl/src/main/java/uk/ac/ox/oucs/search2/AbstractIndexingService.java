package uk.ac.ox.oucs.search2;

import uk.ac.ox.oucs.search2.content.Content;

/**
 * @author Colin Hebert
 */
public abstract class AbstractIndexingService implements IndexingService {
    @Override
    public void reindexSite(String eventHandlerName, Iterable<Content> contents, String site) {
        unindexSite(eventHandlerName, site);
        indexSite(eventHandlerName, contents, site);
    }

    @Override
    public void reindexAll(String eventHandlerName, Iterable<Content> contents) {
        unindexAll(eventHandlerName);
        indexAll(eventHandlerName, contents);
    }
}
