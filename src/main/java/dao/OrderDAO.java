package dao;

import org.telegram.telegrambots.api.methods.send.SendMessage;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface OrderDAO {
    boolean addOrder(String customer, String order);

    String getOrder();

    boolean removeOrder(String customer);

    void removeAllOrders();

    Map<String, Map<String, BigDecimal>> getAllFood();

    List<Map<String, BigDecimal>> getCustomerOrders(String customer);

    boolean removeOrderItem(String customer, String orderItem);
}
