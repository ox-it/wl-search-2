package uk.ac.ox.oucs.search2;

import uk.ac.ox.oucs.search2.filter.SearchFilter;
import uk.ac.ox.oucs.search2.result.SearchResultList;

import java.util.Collection;

/**
 * @author Colin Hebert
 */
public interface SearchService {
    SearchResultList search(String searchQuery);

    SearchResultList search(String searchQuery, Collection<String> contexts);

    SearchResultList search(String searchQuery, SearchContext context);

    SearchResultList search(String searchQuery, long start, long length);

    SearchResultList search(String searchQuery, Collection<String> contexts, long start, long length);

    SearchResultList search(String searchQuery, SearchContext context, long start, long length);

    String getSuggestion(String searchString);

    void setSearchFilters(Iterable<SearchFilter> searchFilters);

    public enum SearchContext {
        @Deprecated
        CURRENT_SITE,
        SUBSCRIBED_SITES,
        ALL_SITES
    }
}
