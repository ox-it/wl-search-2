package uk.ac.ox.oucs.search2.result;

/**
 * @author Colin Hebert
 */
public abstract class AbstractSearchResult implements SearchResult {
    @Override
    public boolean isCensored() {
        return false;
    }
}
