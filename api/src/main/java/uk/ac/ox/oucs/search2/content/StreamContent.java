package uk.ac.ox.oucs.search2.content;

import java.io.InputStream;

/**
 * Content for which the data is accessible through an {@link InputStream}
 *
 * @author Colin Hebert
 */
public interface StreamContent extends Content {
    /**
     * Get the actual data of the current element
     */
    InputStream getContent();
}
