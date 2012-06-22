package uk.ac.ox.oucs.search2.filter;

import uk.ac.ox.oucs.search2.ContentProducerRegistry;
import uk.ac.ox.oucs.search2.content.ContentProducer;
import uk.ac.ox.oucs.search2.result.SearchResult;

/**
 * @author Colin Hebert
 */
public class SecuritySearchFilter implements SearchFilter {
    private ContentProducerRegistry contentProducerRegistry;
    private static final SearchResult censoredSearchResult = new SearchResult.CensoredSearchResult();

    @Override
    public SearchResult filter(SearchResult searchResult, FilterChain filterChain) {
        ContentProducer contentProducer = contentProducerRegistry.getContentProducer(searchResult.getContent().getReference());
        if (contentProducer == null) {
            //TODO: Log that!
        }

        if (contentProducer == null || !contentProducer.isReadable(searchResult.getContent().getReference()))
            return censoredSearchResult;
        else
            return filterChain.filter(searchResult);
    }

    public void setContentProducerRegistry(ContentProducerRegistry contentProducerRegistry) {
        this.contentProducerRegistry = contentProducerRegistry;
    }
}
