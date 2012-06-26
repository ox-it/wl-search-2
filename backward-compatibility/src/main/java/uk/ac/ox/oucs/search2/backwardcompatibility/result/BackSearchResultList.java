package uk.ac.ox.oucs.search2.backwardcompatibility.result;

import org.sakaiproject.search.api.SearchList;
import org.sakaiproject.search.api.TermFrequency;
import uk.ac.ox.oucs.search2.filter.SearchFilter;
import uk.ac.ox.oucs.search2.result.AbstractSearchResultList;
import uk.ac.ox.oucs.search2.result.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Colin Hebert
 */
public class BackSearchResultList extends AbstractSearchResultList {
    private SearchList searchList;
    private Map<String, Long> termFrequency;

    public BackSearchResultList(SearchList searchList) {
        super(convertSearchList(searchList));
        this.searchList = searchList;
    }

    public BackSearchResultList(SearchList searchList, Iterable<SearchFilter> searchFilters) {
        super(convertSearchList(searchList), searchFilters);
        this.searchList = searchList;
    }

    @Override
    public long getNumberResultsFound() {
        return searchList.getFullSize();
    }

    @Override
    public long getStartCurrentSelection() {
        return searchList.getStart();
    }

    @Override
    public String getSuggestion() {
        return null;
    }

    @Override
    public Map<String, Long> getTermFrequencies() {
        if (termFrequency == null) {
            termFrequency = new HashMap<String, Long>();
            for (org.sakaiproject.search.api.SearchResult searchResult : searchList) {
                try {
                    extractResultTermFrequencies(searchResult.getTerms());
                } catch (IOException e) {
                    //Log that
                }
            }
        }
        return termFrequency;
    }

    private void extractResultTermFrequencies(TermFrequency resultTermFrequency) {
        String[] terms = resultTermFrequency.getTerms();
        int[] frequencies = resultTermFrequency.getFrequencies();
        for (int i = 0, termsLength = terms.length; i < termsLength; i++) {
            Long currentFrequency = termFrequency.get(terms[i]);

            if (currentFrequency == null)
                currentFrequency = 0L;

            currentFrequency += frequencies[i];
            termFrequency.put(terms[i], currentFrequency);
        }
    }

    public static List<SearchResult> convertSearchList(SearchList searchList) {
        List<SearchResult> searchResults = new ArrayList<SearchResult>(searchList.size());
        for (org.sakaiproject.search.api.SearchResult searchResult : searchList) {
            searchResults.add(new BackSearchResult(searchResult));
        }
        return searchResults;
    }
}
