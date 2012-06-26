package uk.ac.ox.oucs.search2.backwardcompatibility.content;

import org.sakaiproject.search.api.SearchResult;
import org.sakaiproject.search.api.SearchService;
import uk.ac.ox.oucs.search2.content.Content;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Colin Hebert
 */
public class ResultBackContent implements Content {
    private final SearchResult searchResult;
    private Map<String, Collection<String>> properties;

    public ResultBackContent(SearchResult searchResult) {
        this.searchResult = searchResult;
    }

    @Override
    public String getTitle() {
        return searchResult.getTitle();
    }

    @Override
    public String getUrl() {
        return searchResult.getUrl();
    }

    @Override
    public boolean isPortalUrl() {
        return searchResult.hasPortalUrl();
    }

    @Override
    public String getType() {
        return getPropertyValue(SearchService.FIELD_TYPE);
    }

    @Override
    public String getTool() {
        return searchResult.getTool();
    }

    @Override
    public String getSubtype() {
        return getPropertyValue(SearchService.FIELD_SUBTYPE);
    }

    @Override
    public String getSiteId() {
        return searchResult.getSiteId();
    }

    @Override
    public String getReference() {
        return searchResult.getReference();
    }

    @Override
    public String getContainer() {
        return getPropertyValue(SearchService.FIELD_CONTAINER);
    }

    @Override
    public String getId() {
        return searchResult.getId();
    }

    @Override
    public Map<String, Collection<String>> getProperties() {
        if (properties == null) {
            Map<String, String[]> originalProperties = searchResult.getValueMap();
            properties = new HashMap<String, Collection<String>>(originalProperties.size());

            for (Map.Entry<String, String[]> propertyEntry : originalProperties.entrySet()) {
                properties.put(propertyEntry.getKey(), Arrays.asList(propertyEntry.getValue()));
            }
        }
        return properties;
    }

    private String getPropertyValue(String propertyName) {
        Collection<String> propertyValues = getProperties().get(propertyName);
        if (propertyValues != null && propertyValues.size() > 0)
            return propertyValues.iterator().next();
        else
            return null;
    }
}
