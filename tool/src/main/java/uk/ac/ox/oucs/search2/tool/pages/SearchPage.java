package uk.ac.ox.oucs.search2.tool.pages;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import uk.ac.ox.oucs.search2.SearchService;
import uk.ac.ox.oucs.search2.result.SearchResult;
import uk.ac.ox.oucs.search2.tool.SearchResultDataProvider;
import uk.ac.ox.oucs.search2.tool.TermWeigher;

import java.util.Arrays;

/**
 * @author Colin Hebert
 */
public class SearchPage extends SakaiPage {
    public static final int ITEMS_PER_PAGE = 2;
    //@SpringBean
    private SearchService searchService;
    private Model<SearchScope> searchScopeModel = new Model<SearchScope>(SearchScope.CURRENT_SITE);
    private Panel resultPanel;
    private Model<String> searchQueryModel = new Model<String>();

    public SearchPage() {
        addMenuLink(SearchPage.class, new ResourceModel("menu.list.consumer"), null);
        addMenuLink(SearchPage.class, new ResourceModel("menu.add.consumer"), null);

        Form<?> form = new Form<Void>("searchForm") {
            @Override
            protected void onSubmit() {
                info("Selected Type : " + SearchPage.this.searchScopeModel);
                replaceResultPanel(new ResultPanel(resultPanel.getId()));
            }
        };
        add(form);

        TextField<String> searchField = new TextField<String>("search", searchQueryModel);
        form.add(searchField);

        RadioChoice<SearchScope> searchType = new RadioChoice<SearchScope>("searchScope",
                searchScopeModel,
                Arrays.asList(SearchScope.values()),
                new EnumChoiceRenderer<SearchScope>(this));
        searchType.setSuffix("");
        form.add(searchType);
        resultPanel = new EmptyPanel("resultPanel");
        add(resultPanel);
    }

    private void replaceResultPanel(Panel newPanel) {
        resultPanel.replaceWith(newPanel);
        resultPanel = newPanel;
    }

    public enum SearchScope {
        CURRENT_SITE,
        SUBSCRIBED_SITES,
        ALL_SITES
    }

    private class ResultPanel extends Panel {
        public ResultPanel(String id) {
            super(id);
            SearchResultDataProvider searchResultDataProvider = new SearchResultDataProvider(searchService, searchQueryModel.getObject(), searchScopeModel.getObject());

            final String suggestion = searchResultDataProvider.getSearchResultList().getSuggestion();

            Link<Void> suggestionLink = new Link<Void>("suggestionLink") {
                @Override
                public void onClick() {
                    searchQueryModel.setObject(suggestion);
                    replaceResultPanel(new ResultPanel(resultPanel.getId()));
                }
            };
            suggestionLink.add(new Label("suggestion", suggestion));
            add(suggestionLink);

            DataView<SearchResult> results = new DataView<SearchResult>("resultlist", searchResultDataProvider, ITEMS_PER_PAGE) {
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
            add(new PagingNavigator("navigator", results));
            add(results);

            TermWeigher termWeigher = new TermWeigher(searchResultDataProvider.getSearchResultList().getTermFrequencies());
            termWeigher.setRelativeWeigher(true);
            ListView<TermWeigher.Term> tags = new ListView<TermWeigher.Term>("tagList", termWeigher.getWeighedTerms()) {

                @Override
                protected void populateItem(ListItem<TermWeigher.Term> entryListItem) {

                    Link<TermWeigher.Term> tagLink = new Link<TermWeigher.Term>("taglink", entryListItem.getModel()) {
                        @Override
                        public void onClick() {
                            searchQueryModel.setObject(getModelObject().getTerm());
                            replaceResultPanel(new ResultPanel(resultPanel.getId()));
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
            add(tags);
        }
    }
}
