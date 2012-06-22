package uk.ac.ox.oucs.search2.event;

import org.sakaiproject.event.api.Event;
import uk.ac.ox.oucs.search2.content.Content;

import java.util.Collection;

/**
 * @author Colin Hebert
 */
public interface IndexEventHandler {
    public enum IndexAction {
        INDEX_FILE,
        UNINDEX_FILE,
        INDEX_SITE,
        UNINDEX_SITE,
        INDEX_SITETOOL,
        UNINDEX_SITETOOL,
        INDEX_ALL,
        UNINDEX_ALL
    }

    Collection<String> getSupportedEventTypes();

    IndexAction getIndexAction(Event event);

    Content getContent(Event event);

    String getSite(Event event);

    String getSiteTool(Event event);

    boolean isHandled(Event event);
}
