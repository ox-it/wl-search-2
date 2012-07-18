package uk.ac.ox.oucs.search2.backwardcompatibility;

import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.api.Notification;
import org.sakaiproject.search.api.*;
import org.sakaiproject.search.model.SearchBuilderItem;
import uk.ac.ox.oucs.search2.content.ContentProducerRegistry;
import uk.ac.ox.oucs.search2.backwardcompatibility.content.BackContent;
import uk.ac.ox.oucs.search2.backwardcompatibility.event.BackIndexEventHandler;
import uk.ac.ox.oucs.search2.content.Content;
import uk.ac.ox.oucs.search2.content.ContentProducer;
import uk.ac.ox.oucs.search2.event.IndexEventHandler;
import uk.ac.ox.oucs.search2.event.IndexEventManager;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Intercept registered functions and {@link EntityContentProducer} and add them to the {@link IndexEventManager}
 *
 * @author Colin Hebert
 */
public class SearchServiceInterceptor implements SearchService, SearchIndexBuilder {
    private final IndexEventManager indexEventManager;
    private final ContentProducerRegistry contentProducerRegistry;

    public SearchServiceInterceptor(IndexEventManager indexEventManager, ContentProducerRegistry contentProducerRegistry) {
        this.indexEventManager = indexEventManager;
        this.contentProducerRegistry = contentProducerRegistry;
    }

    @Override
    public void registerFunction(final String function) {
        indexEventManager.addContentEventHandler(new IndexEventHandler() {
            @Override
            public Collection<String> getSupportedEventTypes() {
                return Collections.singleton(function);
            }

            @Override
            public IndexAction getIndexAction(Event event) {
                return null;
            }

            @Override
            public Iterable<Content> getContent(Event event) {
                return null;
            }

            @Override
            public String getSite(Event event) {
                return null;
            }

            @Override
            public boolean isHandled(Event event) {
                return false;
            }

            @Override
            public String getName() {
                //Doesn't matter as it won't be used
                return this.getClass().getCanonicalName();
            }
        });
    }

    @Override
    public void registerEntityContentProducer(final EntityContentProducer ecp) {
        indexEventManager.addContentEventHandler(new BackIndexEventHandler(ecp));
        contentProducerRegistry.registerContentProducer(new ContentProducer() {
            @Override
            public Content getContent(final String reference) {
                return BackContent.extractContent(reference, ecp);
            }

            @Override
            public boolean isHandled(String reference) {
                return ecp.matches(reference);
            }

            @Override
            public boolean isReadable(String reference) {
                return ecp.canRead(reference);
            }
        });
    }

    //-------------------------------------------------------------------------------
    //  The following methods aren't relevant for the EntityContentProducer capture
    //-------------------------------------------------------------------------------

    @Override
    public SearchList search(String searchTerms, List<String> contexts, int searchStart, int searchEnd) throws InvalidSearchQueryException {
        return null;
    }

    @Override
    public SearchList search(String searchTerms, List<String> contexts, int start, int end, String filterName, String sorterName) throws InvalidSearchQueryException {
        return null;
    }

    @Override
    public void reload() {
    }

    @Override
    public void refreshInstance() {
    }

    @Override
    public void rebuildInstance() {
    }

    @Override
    public void refreshSite(String currentSiteId) {
    }

    @Override
    public void rebuildSite(String currentSiteId) {
    }

    @Override
    public String getStatus() {
        return null;
    }

    @Override
    public int getNDocs() {
        return 0;
    }

    @Override
    public int getPendingDocs() {
        return 0;
    }

    @Override
    public void addResource(Notification notification, Event event) {
    }

    @Override
    public void refreshIndex() {
    }

    @Override
    public void rebuildIndex() {
    }

    @Override
    public boolean isBuildQueueEmpty() {
        return false;
    }

    @Override
    public List<EntityContentProducer> getContentProducers() {
        return Collections.emptyList();
    }

    @Override
    public void destroy() {
    }

    @Override
    public int getPendingDocuments() {
        return 0;
    }

    @Override
    public void rebuildIndex(String currentSiteId) {
    }

    @Override
    public void refreshIndex(String currentSiteId) {
    }

    @Override
    public List<SearchBuilderItem> getAllSearchItems() {
        return Collections.emptyList();
    }

    @Override
    public EntityContentProducer newEntityContentProducer(Event event) {
        return null;
    }

    @Override
    public EntityContentProducer newEntityContentProducer(String ref) {
        return null;
    }

    @Override
    public List<SearchBuilderItem> getSiteMasterSearchItems() {
        return Collections.emptyList();
    }

    @Override
    public List<SearchBuilderItem> getGlobalMasterSearchItems() {
        return Collections.emptyList();
    }

    @Override
    public boolean isOnlyIndexSearchToolSites() {
        return false;
    }

    @Override
    public boolean isExcludeUserSites() {
        return false;
    }

    @Override
    public SearchStatus getSearchStatus() {
        return new SearchStatus() {
            @Override
            public String getLastLoad() {
                return null;
            }

            @Override
            public String getLoadTime() {
                return null;
            }

            @Override
            public String getCurrentWorker() {
                return null;
            }

            @Override
            public String getCurrentWorkerETC() {
                return null;
            }

            @Override
            public List getWorkerNodes() {
                return Collections.emptyList();
            }

            @Override
            public String getNDocuments() {
                return null;
            }

            @Override
            public String getPDocuments() {
                return null;
            }
        };
    }

    @Override
    public boolean removeWorkerLock() {
        return false;
    }

    @Override
    public List getSegmentInfo() {
        return Collections.emptyList();
    }

    @Override
    public void forceReload() {
    }

    @Override
    public TermFrequency getTerms(int documentId) throws IOException {
        return null;
    }

    @Override
    public String searchXML(Map parameterMap) {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getDigestStoragePath() {
        return null;
    }

    @Override
    public String getSearchSuggestion(String searchString) {
        return null;
    }

    @Override
    public void enableDiagnostics() {
    }

    @Override
    public void disableDiagnostics() {
    }

    @Override
    public boolean hasDiagnostics() {
        return false;
    }
}
