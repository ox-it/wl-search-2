package uk.ac.ox.oucs.search2.content;

/**
 * Generate {@link Content} elements based on a reference
 *
 * @author Colin Hebert
 */
public interface ContentProducer {
    /**
     * Create the appropriate {@link Content} for the given reference
     *
     * @param reference Reference for a specific element in sakai
     * @return a content object containing the actual data and metadata associated
     */
    Content getContent(String reference);

    /**
     * Check if the given reference can be read by the current producer
     *
     * @param reference Reference to check
     * @return true if the content is handled by the current ContentProducer, false otherwise
     */
    boolean isHandled(String reference);

    /**
     * Get the readability of some content by the current user
     *
     * @param reference Content to check
     * @return true if the current user can read the content, false otherwise
     */
    boolean isReadable(String reference);
}
