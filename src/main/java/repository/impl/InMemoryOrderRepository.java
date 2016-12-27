package repository.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.MenuRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Singleton
public class InMemoryOrderRepository implements repository.OrderRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryOrderRepository.class);

    @Inject
    private MenuRepository menuRepository;

    private Map<String, List<Map<String, BigDecimal>>> allOrders;

    public InMemoryOrderRepository() throws Exception {
        allOrders = new HashMap<>();
    }

    @Override
    public boolean addOrder(String customer, String orderItem) {
        LOGGER.info(customer + " is adding order " + orderItem);
        BigDecimal price = menuRepository.getPrice(orderItem);
        if (price == null) {
            LOGGER.info("Price for order " + orderItem + " has not been found");
            return false;
        }

        HashMap<String, BigDecimal> customerOrder = new HashMap<>();
        customerOrder.put(orderItem, price);

        if (allOrders.containsKey(customer)) {
            List<Map<String, BigDecimal>> customerOrders = allOrders.get(customer);
            customerOrders.add(customerOrder);
        } else {
            allOrders.put(customer, new ArrayList<>(Collections.singletonList(customerOrder)));
        }

        LOGGER.info(orderItem + " with price " + price + " has been added for customer " + customer);

        return true;
    }

    @Override
    public boolean removeCustomerOrder(String customer) {
        LOGGER.info(customer + " is removing its allOrders...");
        List<Map<String, BigDecimal>> oldOrders = allOrders.remove(customer);
        boolean wereOrderPresent = oldOrders != null;
        LOGGER.info(wereOrderPresent ? customer + " has removed its allOrders" : customer + " have had no allOrders");
        return wereOrderPresent;
    }

    @Override
    public void removeAllOrders() {
        allOrders.clear();
        LOGGER.info("All order have been removed");
    }

    @Override
    public List<Map<String, BigDecimal>> getCustomerOrders(String customer) {
        return allOrders.get(customer);
    }

    @Override
    public Map<String, List<Map<String, BigDecimal>>> getAllOrders() {
        return new HashMap<>(allOrders);
    }

    @Override
    public boolean removeCustomerOrderItem(String customer, String orderItem) {
        List<Map<String, BigDecimal>> customerOrders = allOrders.get(customer);
        Iterator<Map<String, BigDecimal>> iterator = customerOrders.iterator();
        while (iterator.hasNext()) {
            Map<String, BigDecimal> item = iterator.next();
            if (item.keySet().contains(orderItem)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

}
