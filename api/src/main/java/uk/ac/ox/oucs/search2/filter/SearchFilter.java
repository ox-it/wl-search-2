package uk.ac.ox.oucs.search2.filter;

import uk.ac.ox.oucs.search2.result.SearchResult;

/**
 * @author Colin Hebert
 */
public interface SearchFilter {
    SearchResult filter(SearchResult searchResult, FilterChain filterChain);
}
