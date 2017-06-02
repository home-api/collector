package model;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Entity
public class Order extends BaseEntity {

    private LocalDate date;
    @Embedded
    private List<CustomerOrder> customersOrders = new ArrayList<>();

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

    public void addCustomerOrder(CustomerOrder customerOrder) {
        deleteCustomerOrder(customerOrder.getName());
        customersOrders.add(customerOrder);
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<CustomerOrder> getCustomersOrders() {
        return customersOrders;
    }

    public void setCustomersOrders(List<CustomerOrder> customersOrders) {
        this.customersOrders = customersOrders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Order)) {
            return false;
        }

        Order that = (Order) o;

        if (date != null ? !date.equals(that.date) : that.date != null) {
            return false;
        }

        //noinspection RedundantIfStatement
        if (customersOrders != null ? !customersOrders.equals(that.customersOrders) : that.customersOrders != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = result * 31 + (date != null ? date.hashCode() : 0);
        result = result * 31 + (customersOrders != null ? customersOrders.hashCode() : 0);
        return result;
    }
}
