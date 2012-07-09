package uk.ac.ox.oucs.search2.solr;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.oucs.search2.AbstractIndexingService;
import uk.ac.ox.oucs.search2.content.Content;

import java.io.IOException;

/**
 * @author Colin Hebert
 */
public class SolrIndexingService extends AbstractIndexingService {
    private final SolrServer solrServer;
    private static final Logger logger = LoggerFactory.getLogger(AbstractIndexingService.class);

    public SolrIndexingService(SolrServer solrServer) {
        this.solrServer = solrServer;
    }

    @Override
    public void indexContent(String eventHandlerName, Iterable<Content> content) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void unindexContent(String eventHandlerName, Iterable<Content> contents) {
        try {
            UpdateRequest unindexRequest = new UpdateRequest();
            for (Content content : contents) {
                unindexRequest.deleteById(content.getId());
            }
            solrServer.request(unindexRequest);
            solrServer.commit();
        } catch (IOException e) {
            logger.warn("Couldn't execute the request", e);
        } catch (SolrServerException e) {
            logger.error("Can't contact the search server", e);
        }
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
