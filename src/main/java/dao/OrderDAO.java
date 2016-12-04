package dao;

import java.math.BigDecimal;
import java.util.Map;

public interface OrderDAO {
    boolean addOrder(String customer, String order);

    String getOrder();

    boolean removeOrder(String customer);

    void removeAllOrders();

    Map<String, Map<String, BigDecimal>> getAllFood();
}
