package uk.ac.ox.oucs.search2.content;

import java.util.Collection;
import java.util.Map;

/**
 * Data indexed or about to be indexed
 * <p>
 * Basic Content only contains metadata, use a sub interface ({@link ReaderContent}, {@link StringContent} or {@link StreamContent}
 * to get the actual data in its appropriate format
 * </p>
 *
 * @author Colin Hebert
 */
public interface Content {
    /**
     * Unique identifier for the content
     */
    String getId();

    /**
     * Title of the indexed content
     */
    String getTitle();

    /**
     * URL referencing the content
     */
    String getUrl();

    /**
     * State of the URL provided by {@link #getUrl()}
     *
     * @return true if the URL is a portal URL (internal), false otherwise
     */
    boolean isPortalUrl();

    /**
     * Type of the content
     */
    String getType();

    /**
     * Subtype for a more accurate description
     */
    String getSubtype();

    /**
     * Tool in which the content is available
     */
    String getTool();

    /**
     * Site hosting the content
     */
    String getSiteId();

    /**
     * Reference of the content
     */
    String getReference();

    /**
     * Container of the content
     */
    String getContainer();

    /**
     * Additional properties of the indexed content
     */
    Map<String, Collection<String>> getProperties();
}
