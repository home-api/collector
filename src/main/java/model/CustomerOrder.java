package model;

import org.mongodb.morphia.annotations.Embedded;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Embedded
public class CustomerOrder implements Comparable<CustomerOrder> {

    private String name;
    @Embedded
    private List<OrderItem> orders = new ArrayList<>();

    public boolean deleteItem(String orderItem) {
        for (Iterator<OrderItem> iterator = orders.iterator(); iterator.hasNext();) {
            if (iterator.next().getItem().equals(orderItem)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return orders.isEmpty();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof CustomerOrder)) {
            return false;
        }

        CustomerOrder that = (CustomerOrder) o;

        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }

        //noinspection RedundantIfStatement
        if (orders != null ? !orders.equals(that.orders) : that.orders != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = result * 31 + (name != null ? name.hashCode() : 0);
        result = result * 31 + (orders != null ? orders.hashCode() : 0);
        return result;
    }

}
