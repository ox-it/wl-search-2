package uk.ac.ox.oucs.search2.content;

import java.io.Reader;

/**
 * Content for which the data is accessible through a {@link Reader}
 *
 * @author Colin Hebert
 */
public interface ReaderContent extends Content {
    /**
     * Get the actual data of the current element
     */
    Reader getContent();
}
