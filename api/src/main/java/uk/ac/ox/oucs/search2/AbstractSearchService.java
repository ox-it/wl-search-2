package uk.ac.ox.oucs.search2;

import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.user.api.UserDirectoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.oucs.search2.filter.SearchFilter;
import uk.ac.ox.oucs.search2.result.SearchResultList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Colin Hebert
 */
public abstract class AbstractSearchService implements SearchService {
    private static final Logger logger = LoggerFactory.getLogger(AbstractSearchService.class);
    private int defaultLength = 10;
    private Iterable<SearchFilter> searchFilters;
    private UserDirectoryService userDirectoryService;
    private SiteService siteService;

    @Override
    public SearchResultList search(String searchQuery) {
        return search(searchQuery, getAllUserSites(), 0, defaultLength, searchFilters);
    }

    @Override
    public SearchResultList search(String searchQuery, Collection<String> contexts) {
        return search(searchQuery, contexts, 0, defaultLength, searchFilters);
    }

    @Override
    public SearchResultList search(String searchQuery, int start, int length) {
        return search(searchQuery, getAllUserSites(), start, length, searchFilters);
    }

    @Override
    public SearchResultList search(String searchQuery, Collection<String> contexts, int start, int length) {
        return search(searchQuery, contexts, start, length, searchFilters);
    }

    protected abstract SearchResultList search(String searchQuery, Collection<String> contexts, int start, int length, Iterable<SearchFilter> filterChain);

    @Override
    public void setSearchFilters(Iterable<SearchFilter> searchFilters) {
        this.searchFilters = searchFilters;
    }

    private Collection<String> getAllUserSites() {
        try {
            logger.info("Finding every site to in which the current user is a member.");
            String userId = userDirectoryService.getCurrentUser().getId();
            List<Site> sites = siteService.getSites(SiteService.SelectionType.ACCESS, null, null, null, null, null);
            Collection<String> siteIds = new ArrayList<String>(sites.size() + 1);
            for (Site site : sites) {
                siteIds.add(site.getId());
            }
            siteIds.add(siteService.getUserSiteId(userId));
            logger.debug("Found " + siteIds.size() + " sites: " + siteIds);
            return siteIds;
        } catch (Exception e) {
            logger.warn("Couldn't get every site for the current user.", e);
            return Collections.emptyList();
        }
    }

    public void setDefaultLength(int defaultLength) {
        this.defaultLength = defaultLength;
    }

    public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
        this.userDirectoryService = userDirectoryService;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }
}
