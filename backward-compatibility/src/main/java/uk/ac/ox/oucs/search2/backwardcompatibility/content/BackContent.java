package uk.ac.ox.oucs.search2.backwardcompatibility.content;

import org.sakaiproject.search.api.EntityContentProducer;
import org.sakaiproject.search.api.PortalUrlEnabledProducer;
import uk.ac.ox.oucs.search2.content.Content;
import uk.ac.ox.oucs.search2.content.ReaderContent;
import uk.ac.ox.oucs.search2.content.StringContent;

import java.io.Reader;
import java.util.*;

/**
 * @author Colin Hebert
 */
public abstract class BackContent implements Content {
    private final String reference;
    private final EntityContentProducer entityContentProducer;
    private Map<String, Collection<String>> properties;

    public BackContent(String reference, EntityContentProducer entityContentProducer) {
        this.reference = reference;
        this.entityContentProducer = entityContentProducer;
    }

    @Override
    public String getTitle() {
        return entityContentProducer.getTitle(reference);
    }

    @Override
    public String getUrl() {
        return entityContentProducer.getUrl(reference);
    }

    @Override
    public boolean isPortalUrl() {
        return entityContentProducer instanceof PortalUrlEnabledProducer;
    }

    @Override
    public String getType() {
        return entityContentProducer.getType(reference);
    }

    @Override
    public String getTool() {
        return entityContentProducer.getTool();
    }

    @Override
    public String getSubtype() {
        return entityContentProducer.getSubType(reference);
    }

    @Override
    public String getSiteId() {
        return entityContentProducer.getSiteId(reference);
    }

    @Override
    public String getReference() {
        return reference;
    }

    @Override
    public String getContainer() {
        return entityContentProducer.getContainer(reference);
    }

    @Override
    public String getId() {
        return entityContentProducer.getId(reference);
    }

    @Override
    public Map<String, Collection<String>> getProperties() {
        if (properties == null)
            extractProperties();
        return properties;
    }

    private void extractProperties() {
        Map<String, ?> m = entityContentProducer.getCustomProperties(reference);

        if (m == null) {
            properties = Collections.emptyMap();
            return;
        }

        properties = new HashMap<String, Collection<String>>(m.size());
        for (Map.Entry<String, ?> propertyEntry : m.entrySet()) {
            String propertyName = propertyEntry.getKey();
            Object propertyValue = propertyEntry.getValue();
            Collection<String> values;

            //Check for basic data type that could be provided by the EntityContentProducer
            //If the data type can't be defined, nothing is stored. The toString method could be called, but some values
            //could be not meant to be indexed.
            if (propertyValue instanceof String)
                values = Collections.singleton((String) propertyValue);
            else if (propertyValue instanceof String[])
                values = Arrays.asList((String[]) propertyValue);
            else if (propertyValue instanceof Collection)
                values = (Collection<String>) propertyValue;
            else {
                //TODO: Log
                //if (propertyValue != null)
                //    logger.warn("Couldn't find what the value for '" + propertyName + "' was. It has been ignored. " + propertyName.getClass());
                values = Collections.emptyList();
            }

            properties.put(propertyName, values);
        }
    }

    public static Content extractContent(String reference, EntityContentProducer entityContentProducer) {
        if (entityContentProducer.isContentFromReader(reference))
            return new BackReaderContent(reference, entityContentProducer);
        else
            return new BackStringContent(reference, entityContentProducer);
    }

    public static class BackReaderContent extends BackContent implements ReaderContent {

        public BackReaderContent(String reference, EntityContentProducer entityContentProducer) {
            super(reference, entityContentProducer);
        }

        @Override
        public Reader getContent() {
            return super.entityContentProducer.getContentReader(super.reference);
        }
    }

    public static class BackStringContent extends BackContent implements StringContent {

        public BackStringContent(String reference, EntityContentProducer entityContentProducer) {
            super(reference, entityContentProducer);
        }

        @Override
        public String getContent() {
            return super.entityContentProducer.getContent(super.reference);
        }
    }
}
