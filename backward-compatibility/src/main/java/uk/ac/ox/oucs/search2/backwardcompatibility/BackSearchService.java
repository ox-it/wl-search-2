package uk.ac.ox.oucs.search2.backwardcompatibility;

import org.sakaiproject.search.api.InvalidSearchQueryException;
import org.sakaiproject.search.api.SearchList;
import org.sakaiproject.search.api.SearchService;
import uk.ac.ox.oucs.search2.AbstractSearchService;
import uk.ac.ox.oucs.search2.backwardcompatibility.result.BackSearchResultList;
import uk.ac.ox.oucs.search2.filter.SearchFilter;
import uk.ac.ox.oucs.search2.result.SearchResultList;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Colin Hebert
 */
public class BackSearchService extends AbstractSearchService {
    private SearchService searchService;

    @Override
    protected SearchResultList search(String searchQuery, Collection<String> contexts, int start, int length, Iterable<SearchFilter> filterChain) {
        try {
            SearchList searchList = searchService.search(searchQuery, new ArrayList<String>(contexts), start, start+length, null, null);
            return new BackSearchResultList(searchList, filterChain);
        } catch (InvalidSearchQueryException e) {
            //TODO: Log that
            return null;
        }
    }

    @Override
    public long getIndexSize() {
        return searchService.getNDocs();
    }

    @Override
    public String getSuggestion(String searchString) {
        return searchService.getSearchSuggestion(searchString);
    }

    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }
}
