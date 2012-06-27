package uk.ac.ox.oucs.search2.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.oucs.search2.ContentProducerRegistry;
import uk.ac.ox.oucs.search2.content.ContentProducer;
import uk.ac.ox.oucs.search2.result.SearchResult;

/**
 * @author Colin Hebert
 */
public class SecuritySearchFilter implements SearchFilter {
    private static final Logger logger = LoggerFactory.getLogger(FilterChain.class);
    private ContentProducerRegistry contentProducerRegistry;
    private static final SearchResult censoredSearchResult = new SearchResult.CensoredSearchResult();

    @Override
    public SearchResult filter(SearchResult searchResult, FilterChain filterChain) {
        ContentProducer contentProducer = contentProducerRegistry.getContentProducer(searchResult.getContent().getReference());
        if (contentProducer == null) {
            logger.warn("Can't find a content producer for '" + searchResult.getContent() + "'.");
        }

        if (contentProducer == null || !contentProducer.isReadable(searchResult.getContent().getReference())) {
            logger.debug("The result '" + searchResult.getContent().getReference() + "' has been censored.");
            return censoredSearchResult;
        } else {
            return filterChain.filter(searchResult);
        }
    }

    public void setContentProducerRegistry(ContentProducerRegistry contentProducerRegistry) {
        this.contentProducerRegistry = contentProducerRegistry;
    }
}
