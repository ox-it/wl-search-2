package uk.ac.ox.oucs.search2.solr;

import uk.ac.ox.oucs.search2.AbstractIndexingService;
import uk.ac.ox.oucs.search2.content.Content;

/**
 * @author Colin Hebert
 */
public class SolrIndexingService extends AbstractIndexingService {
    @Override
    public void indexContent(String eventHandlerName, Iterable<Content> content) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void unindexContent(String eventHandlerName, Iterable<Content> content) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void indexSite(String eventHandlerName, Iterable<Content> content, String site) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void unindexSite(String eventHandlerName, String site) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void indexAll(String eventHandlerName, Iterable<Content> content) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void unindexAll(String eventHandlerName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
