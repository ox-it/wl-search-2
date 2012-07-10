package uk.ac.ox.oucs.search2.solr.result;

import uk.ac.ox.oucs.search2.content.Content;
import uk.ac.ox.oucs.search2.result.AbstractSearchResult;

/**
 * @author Colin Hebert
 */
public class SolrSearchResult extends AbstractSearchResult {
    private final Content content;
    private final double score;
    private final long index;
    private final String displayedText;

    public SolrSearchResult(Content content, double score, long index, String displayedText) {
        this.content = content;
        this.score = score;
        this.index = index;
        this.displayedText = displayedText;
    }

    @Override
    public Content getContent() {
        return content;
    }

    @Override
    public double getScore() {
        return score;
    }

    @Override
    public long getIndex() {
        return index;
    }

    @Override
    public String getDisplayedText() {
        return displayedText;
    }
}
