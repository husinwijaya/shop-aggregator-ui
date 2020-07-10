package net.sin;

import com.google.common.collect.Sets;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import net.sin.model.ShopResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.stream.Collectors.toList;

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
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Logger log = LoggerFactory.getLogger(MainView.class);

    public MainView(@Autowired BackendService backendService) {
        MultiselectComboBox<String> search = new MultiselectComboBox<>();
        search.setLabel("Search something");
        var dataProvider = SuggestionProvider.create(backendService);
        search.setDataProvider(dataProvider);
        search.setAllowCustomValues(true);
        search.addCustomValuesSetListener(event -> {
            dataProvider.cancelCurrent();
            LinkedHashSet<String> newValue = Sets.newLinkedHashSet(search.getValue());
            newValue.add(event.getDetail());
            search.setValue(newValue);
        });
        search.setWidthFull();

        Button submitBtn = new Button("Submit");
        submitBtn.getStyle().set("align-self", "center");

        submitBtn.addClickListener(event -> {
            UI ui = event.getSource().getUI().orElse(UI.getCurrent());
            Set<String> terms = search.getValue();
            clearSearch();
            executor.submit(() -> {
                Iterator<Set<ShopResult>> storeIterator = terms.parallelStream().map(backendService::search).collect(toList()).iterator();
                Set<ShopResult> stores = null;
                while (storeIterator.hasNext()) {
                    if (stores == null) stores = storeIterator.next();
                    else {
                        stores = Sets.intersection(stores, storeIterator.next());
                    }
                }
                if (stores == null) return;
                stores.parallelStream().forEach(store -> {
                    var storeView = new StoreView(store.getId(), store.getName(), store.getUrl(), ui, backendService);
                    ui.access(() -> {
                        add(storeView);
                        ui.push();
                    });
                    storeView.display(terms);
                });
            });
        });

        addClassName("centered-content");
        add(search, submitBtn);
    }

    public void clearSearch() {
        getChildren().filter(StoreView.class::isInstance).forEach(this::remove);
    }

}
