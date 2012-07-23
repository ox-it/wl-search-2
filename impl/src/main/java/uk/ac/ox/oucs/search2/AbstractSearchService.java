package uk.ac.ox.oucs.search2;

import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.user.api.UserDirectoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ox.oucs.search2.filter.SearchFilter;
import uk.ac.ox.oucs.search2.result.SearchResultList;

import java.util.*;

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
        return search(searchQuery, getAllViewableSites(), 0, defaultLength, searchFilters);
    }

    @Override
    public SearchResultList search(String searchQuery, Collection<String> contexts) {
        return search(searchQuery, contexts, 0, defaultLength, searchFilters);
    }

    @Override
    public SearchResultList search(String searchQuery, SearchContext context) {
        return search(searchQuery, getContexts(context), 0, defaultLength, searchFilters);
    }

    @Override
    public SearchResultList search(String searchQuery, long start, long length) {
        return search(searchQuery, getAllViewableSites(), start, length, searchFilters);
    }

    @Override
    public SearchResultList search(String searchQuery, Collection<String> contexts, long start, long length) {
        return search(searchQuery, contexts, start, length, searchFilters);
    }

    protected abstract SearchResultList search(String searchQuery, Collection<String> contexts, long start, long length, Iterable<SearchFilter> filterChain);

    @Override
    public void setSearchFilters(Iterable<SearchFilter> searchFilters) {
        this.searchFilters = searchFilters;
    }


    /**
     * Obtain a list of site id based on the selected context
     *
     * @param context
     * @return
     */
    private Collection<String> getContexts(SearchContext context) {
        switch (context) {
            case ALL_SITES:
                return getAllViewableSites();
            case SUBSCRIBED_SITES:
                return getAllSubscribedSites();
            case CURRENT_SITE:
                if (false) {
                    //ToolManager toolManager = (ToolManager) ComponentManager.get(ToolManager.class);
                    //return Collections.singleton(toolManager.getCurrentPlacement().getContext());
                } else {
                    logger.info("Couldn't find the current website, switching back to the default context");
                }
            default:
                return getAllSubscribedSites();
        }
    }

    private Collection<String> getAllViewableSites() {
        try {
            logger.info("Finding every site to in which the current user is a member.");
            //TODO: Check that PUBVIEW and ACCESS aren't redundant
            List<Site> publicSites = siteService.getSites(SiteService.SelectionType.PUBVIEW, null, null, null, null, null);
            Collection<String> siteIds = new HashSet<String>(getAllSubscribedSites());
            for (Site site : publicSites) {
                siteIds.add(site.getId());
            }
            logger.debug("Found " + siteIds.size() + " userSites: " + siteIds);
            return siteIds;
        } catch (Exception e) {
            logger.warn("Couldn't get every site for the current user.", e);
            return Collections.emptyList();
        }
    }

    private Collection<String> getAllSubscribedSites() {
        List<Site> subscribedSites = siteService.getSites(SiteService.SelectionType.ACCESS, null, null, null, null, null);
        List<String> siteIds = new ArrayList<String>(subscribedSites.size() + 1);

        return siteIds;

    }

    private String getUserSite() {
        String userId = userDirectoryService.getCurrentUser().getId();
        return siteService.getUserSiteId(userId);
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
