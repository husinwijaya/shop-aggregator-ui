package net.sin;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.gatanaso.MultiselectComboBox;

import static com.google.common.collect.Sets.union;
import static java.util.Collections.singleton;

/**
 * A sample Vaadin view class.
 * <p>
 * To implement a Vaadin view just extend any Vaadin component and use @Route
 * annotation to announce it in a URL as a Spring managed bean.
 * <p>
 * A new instance of this class is created for every new user and every browser
 * tab/window.
 * <p>
 * The main view contains a text field for getting the user name and a button
 * that shows a greeting message in a notification.
 */
@Route
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/multiselect-combo-box-input-styles.css", themeFor = "multiselect-combo-box-input")
public class MainView extends VerticalLayout {

    /**
     * Construct a new Vaadin view.
     * <p>
     * Build the initial UI state for the user accessing the application.
     *
     * @param service The message service. Automatically injected Spring managed
     *                bean.
     */
    public MainView(@Autowired GreetService service, @Autowired BackendService backendService) {
        MultiselectComboBox<String> search = new MultiselectComboBox<>();
        search.setLabel("Search something");
        search.setDataProvider(SuggestionProvider.create(backendService));
        search.setAllowCustomValues(true);
        search.addCustomValuesSetListener(event -> search.setValue(union(search.getValue(), singleton(event.getDetail()))));
        search.setWidthFull();
        addClassName("centered-content");
        add(search);
    }

}
