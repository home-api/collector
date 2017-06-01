package service.impl;

import com.google.inject.Inject;
import model.CustomerOrder;
import model.Order;
import model.OrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.OrderDAO;
import service.OrderService;
import util.Menu;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Inject
    private Menu menu;

    @Inject
    private OrderDAO orderDAO;

    @Override
    public boolean addOrder(String customer, String orderItem) {
        LOGGER.info(customer + " is adding order " + orderItem);
        Order order = orderDAO.getCurrentOrder();
        if (order == null) {
            order = new Order();
            order.setDate(LocalDate.now());
        }

        CustomerOrder customerOrder = order.getCustomerOrder(customer);
        if (customerOrder == null) {
            customerOrder = new CustomerOrder();
            customerOrder.setName(customer);
        }

        customerOrder.getOrders().add(createCustomerOrderItem(orderItem));
        order.getCustomersOrders().add(customerOrder);

        LOGGER.info(orderItem + " with price  has been added for customer " + customer);

        orderDAO.saveOrder(order);
        return true;
    }

    private OrderItem createCustomerOrderItem(String orderItem) {
        Double price = menu.getPrice(orderItem);

        if (price == null) {
            LOGGER.info("Price for order " + orderItem + " has not been found");
            throw new IllegalArgumentException("Price for order " + orderItem + " has not been found");
        }

        return new OrderItem(orderItem, price);
    }

    @Override
    public Map<String, List<Map<String, BigDecimal>>> getAllOrders() {
        Order currentOrder = orderDAO.getCurrentOrder();

        if (currentOrder == null) {
            return new HashMap<>();
        }

        Map<String, List<Map<String, BigDecimal>>> allOrder = new HashMap<>();
        for (CustomerOrder customerOrder : currentOrder.getCustomersOrders()) {
            List<Map<String, BigDecimal>> allOrders = new ArrayList<>();
            for (OrderItem orderItem : customerOrder.getOrders()) {
                HashMap<String, BigDecimal> orderPrices = new HashMap<>();
                orderPrices.put(orderItem.getItem(), BigDecimal.valueOf(orderItem.getPrice()));
                allOrders.add(orderPrices);
            }
            allOrder.put(customerOrder.getName(), allOrders);
        }
        return allOrder;
    }

    @Override
    public List<Map<String, Double>> getCustomerOrders(String customer) {
        List<Map<String, Double>> customerOrdersItems = new ArrayList<>();
        CustomerOrder order = orderDAO.getCurrentOrder().getCustomerOrder(customer);
        for (OrderItem orderItem : order.getOrders()) {
            Map<String, Double> customerOrderItem = new HashMap<>();
            customerOrderItem.put(orderItem.getItem(), orderItem.getPrice());
            customerOrdersItems.add(customerOrderItem);
        }
        return customerOrdersItems;
    }

    @Override
    public boolean deleteOrder(String customer) {
        return orderDAO.deleteOrder(customer);
    }

    @Override
    public boolean deleteOrderItem(String customer, String orderItem) {
        Order order = orderDAO.getCurrentOrder();
        boolean wasDeleted = order.deleteCustomerOrderItem(customer, orderItem);
        orderDAO.saveOrder(order);
        return wasDeleted;
    }

    @Override
    public boolean deleteAllOrders() {
        return orderDAO.deleteAllOrders();
    }

    @Override
    public boolean repeatOrder(String customer) {
        Order customerOrder = orderDAO.getRecentOrder(customer);

        if (customerOrder == null) {
            return false;
        }

        Order currentOrder = orderDAO.getCurrentOrder();
        currentOrder.addCustomerOrder(customerOrder.getCustomerOrder(customer));
        orderDAO.saveOrder(currentOrder);

        return true;
    }


}
