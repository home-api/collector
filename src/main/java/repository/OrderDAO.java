package repository;

import model.Order;

public interface OrderDAO {

    /**
     * Returns current order (as of today).
     *
     * @return order
     */
    Order getCurrentOrder();

    /**
     * Returns the most recent order that contains a customer order with the given customer.
     *
     * @param customer customer name
     * @return the most recent order
     */
    Order getRecentOrder(String customer);

    /**
     * Updates order for the specified customer. Order price should be determined inside an implementation.
     *
     * @param order order
     * @return whether order has been successfully added or not
     */
    boolean saveOrder(Order order);

    /**
     * Remove entire order (all ordered items) for the specified customer.
     *
     * @param customer sutomer name
     * @return whether order has been removed
     */
    boolean deleteOrder(String customer);

    /**
     * Delete all orders. This operation is now redundant and should be deleted in the future.
     *
     * @return whether all orders have been deleted.
     */
    boolean deleteAllOrders();
}
