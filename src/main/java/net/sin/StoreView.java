package net.sin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import net.sin.model.ProductResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;


public class StoreView extends VerticalLayout {
    private final VerticalLayout products;
    private final long storeId;
    private final UI ui;
    private final BackendService backendService;

    public StoreView(long storeId, String storeName, String storeUrl, UI ui, @Autowired BackendService backendService) {
        this.storeId = storeId;
        this.ui = ui;
        this.backendService = backendService;
        Anchor name = new Anchor(storeUrl, storeName);
        products = new VerticalLayout();
        add(name, products);
        getStyle().set("border-style", "solid");
        getStyle().set("border-width", "1px");
        setPadding(false);
        setSpacing(false);
    }

    public void display(Set<String> terms) {
        for (String term : terms) {
            HorizontalLayout product = new HorizontalLayout();
            for (ProductResult result : backendService.searchPerStore(storeId, term)) {
                product.add(new ProductView(result.getImage(), result.getPrice(), result.getName(), result.getUrl()));
            }
            ui.access(() -> {
                products.add(new VerticalLayout(new Label(term), product));
                ui.push();
            });
        }
    }
}
