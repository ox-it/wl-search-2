package uk.ac.ox.oucs.search2.tool.pages;

import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import uk.ac.ox.oucs.search2.SearchService;

import java.util.Arrays;

/**
 * @author Colin Hebert
 */
public class SearchPage extends SakaiPage {
    public static final int ITEMS_PER_PAGE = 10;

    @SpringBean
    private SearchService searchService;
    private Model<SearchScope> searchScopeModel = new Model<SearchScope>(SearchScope.CURRENT_SITE);
    private Panel resultPanel;
    private Model<String> searchQueryModel = new Model<String>();
    private final SuggestionCallback CALLBACK = new SuggestionCallback();

    public SearchPage() {
        Form<?> form = new Form<Void>("searchForm") {
            @Override
            protected void onSubmit() {
                generateResultPanel();
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

    private void generateResultPanel() {
        ResultPanel newPanel = new ResultPanel(this.resultPanel.getId(), searchService, searchQueryModel.getObject(), searchScopeModel.getObject(), CALLBACK);
        newPanel.setItemsPerPage(ITEMS_PER_PAGE);
        resultPanel.replaceWith(newPanel);
        resultPanel = newPanel;
    }

    public enum SearchScope {
        CURRENT_SITE,
        SUBSCRIBED_SITES,
        ALL_SITES
    }

    public class SuggestionCallback {
        public void callback(String suggestion) {
            searchQueryModel.setObject(suggestion);
            generateResultPanel();
        }
    }
}
