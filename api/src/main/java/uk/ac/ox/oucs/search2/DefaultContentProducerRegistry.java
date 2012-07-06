package uk.ac.ox.oucs.search2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ox.oucs.search2.content.ContentProducer;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Colin Hebert
 */
public class DefaultContentProducerRegistry implements ContentProducerRegistry {
    private final static Log logger = LogFactory.getLog(DefaultContentProducerRegistry.class);
    private Collection<ContentProducer> contentProducers = new ArrayList<ContentProducer>();

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
        logger.warn("Couldn't find a content producer for the reference '" + reference + "'.");
        return null;
    }
}
