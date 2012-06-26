package uk.ac.ox.oucs.search2.backwardcompatibility.result;


import uk.ac.ox.oucs.search2.backwardcompatibility.content.ResultBackContent;
import uk.ac.ox.oucs.search2.content.Content;
import uk.ac.ox.oucs.search2.result.SearchResult;

/**
 * @author Colin Hebert
 */
public class BackSearchResult implements SearchResult {
    private final org.sakaiproject.search.api.SearchResult searchResult;
    private final Content content;

    public BackSearchResult(org.sakaiproject.search.api.SearchResult searchResult) {
        this.searchResult = searchResult;
        content = new ResultBackContent(searchResult);
    }

    @Override
    public Content getContent() {
        return content;
    }

    @Override
    public double getScore() {
        return searchResult.getScore();
    }

    @Override
    public long getIndex() {
        return searchResult.getIndex();
    }

    @Override
    public boolean isCensored() {
        return searchResult.isCensored();
    }

    @Override
    public String getDisplayedText() {
        return searchResult.getSearchResult();
    }
}
