package uk.ac.ox.oucs.search2.result;

import uk.ac.ox.oucs.search2.content.Content;

/**
 * @author Colin Hebert
 */
public interface SearchResult {
    Content getContent();

    double getScore();

    long getIndex();

    boolean isCensored();

    String getDisplayedText();

    /**
     * Implementation of {@link SearchResult} used to censor a result.
     */
    public static final class CensoredSearchResult implements SearchResult {
        @Override
        public Content getContent() {
            return null;
        }

        @Override
        public double getScore() {
            return 0;
        }

        @Override
        public long getIndex() {
            return 0;
        }

        @Override
        public boolean isCensored() {
            return true;
        }

        @Override
        public String getDisplayedText() {
            return null;
        }
    }
}
