package uk.ac.ox.oucs.search2.content;

import java.util.Collection;
import java.util.Map;

/**
 * @author Colin Hebert
 */
public interface Content {
    String getTitle();

    String getUrl();

    boolean isPortalUrl();

    String getType();

    String getTool();

    String getSubtype();

    String getSiteId();

    String getReference();

    String getId();

    Map<String, Collection<String>> getProperties();
}
