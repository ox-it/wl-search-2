package uk.ac.ox.oucs.search2;

import uk.ac.ox.oucs.search2.content.ContentProducer;

import java.util.Collection;

/**
 * @author Colin Hebert
 */
public class DefaultContentProducerRegistry implements ContentProducerRegistry {
    private Collection<ContentProducer> contentProducers;

    @Override
    public void registerContentProducer(ContentProducer contentProducer) {
        contentProducers.add(contentProducer);
    }

    @Override
    public ContentProducer getContentProducer(String reference) {
        for (ContentProducer contentProducer : contentProducers) {
            if (contentProducer.isHandled(reference))
                return contentProducer;
        }
        //TODO: Log that
        return null;
    }
}
