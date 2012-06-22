package uk.ac.ox.oucs.search2.content;

import java.io.Reader;

/**
 * @author Colin Hebert
 */
public interface ReaderContent extends Content {
    Reader getContent();
}
