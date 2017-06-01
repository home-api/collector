package model;

import org.mongodb.morphia.annotations.Embedded;

@Embedded
public class OrderItem {

    private String item;
    private Double price;

    public OrderItem() {
    }

    public OrderItem(String item, Double price) {
        this.item = item;
        this.price = price;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof OrderItem)) {
            return false;
        }

        OrderItem that = (OrderItem) o;

        if (item != null ? !item.equals(that.item) : that.item != null) {
            return false;
        }

        //noinspection RedundantIfStatement
        if (price != null ? !price.equals(that.price) : that.price != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = result * 31 + (item != null ? item.hashCode() : 0);
        result = result * 31 + (price != null ? price.hashCode() : 0);
        return result;
    }
}
