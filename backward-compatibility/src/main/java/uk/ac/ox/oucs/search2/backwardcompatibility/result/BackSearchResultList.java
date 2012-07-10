package uk.ac.ox.oucs.search2.backwardcompatibility.result;

import org.sakaiproject.search.api.SearchList;
import org.sakaiproject.search.api.TermFrequency;
import uk.ac.ox.oucs.search2.filter.FilterChain;
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
public class BackSearchResultList extends AbstractSearchResultList<SearchList> {
    private long numberResultsFound;
    private long startCurrentSelection;

    private Map<String, Long> termFrequency;

    public BackSearchResultList(SearchList searchList) {
        super(searchList);
        numberResultsFound = searchList.getFullSize();
        startCurrentSelection = searchList.getStart();
    }

    public BackSearchResultList(SearchList searchList, Iterable<SearchFilter> searchFilters) {
        super(searchList, searchFilters);
        numberResultsFound = searchList.getFullSize();
        startCurrentSelection = searchList.getStart();
    }

    /**
     * {@inheritDoc}
     * Extract the term frequencies at the same time that result are extracted. This avoids to do another loop on each result
     * @param result
     * @param filters
     * @return
     */
    @Override
    protected List<? extends SearchResult> getSearchResults(SearchList result, Iterable<SearchFilter> filters) {
        termFrequency = new HashMap<String, Long>();
        List<SearchResult> searchResults = new ArrayList<SearchResult>(result.size());
        for (org.sakaiproject.search.api.SearchResult originalSearchResult : result) {
            SearchResult searchResult = new FilterChain(filters).filter(new BackSearchResult(originalSearchResult));
            searchResults.add(searchResult);
            if (!searchResult.isCensored()) {
                try {
                    extractResultTermFrequencies(originalSearchResult.getTerms());
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

        }
        return searchResults;
    }

    @Override
    public long getNumberResultsFound() {
        return numberResultsFound;
    }

    @Override
    public long getStartCurrentSelection() {
        return startCurrentSelection;
    }

    @Override
    public String getSuggestion() {
        return null;
    }

    @Override
    public Map<String, Long> getTermFrequencies() {
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
}
