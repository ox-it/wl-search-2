package uk.ac.ox.oucs.search2.result;

import java.util.List;
import java.util.Map;

/**
 * @author Colin Hebert
 */
public interface SearchResultList extends List<SearchResult> {
    long getNumberResultsFound();

    /**
     * Get the number of the first result in the current selection
     *
     * @return
     */
    long getStartCurrentSelection();

    /**
     * Get a suggested alternative search
     *
     * @return a suggestion for another search query, null if none if found
     */
    String getSuggestion();

    Map<String, Long> getTermFrequencies();
}
