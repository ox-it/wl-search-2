package uk.ac.ox.oucs.search2.content;

/**
 * Class to which every {@link ContentProducer} has to register.
 *
 * This allows to obtain a {@link ContentProducer} for a specific reference
 *
 * @author Colin Hebert
 */
public interface ContentProducerRegistry {
    /**
     * Register a new {@link ContentProducer}
     * @param contentProducer contentProducer to register
     */
    void registerContentProducer(ContentProducer contentProducer);

    /**
     * Obtain the content producer able to generate a new {@link Content} for the given reference
     * @param reference reference to an element
     * @return the first contentProducer able to handle the given reference
     */
    ContentProducer getContentProducer(String reference);
}
