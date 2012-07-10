package uk.ac.ox.oucs.search2.solr.content;

import org.apache.solr.common.SolrDocument;
import uk.ac.ox.oucs.search2.content.Content;
import uk.ac.ox.oucs.search2.solr.SolrSchemaConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Colin Hebert
 */
public class SolrContent implements Content {
    private final SolrDocument document;

    public SolrContent(SolrDocument document) {
        this.document = document;
    }

    @Override
    public String getId() {
        return (String) document.getFieldValue(SolrSchemaConstants.ID_FIELD);
    }

    @Override
    public String getTitle() {
        return (String) document.getFieldValue(SolrSchemaConstants.TITLE_FIELD);
    }

    @Override
    public String getUrl() {
        return (String) document.getFieldValue(SolrSchemaConstants.URL_FIELD);
    }

    @Override
    public boolean isPortalUrl() {
        return (Boolean) document.getFieldValue(SolrSchemaConstants.PORTALURL_FIELD);
    }

    @Override
    public String getType() {
        return (String) document.getFieldValue(SolrSchemaConstants.TYPE_FIELD);

    }

    @Override
    public String getSubtype() {
        return (String) document.getFieldValue(SolrSchemaConstants.SUBTYPE_FIELD);
    }

    @Override
    public String getTool() {
        return (String) document.getFieldValue(SolrSchemaConstants.TOOL_FIELD);
    }

    @Override
    public String getSiteId() {
        return (String) document.getFieldValue(SolrSchemaConstants.SITEID_FIELD);
    }

    @Override
    public String getReference() {
        return (String) document.getFieldValue(SolrSchemaConstants.REFERENCE_FIELD);

    }

    @Override
    public String getContainer() {
        return (String) document.getFieldValue(SolrSchemaConstants.CONTAINER_FIELD);
    }

    @Override
    public Map<String, Collection<String>> getProperties() {
        Map<String, Collection<Object>> fieldValuesMap = document.getFieldValuesMap();
        Map<String, Collection<String>> properties = new HashMap<String, Collection<String>>(fieldValuesMap.size());
        for (Map.Entry<String, Collection<Object>> property : fieldValuesMap.entrySet()) {
            Collection<String> values = new ArrayList<String>(property.getValue().size());
            for (Object value : property.getValue()) {
                values.add(value.toString());
            }

            properties.put(property.getKey(), values);
        }
        return properties;
    }
}
