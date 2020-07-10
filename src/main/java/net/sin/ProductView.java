package net.sin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ProductView extends VerticalLayout {
    public ProductView(String productImage, String productPrice, String productName, String productUrl) {
        Image image = new Image(productImage, productImage);
        image.setWidth("100px");
        Label price = new Label(productPrice);
        Label name = new Label(productName);
        add(image, price, name);
        getStyle().set("border-style", "dashed");
        getStyle().set("border-width", "1px");
        getStyle().set("cursor", "pointer");
        setPadding(false);
        setSpacing(false);
        addClickListener(e -> UI.getCurrent().getPage().executeJs("window.open(\"" + productUrl + "\", \"_blank\", \"\");"));
    }
}
