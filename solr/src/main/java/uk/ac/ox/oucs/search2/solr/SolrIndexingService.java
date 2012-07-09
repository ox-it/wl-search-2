package uk.ac.ox.oucs.search2.solr;

import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.oucs.search2.AbstractIndexingService;
import uk.ac.ox.oucs.search2.content.Content;
import uk.ac.ox.oucs.search2.content.ReaderContent;
import uk.ac.ox.oucs.search2.content.StreamContent;
import uk.ac.ox.oucs.search2.content.StringContent;

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
    public void indexContent(String eventHandlerName, Iterable<Content> contents) {
        try {
            for (Content content : contents) {
                SolrRequest indexRequest;

                SolrInputDocument document = new SolrInputDocument();
                document.addField(SolrSchemaConstants.ID_FIELD, content.getId());
                document.addField(SolrSchemaConstants.TITLE_FIELD, content.getTitle());
                document.addField(SolrSchemaConstants.REFERENCE_FIELD, content.getReference());
                document.addField(SolrSchemaConstants.SITEID_FIELD, content.getSiteId());
                document.addField(SolrSchemaConstants.TOOL_FIELD, content.getTool());
                document.addField(SolrSchemaConstants.CONTAINER_FIELD, content.getContainer());
                document.addField(SolrSchemaConstants.TYPE_FIELD, content.getType());
                document.addField(SolrSchemaConstants.SUBTYPE_FIELD, content.getSubtype());
                document.addField(SolrSchemaConstants.EVENTHANDLER_FIELD, eventHandlerName);
                document.addField(SolrSchemaConstants.TIMESTAMP_FIELD, System.currentTimeMillis());

                if (content instanceof StreamContent) {
                    indexRequest=null;
                } else if (content instanceof ReaderContent) {
                    document.addField(SolrSchemaConstants.CONTENT_FIELD, ((ReaderContent) content).getContent());
                    //indexRequest = new ReaderUpdateRequest().add(document);
                    indexRequest=null;
                } else if (content instanceof StringContent) {
                    document.addField(SolrSchemaConstants.CONTENT_FIELD, ((StringContent) content).getContent());
                   indexRequest = new UpdateRequest().add(document);
                } else {
                    //TODO: Log/exception??
                    continue;
                }
                solrServer.request(indexRequest);

            }
            solrServer.commit();
        } catch (IOException e) {
            logger.warn("Couldn't execute the request", e);
        } catch (SolrServerException e) {
            logger.error("Can't contact the search server", e);
        }
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
    public void indexSite(String eventHandlerName, Iterable<Content> contents, String site) {
        indexContent(eventHandlerName, contents);
    }

    @Override
    public void unindexSite(String eventHandlerName, String site) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void indexAll(String eventHandlerName, Iterable<Content> contents) {
        indexContent(eventHandlerName, contents);
    }

    @Override
    public void unindexAll(String eventHandlerName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
