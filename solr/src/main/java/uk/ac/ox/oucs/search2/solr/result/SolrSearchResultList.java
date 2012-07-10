package uk.ac.ox.oucs.search2.solr.result;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.util.NamedList;
import uk.ac.ox.oucs.search2.content.Content;
import uk.ac.ox.oucs.search2.filter.FilterChain;
import uk.ac.ox.oucs.search2.filter.SearchFilter;
import uk.ac.ox.oucs.search2.result.AbstractSearchResultList;
import uk.ac.ox.oucs.search2.result.SearchResult;
import uk.ac.ox.oucs.search2.solr.SolrSchemaConstants;
import uk.ac.ox.oucs.search2.solr.content.SolrContent;

import java.util.*;

/**
 * @author Colin Hebert
 */
public class SolrSearchResultList extends AbstractSearchResultList<QueryResponse> {
    private long numberResultsFound;
    private long startCurrentSelection;
    private String suggestion;
    private final Map<String, Long> termFrequencies = new HashMap<String, Long>();

    public SolrSearchResultList(QueryResponse queryResponse) {
        this(queryResponse, Collections.<SearchFilter>emptyList());
    }

    public SolrSearchResultList(QueryResponse queryResponse, Iterable<SearchFilter> searchFilters) {
        super(queryResponse, searchFilters);
        numberResultsFound = queryResponse.getResults().getNumFound();
        startCurrentSelection = extractStartCurrentSelection(queryResponse);
        suggestion = extractSuggestion(queryResponse);
    }

    @Override
    protected List<? extends SearchResult> getSearchResults(QueryResponse queryResponse, Iterable<SearchFilter> filters) {
        List<SearchResult> searchResults = new ArrayList<SearchResult>(queryResponse.getResults().size());
        TermVectorExtractor termVectorExtractor = new TermVectorExtractor(queryResponse);
        Map<String, Map<String, Map<String, TermVectorExtractor.TermInfo>>> termsPerDocument = termVectorExtractor.getTermVectorInfo();
        long index = 0;

        for (SolrDocument document : queryResponse.getResults()) {
            String id = (String) document.getFieldValue(SolrSchemaConstants.ID_FIELD);
            SolrSearchResult solrResult = extractResult(index++, document, queryResponse.getHighlighting().get(id));
            SearchResult searchResult = new FilterChain(filters).filter(solrResult);
            searchResults.add(searchResult);

            if (!searchResult.isCensored()) {
                extractDocumentTermFrequencies(termsPerDocument.get(id));
            }

        }
        return searchResults;
    }

    private void extractDocumentTermFrequencies(Map<String, Map<String, TermVectorExtractor.TermInfo>> terms) {
        for (Map.Entry<String, Map<String, TermVectorExtractor.TermInfo>> fieldTerms : terms.entrySet()) {
            for (Map.Entry<String, TermVectorExtractor.TermInfo> term : fieldTerms.getValue().entrySet()) {
                Long frequency = termFrequencies.get(term.getKey());
                if (frequency == null)
                    frequency = term.getValue().getDocumentFrequency();
                else
                    frequency += term.getValue().getDocumentFrequency();
                termFrequencies.put(term.getKey(), frequency);
            }
        }
    }

    private static SolrSearchResult extractResult(long index, SolrDocument document, Map<String, List<String>> highlights) {
        Content content = new SolrContent(document);
        double score = (Double) document.getFieldValue(SolrSchemaConstants.SCORE_FIELD);
        String highlightedText = getText(highlights.get(SolrSchemaConstants.CONTENT_FIELD));
        return new SolrSearchResult(content, score, index, highlightedText);
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
        return suggestion;
    }

    @Override
    public Map<String, Long> getTermFrequencies() {
        return termFrequencies;
    }

    private long extractStartCurrentSelection(QueryResponse queryResponse) {
        String expectedStart = ((NamedList<String>) queryResponse.getHeader().get("params")).get("start");
        return (expectedStart != null) ? Integer.parseInt(expectedStart) : 0;
    }

    private String extractSuggestion(QueryResponse queryResponse) {
        SpellCheckResponse spellCheckResponse = queryResponse.getSpellCheckResponse();
        if (spellCheckResponse == null || !spellCheckResponse.isCorrectlySpelled())
            return null;
        else
            return spellCheckResponse.getCollatedResult();
    }

    private static String getText(Iterable<String> highlights) {
        StringBuilder sb = new StringBuilder();
        for (String highlight : highlights)
            sb.append(highlight).append("... ");
        return sb.toString();
    }
}
