package uk.ac.ox.oucs.search2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.oucs.search2.content.ContentProducer;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Colin Hebert
 */
public class DefaultContentProducerRegistry implements ContentProducerRegistry {
    private static final Logger logger = LoggerFactory.getLogger(DefaultContentProducerRegistry.class);
    private Collection<ContentProducer> contentProducers = new ArrayList<ContentProducer>();

    @Override
    public void registerContentProducer(ContentProducer contentProducer) {
        contentProducers.add(contentProducer);
    }

    @Override
    public ContentProducer getContentProducer(String reference) {
        for (ContentProducer contentProducer : contentProducers) {
            try {
                if (contentProducer.isHandled(reference))
                    return contentProducer;
            } catch (Exception e) {
                logger.warn("The content producer '" + contentProducer.getClass() + "' has thrown an exception", e);
            }
        }
        logger.warn("Couldn't find a content producer for the reference '" + reference + "'.");
        return null;
    }
}
