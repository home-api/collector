package model;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

@Entity
public class Order extends BaseEntity {

    private LocalDate date;
    @Embedded
    private Set<CustomerOrder> customersOrders = new TreeSet<>();

    public CustomerOrder getCustomerOrder(String customer) {
        for (CustomerOrder customerOrder : customersOrders) {
            if (customerOrder.getName().equals(customer)) {
                return customerOrder;
            }
        }
        return null;
    }

    public boolean deleteCustomerOrder(String customerName) {
        for (Iterator<CustomerOrder> iterator = customersOrders.iterator(); iterator.hasNext(); ) {
            CustomerOrder customerOrder = iterator.next();
            if (customerOrder.getName().equals(customerName)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public boolean deleteCustomerOrderItem(String customer, String orderItem) {
        for (CustomerOrder customerOrder : customersOrders) {
            if (customerOrder.getName().equals(customer)) {
                return customerOrder.deleteItem(orderItem);
            }
        }
        return false;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Set<CustomerOrder> getCustomersOrders() {
        // TODO: e.k. check why this field is not initialized by default
        if (customersOrders == null) {
            customersOrders = new TreeSet<>();
        }
        return customersOrders;
    }

    public void setCustomersOrders(Set<CustomerOrder> customersOrders) {
        this.customersOrders = customersOrders;
    }
}
