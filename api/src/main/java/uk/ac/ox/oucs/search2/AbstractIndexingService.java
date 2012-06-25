package uk.ac.ox.oucs.search2;

import uk.ac.ox.oucs.search2.content.Content;

/**
 * @author Colin Hebert
 */
public abstract class AbstractIndexingService implements IndexingService {
    @Override
    public void reindexContent(Content content) {
        unindexContent(content);
        indexContent(content);
    }

    @Override
    public void reindexSite(String siteId) {
        unindexSite(siteId);
        indexSite(siteId);
    }

    @Override
    public void reindexSiteTool(String siteTool) {
        unindexSiteTool(siteTool);
        indexSiteTool(siteTool);
    }

    @Override
    public void reindexEverything() {
        unindexEverything();
        indexEverything();
    }
}
