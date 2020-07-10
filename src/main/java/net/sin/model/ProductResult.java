package net.sin.model;

public class ProductResult extends ShopResult {
    private String price;
    private String image;

    public void setPrice(String price) {
        this.price = price;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }
}
