package uk.ac.ox.oucs.search2.backwardcompatibility.event;

import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.api.NotificationService;
import org.sakaiproject.search.api.EntityContentProducer;
import org.sakaiproject.search.api.SearchIndexBuilder;
import org.sakaiproject.search.model.SearchBuilderItem;
import uk.ac.ox.oucs.search2.content.Content;
import uk.ac.ox.oucs.search2.content.ReaderContent;
import uk.ac.ox.oucs.search2.content.StringContent;
import uk.ac.ox.oucs.search2.event.AbstractIndexEventManager;
import uk.ac.ox.oucs.search2.event.IndexEventHandler;

import java.io.Reader;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Colin Hebert
 */
public class BackIndexEventManager extends AbstractIndexEventManager {
    private SearchIndexBuilder searchIndexBuilder;
    private final BackAdditionalEventHandler eventHandler;

    public BackIndexEventManager(NotificationService notificationService) {
        super(notificationService);
        eventHandler = new BackAdditionalEventHandler();
        super.addContentEventHandler(eventHandler);
    }

    @Override
    public void addContentEventHandler(IndexEventHandler indexEventHandler) {
        super.addContentEventHandler(indexEventHandler);
        if (indexEventHandler instanceof BackIndexEventHandler)
            searchIndexBuilder.registerEntityContentProducer(((BackIndexEventHandler) indexEventHandler).getEntityContentProducer());
         else
            searchIndexBuilder.registerEntityContentProducer(new BackEventEntityContentProducer(indexEventHandler));
    }

    @Override
    protected void notify(Event event) {
        if (eventHandler.isHandled(event)) {
            switch (eventHandler.getIndexAction(event)) {
                case INDEX_SITE:
                    searchIndexBuilder.rebuildIndex(eventHandler.getSite(event));
                    break;
                case REINDEX_SITE:
                    searchIndexBuilder.refreshIndex(eventHandler.getSite(event));
                    break;
                case INDEX_ALL:
                    searchIndexBuilder.rebuildIndex();
                    break;
                case REINDEX_ALL:
                    searchIndexBuilder.refreshIndex();
                    break;
                default:
                    //TODO: Log
            }
        }
        searchIndexBuilder.addResource(null, event);
    }

    public void setSearchIndexBuilder(SearchIndexBuilder searchIndexBuilder) {
        this.searchIndexBuilder = searchIndexBuilder;
    }

    private class BackEventEntityContentProducer implements EntityContentProducer {
        private final IndexEventHandler indexEventHandler;
        private Event event;

        private BackEventEntityContentProducer(IndexEventHandler indexEventHandler) {
            this.indexEventHandler = indexEventHandler;
        }

        @Override
        public boolean isContentFromReader(String reference) {
            checkReference(reference);
            return getUniqueContent() instanceof ReaderContent;
        }

        @Override
        public Reader getContentReader(String reference) {
            checkReference(reference);
            if (!isContentFromReader(reference))
                throw new UnsupportedOperationException("Can't get a reader from a non ReaderContent");
            return ((ReaderContent) getUniqueContent()).getContent();
        }

        @Override
        public String getContent(String reference) {
            checkReference(reference);
            if (getUniqueContent() instanceof StringContent)
                throw new UnsupportedOperationException("Can't get a String from a non StringContent");
            return ((StringContent) getUniqueContent()).getContent();
        }

        @Override
        public String getTitle(String reference) {
            checkReference(reference);
            return getUniqueContent().getTitle();
        }

        @Override
        public String getUrl(String reference) {
            checkReference(reference);
            return getUniqueContent().getUrl();
        }

        @Override
        public boolean matches(String reference) {
            return false;
        }

        @Override
        public Integer getAction(Event event) {
            switch (indexEventHandler.getIndexAction(event)) {
                case INDEX_FILE:
                    return SearchBuilderItem.ACTION_ADD;
                case UNINDEX_FILE:
                    return SearchBuilderItem.ACTION_DELETE;
                default:
                    // The wrapper can't understand operations such as site indexation or tool indexation
                    // The previous implementation was only able to handle one file at the time
                    return SearchBuilderItem.ACTION_UNKNOWN;
            }
        }

        @Override
        public boolean matches(Event event) {
            if (indexEventHandler.isHandled(event)) {
                this.event = event;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public String getTool() {
            if (event == null)
                return "Unknown";
            else
                return getUniqueContent().getTool();
        }

        @Override
        public String getSiteId(String reference) {
            checkReference(reference);
            return getUniqueContent().getSiteId();
        }

        @Override
        public Iterator<String> getSiteContentIterator(String context) {
            return Collections.<String>emptyList().iterator();
        }

        @Override
        public boolean isForIndex(String reference) {
            checkReference(reference);
            // Content matching this entity content producer are for index by nature.
            return true;
        }

        @Override
        public boolean canRead(String reference) {
            //This method should be only used when the user get the result back (so not during an event)
            return false;
        }

        @Override
        public Map<String, ?> getCustomProperties(String ref) {
            checkReference(ref);
            return getUniqueContent().getProperties();
        }

        @Override
        public String getCustomRDF(String ref) {
            return null;
        }

        @Override
        public String getId(String ref) {
            checkReference(ref);
            return getUniqueContent().getId();
        }

        private Content getUniqueContent() {
            return indexEventHandler.getContent(event).iterator().next();
        }

        @Override
        public String getType(String ref) {
            checkReference(ref);
            return getUniqueContent().getType();
        }

        @Override
        public String getSubType(String ref) {
            checkReference(ref);
            return getUniqueContent().getSubtype();
        }

        @Override
        public String getContainer(String ref) {
            checkReference(ref);
            return getUniqueContent().getContainer();
        }

        private void checkReference(String ref) {
            if (event == null)
                throw new IllegalStateException("Trying to use a fake EntityContentProducer without checking if the event is supported first!");
            if (event.getResource() == null)
                throw new IllegalStateException("An event with no resource has been sent, impossible to use that in a fake entityContentProvider");
            if (!event.getResource().equals(ref))
                throw new IllegalArgumentException("Trying to use a reference which hasn't been verified against the fake entity content producer");
        }
    }
}
