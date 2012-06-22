package uk.ac.ox.oucs.search2;

import uk.ac.ox.oucs.search2.filter.SearchFilter;
import uk.ac.ox.oucs.search2.result.SearchResultList;

import javax.naming.directory.SearchResult;
import java.util.Collection;

/**
 * @author Colin Hebert
 */
public interface SearchService {
    SearchResultList search(String searchQuery);

    SearchResultList search(String searchQuery, Collection<String> contexts);

    SearchResultList search(String searchQuery, int start, int length);

    SearchResultList search(String searchQuery, Collection<String> contexts, int start, int length);

    long getIndexSize();

    String getSuggestion(String searchString);

    void setSearchFilters(Iterable<SearchFilter> searchFilters);
}