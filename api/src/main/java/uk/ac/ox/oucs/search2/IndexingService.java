package uk.ac.ox.oucs.search2;

import uk.ac.ox.oucs.search2.content.Content;

/**
 * @author Colin Hebert
 */
public interface IndexingService {
    void indexContent(String eventHandlerName, Iterable<Content> content);

    void unindexContent(String eventHandlerName, Iterable<Content> content);

    void indexSite(String eventHandlerName, Iterable<Content> content, String site);

    void reindexSite(String eventHandlerName, Iterable<Content> content, String site);

    void unindexSite(String eventHandlerName, String site);

    void indexAll(String eventHandlerName, Iterable<Content> content);

    void reindexAll(String eventHandlerName, Iterable<Content> content);

    void unindexAll(String eventHandlerName);
}
