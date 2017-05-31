package model;

import org.mongodb.morphia.annotations.Embedded;

@Embedded
public class OrderItem {

    private String item;
    private Double price;

    public String getItem() {
        return item;
    }

    public void setItem(String itemn) {
        this.item = itemn;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
