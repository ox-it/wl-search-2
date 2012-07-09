package uk.ac.ox.oucs.search2;

import uk.ac.ox.oucs.search2.content.Content;

/**
 * @author Colin Hebert
 */
public interface IndexingService {
    void indexContent(String eventHandlerName, Iterable<Content> contents);

    void unindexContent(String eventHandlerName, Iterable<Content> contents);

    void indexSite(String eventHandlerName, Iterable<Content> contents, String site);

    void reindexSite(String eventHandlerName, Iterable<Content> contents, String site);

    void unindexSite(String eventHandlerName, String site);

    void indexAll(String eventHandlerName, Iterable<Content> contents);

    void reindexAll(String eventHandlerName, Iterable<Content> contents);

    void unindexAll(String eventHandlerName);
}
