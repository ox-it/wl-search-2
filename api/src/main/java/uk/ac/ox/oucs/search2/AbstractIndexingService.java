package uk.ac.ox.oucs.search2;

import uk.ac.ox.oucs.search2.content.Content;

/**
 * @author Colin Hebert
 */
public abstract class AbstractIndexingService implements IndexingService {
    @Override
    public void reindexSiteTool(String eventHandlerName, Iterable<Content> content, String siteTool) {
        unindexSiteTool(eventHandlerName, siteTool);
        indexSiteTool(eventHandlerName, content, siteTool);
    }

    @Override
    public void reindexSite(String eventHandlerName, Iterable<Content> content, String site) {
        unindexSite(eventHandlerName, site);
        indexSite(eventHandlerName, content, site);
    }

    @Override
    public void reindexAll(String eventHandlerName, Iterable<Content> content) {
        unindexAll(eventHandlerName);
        indexAll(eventHandlerName, content);
    }
}
