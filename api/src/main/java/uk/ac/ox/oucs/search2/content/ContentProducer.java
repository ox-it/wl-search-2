package uk.ac.ox.oucs.search2.content;

/**
 * @author Colin Hebert
 */
public interface ContentProducer {
    Content getContent(String reference);

    /**
     * Check if the given reference references handled content
     *
     * @param reference Content to check
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
