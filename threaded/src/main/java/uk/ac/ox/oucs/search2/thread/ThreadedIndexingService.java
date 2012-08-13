package uk.ac.ox.oucs.search2.thread;

import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import uk.ac.ox.oucs.search2.IndexingService;
import uk.ac.ox.oucs.search2.content.Content;

import java.util.concurrent.Executor;

/**
 * @author Colin Hebert
 */
public class ThreadedIndexingService implements IndexingService {
    private IndexingService wrappedIndexingService;
    private Executor executor;
    private SessionManager sessionManager;

    public void setWrappedIndexingService(IndexingService wrappedIndexingService) {
        this.wrappedIndexingService = wrappedIndexingService;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void indexContent(final String eventHandlerName, final Iterable<Content> contents) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                setCurrentSessionToAdmin();
                wrappedIndexingService.indexContent(eventHandlerName, contents);
            }
        });
    }

    @Override
    public void unindexContent(final String eventHandlerName, final Iterable<Content> contents) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                setCurrentSessionToAdmin();
                wrappedIndexingService.unindexContent(eventHandlerName, contents);
            }
        });
    }

    @Override
    public void indexSite(final String eventHandlerName, final Iterable<Content> contents, final String site) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                setCurrentSessionToAdmin();
                wrappedIndexingService.indexSite(eventHandlerName, contents, site);
            }
        });
    }

    @Override
    public void reindexSite(final String eventHandlerName, final Iterable<Content> contents, final String site) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                setCurrentSessionToAdmin();
                wrappedIndexingService.reindexSite(eventHandlerName, contents, site);
            }
        });
    }

    @Override
    public void unindexSite(final String eventHandlerName, final String site) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                setCurrentSessionToAdmin();
                wrappedIndexingService.unindexSite(eventHandlerName, site);
            }
        });
    }

    @Override
    public void indexAll(final String eventHandlerName, final Iterable<Content> contents) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                setCurrentSessionToAdmin();
                wrappedIndexingService.indexAll(eventHandlerName, contents);
            }
        });
    }

    @Override
    public void reindexAll(final String eventHandlerName, final Iterable<Content> contents) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                setCurrentSessionToAdmin();
                wrappedIndexingService.reindexAll(eventHandlerName, contents);
            }
        });
    }

    @Override
    public void unindexAll(final String eventHandlerName) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                setCurrentSessionToAdmin();
                wrappedIndexingService.unindexAll(eventHandlerName);
            }
        });
    }

    private void setCurrentSessionToAdmin(){
        Session session =  sessionManager.getCurrentSession();
        session.setUserId("admin");
        session.setUserEid("admin");
    }
}
