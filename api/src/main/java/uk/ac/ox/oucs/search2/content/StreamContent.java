package uk.ac.ox.oucs.search2.content;

import java.io.InputStream;

/**
 * @author Colin Hebert
 */
public interface StreamContent extends Content {
    InputStream getContent();
}
