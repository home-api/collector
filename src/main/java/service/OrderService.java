package service;

import java.util.List;
import java.util.Map;

public interface OrderService {

    /**
     * Creates an order (from orderItem) and add that to the customer.
     *
     * @param customer  customer name
     * @param orderItem order item
     * @return whether order has been added
     */
    boolean addOrder(String customer, String orderItem);

    /**
     * Returns all orders.
     *
     * @return orders
     */
    Map<String, List<Map<String, Double>>> getAllOrders();

    /**
     * Returns all customer order;
     *
     * @param customer customer name
     * @return customer orders
     */
    List<Map<String, Double>> getCustomerOrder(String customer);

    /**
     * Deletes entire order (all ordered items) for the specified customer.
     *
     * @param customer sutomer name
     * @return whether order has been removed
     */
    boolean deleteOrder(String customer);

    /**
     * Deletes order item for the specified customer.
     *
     * @param customer  customer name
     * @param orderItem order item name
     * @return whether order item has been deleted
     */
    boolean deleteOrderItem(String customer, String orderItem);

    /**
     * Delete all orders. This operation is now redundant and should be deleted in the future.
     *
     * @return whether all orders have been deleted.
     */
    boolean deleteAllOrders();

    /**
     * Repeats the most recent customer order. If customer does not have any previous orders returns false.
     *
     * @param customer customer
     * @return whether an order has been repeated
     */
    boolean repeatOrder(String customer);
}
