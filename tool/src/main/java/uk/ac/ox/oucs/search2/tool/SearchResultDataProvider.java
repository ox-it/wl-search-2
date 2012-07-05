package uk.ac.ox.oucs.search2.tool;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.user.api.UserDirectoryService;
import uk.ac.ox.oucs.search2.SearchService;
import uk.ac.ox.oucs.search2.result.SearchResult;
import uk.ac.ox.oucs.search2.result.SearchResultList;
import uk.ac.ox.oucs.search2.tool.pages.SearchPage;

import java.util.*;

/**
 * @author Colin Hebert
 */
public class SearchResultDataProvider implements IDataProvider<SearchResult> {
    private final SearchService searchService;
    private final String query;
    private final Collection<String> siteIds;
    private double queryDuration;
    private SearchResultList searchResultList;


    public SearchResultDataProvider(SearchService searchService, String query, SearchPage.SearchScope searchScope) {
        this.searchService = searchService;
        this.query = query;
        if (searchScope == SearchPage.SearchScope.CURRENT_SITE) {
            siteIds = Collections.singleton("test");
        } else if (searchScope == SearchPage.SearchScope.SUBSCRIBED_SITES) {
            this.siteIds = getCurrentUserSubscribedSites();
        } else if (searchScope == SearchPage.SearchScope.ALL_SITES) {
            siteIds = null;
        } else {
            throw new UnsupportedOperationException("Can't do a search on " + searchScope);
        }
    }

    private Collection<String> getCurrentUserSubscribedSites() {
        SiteService siteService = (SiteService) ComponentManager.get(SiteService.class);
        UserDirectoryService userDirectoryService = (UserDirectoryService) ComponentManager.get(UserDirectoryService.class);

        List<Site> userSites = siteService.getSites(SiteService.SelectionType.ACCESS, null, null, null, null, null);
        String userId = userDirectoryService.getCurrentUser().getId();

        Collection<String> siteIds = new ArrayList<String>(userSites.size() + 1);
        for (Site site : userSites) {
            siteIds.add(site.getId());
        }
        siteIds.add(siteService.getUserSiteId(userId));
        return siteIds;
    }

    @Override
    public Iterator<? extends SearchResult> iterator(int first, int count) {
        long queryStartTime = System.currentTimeMillis();
        if (siteIds == null) {
            searchResultList = searchService.search(query, first, count);
        } else {
            searchResultList = searchService.search(query, siteIds, first, count);
        }
        long queryEndTime = System.currentTimeMillis();
        queryDuration = (queryEndTime - queryStartTime) / 1000d;

        return searchResultList.iterator();
    }

    @Override
    public int size() {
        return (int) searchService.search(query, null, 0, 0).getNumberResultsFound();
    }

    @Override
    public IModel<SearchResult> model(final SearchResult object) {
        return new AbstractReadOnlyModel<SearchResult>() {
            @Override
            public SearchResult getObject() {
                return object;
            }
        };
    }

    @Override
    public void detach() {
    }

    public double getQueryDuration() {
        return queryDuration;
    }

    public SearchResultList getSearchResultList() {
        return searchResultList;
    }
}
