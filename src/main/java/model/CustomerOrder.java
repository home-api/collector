package model;

import org.mongodb.morphia.annotations.Embedded;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Embedded
public class CustomerOrder implements Comparable<CustomerOrder> {

    private String name;
    @Embedded
    private List<OrderItem> orders = new ArrayList<>();

    boolean deleteItem(String orderItem) {
        for (Iterator<OrderItem> iterator = orders.iterator(); iterator.hasNext();) {
            if (iterator.next().getItem().equals(orderItem)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OrderItem> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderItem> orders) {
        this.orders = orders;
    }

    @Override
    public int compareTo(CustomerOrder that) {
        return name.compareTo(that.getName());
    }

}
