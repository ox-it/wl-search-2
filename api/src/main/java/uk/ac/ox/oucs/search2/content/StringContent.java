package uk.ac.ox.oucs.search2.content;

/**
 * Content for which the data is accessible through a {@link String}
 *
 * @author Colin Hebert
 */
public interface StringContent extends Content {
    /**
     * Get the actual data of the current element
     */
    String getContent();
}
