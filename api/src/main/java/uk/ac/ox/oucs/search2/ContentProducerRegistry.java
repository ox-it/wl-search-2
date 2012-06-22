package uk.ac.ox.oucs.search2;

import uk.ac.ox.oucs.search2.content.ContentProducer;

/**
 * @author Colin Hebert
 */
public interface ContentProducerRegistry {
    void registerContentProducer(ContentProducer contentProducer);

    ContentProducer getContentProducer(String reference);
}
