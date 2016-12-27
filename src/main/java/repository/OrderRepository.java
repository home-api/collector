package repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface OrderRepository {

    /**
     * Returns all customer's orders.
     * @param customer customer name
     * @return orders
     */
    List<Map<String, BigDecimal>> getCustomerOrders(String customer);

    /**
     * Returns all orders.
     * @return orders
     */
    Map<String, List<Map<String, BigDecimal>>> getAllOrders();

    //String getOrder();

    /**
     * Adds order.
     * @param customer customer's name
     * @param orderItem orderItem
     * @return if order has been added
     */
    boolean addOrder(String customer, String orderItem);

    /**
     * Removes all customer's order items.
     * @param customer customer name
     * @return if order has been removed
     */
    boolean removeCustomerOrder(String customer);

    /**
     * Removes single customer's order item.
     * @param customer customer name
     * @param orderItem order item
     * @return if order item has been removed
     */
    boolean removeCustomerOrderItem(String customer, String orderItem);

    /**
     * Removes all orders.
     */
    void removeAllOrders();

}
