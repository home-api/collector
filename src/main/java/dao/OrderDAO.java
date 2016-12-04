package dao;

import java.math.BigDecimal;
import java.util.Map;

public interface OrderDAO {
    boolean addOrder(String customer, String order);

    String getOrder();

    Map<String, BigDecimal> getGarnir();

    Map<String, BigDecimal> getGunkan();

    Map<String, BigDecimal> getHotMaki();

    Map<String, BigDecimal> getMiniRolls();

    Map<String, BigDecimal> getNigiri();

    Map<String, BigDecimal> getNoriMaki();

    Map<String, BigDecimal> getSoups();

    Map<String, BigDecimal> getUraMaki();

    Map<String, BigDecimal> getFood();
    boolean removeOrder(String customer);
    void removeAllOrders();
}
