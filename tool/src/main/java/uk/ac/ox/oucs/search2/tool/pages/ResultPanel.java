package uk.ac.ox.oucs.search2.tool.pages;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.UserDirectoryService;
import uk.ac.ox.oucs.search2.SearchService;
import uk.ac.ox.oucs.search2.result.SearchResult;
import uk.ac.ox.oucs.search2.result.SearchResultList;
import uk.ac.ox.oucs.search2.tool.TermWeigher;

import java.util.*;

/**
 * @author Colin Hebert
 */
public class ResultPanel extends Panel {
    private int itemsPerPage = 10;
    private TermWeigher termWeigher;
    private IModel<String> suggestion = Model.of("");
    private IModel<Double> queryDuration = Model.of(0.0d);
    private IModel<List<? extends TermWeigher.Term>> tags = Model.ofList(Collections.<TermWeigher.Term>emptyList());
    private final SearchResultDataProvider searchResultDataProvider;
    private final SearchPage.SuggestionCallback callback;

    public ResultPanel(String id, SearchService searchService, String searchQuery, SearchPage.SearchScope searchScope, SearchPage.SuggestionCallback suggestionCallback) {
        super(id);
        searchResultDataProvider = new SearchResultDataProvider(searchService, searchQuery, searchScope);
        termWeigher = new TermWeigher();
        callback = suggestionCallback;

        add(generateSuggestionLink());
        DataView<SearchResult> results = generateResultDataView();
        add(new PagingNavigator("navigator", results));
        add(results);
        add(generateTagListView());
    }

    private Link<String> generateSuggestionLink() {
         Link<String> suggestionLink = new Link<String>("suggestionLink", suggestion) {
            @Override
            public void onClick() {
                callback.callback(getModelObject());
            }
        };
        suggestionLink.add(new Label("suggestion", suggestion));
        return suggestionLink;
    }

    private DataView<SearchResult> generateResultDataView() {
        return new DataView<SearchResult>("resultlist", searchResultDataProvider, itemsPerPage) {
            @Override
            protected void populateItem(Item<SearchResult> stringItem) {
                SearchResult searchResult = stringItem.getModelObject();
                stringItem.setVisible(!searchResult.isCensored());
                stringItem.add(new Label("tool", searchResult.getContent().getTool()));
                stringItem.add(new ExternalLink("titlelink", searchResult.getContent().getUrl(), searchResult.getContent().getTitle()));
                stringItem.add(new Label("text", searchResult.getDisplayedText()));
                stringItem.add(new ExternalLink("footlink", searchResult.getContent().getUrl(), searchResult.getContent().getUrl()));
            }
        };
    }

    private ListView<TermWeigher.Term> generateTagListView() {
        return new ListView<TermWeigher.Term>("tagList", tags) {

            @Override
            protected void populateItem(ListItem<TermWeigher.Term> entryListItem) {

                Link<TermWeigher.Term> tagLink = new Link<TermWeigher.Term>("taglink", entryListItem.getModel()) {
                    @Override
                    public void onClick() {
                        callback.callback(getModelObject().getTerm());
                    }

                    @Override
                    protected void onComponentTag(ComponentTag tag) {
                        super.onComponentTag(tag);
                        tag.put("style", tag.getAttribute("style") + "; font-size:" + getModelObject().getWeight() + "em;");
                    }
                };
                tagLink.add(new Label("term", entryListItem.getModelObject().getTerm()));
                entryListItem.add(tagLink);
            }
        };
    }

    private class SearchResultDataProvider implements IDataProvider<SearchResult> {
        private final SearchService searchService;
        private final String query;
        private final Collection<String> contexts;


        public SearchResultDataProvider(SearchService searchService, String query, SearchPage.SearchScope searchScope) {
            this.searchService = searchService;
            this.query = query;
            contexts = extractContexts(searchScope);
        }

        private Collection<String> extractContexts(SearchPage.SearchScope searchScope) {
            if (searchScope == SearchPage.SearchScope.CURRENT_SITE) {
                ToolManager toolManager = (ToolManager) ComponentManager.get(ToolManager.class);
                return Collections.singleton(toolManager.getCurrentPlacement().getContext());
            } else if (searchScope == SearchPage.SearchScope.SUBSCRIBED_SITES) {
                return getCurrentUserSubscribedSites();
            } else if (searchScope == SearchPage.SearchScope.ALL_SITES) {
                return null;
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
            SearchResultList searchResultList;
            long queryStartTime = System.currentTimeMillis();
            if (contexts == null) {
                searchResultList = searchService.search(query, first, count);
            } else {
                searchResultList = searchService.search(query, contexts, first, count);
            }
            long queryEndTime = System.currentTimeMillis();
            queryDuration.setObject((queryEndTime - queryStartTime) / 1000d);

            suggestion.setObject(searchResultList.getSuggestion());
            tags.setObject(termWeigher.getWeighedTerms(searchResultList.getTermFrequencies()));

            return searchResultList.iterator();
        }

        @Override
        public int size() {
            if (contexts == null) {
                return (int) searchService.search(query, 0, 0).getNumberResultsFound();
            } else {
                return (int) searchService.search(query, contexts, 0, 0).getNumberResultsFound();
            }
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
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public void setTermWeigher(TermWeigher termWeigher) {
        this.termWeigher = termWeigher;
    }
}
