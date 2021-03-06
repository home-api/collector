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
            order.getCustomersOrders().add(customerOrder);
        }

        customerOrder.getOrders().add(createCustomerOrderItem(orderItem));

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
    public Map<String, List<Map<String, Double>>> getAllOrders() {
        Order currentOrder = orderDAO.getCurrentOrder();

        if (currentOrder == null) {
            return new HashMap<>();
        }

        Map<String, List<Map<String, Double>>> allOrder = new HashMap<>();
        for (CustomerOrder customerOrder : currentOrder.getCustomersOrders()) {
            List<Map<String, Double>> allOrders = new ArrayList<>();
            for (OrderItem orderItem : customerOrder.getOrders()) {
                HashMap<String, Double> orderPrices = new HashMap<>();
                orderPrices.put(orderItem.getItem(), orderItem.getPrice());
                allOrders.add(orderPrices);
            }
            allOrder.put(customerOrder.getName(), allOrders);
        }
        return allOrder;
    }

    @Override
    public List<Map<String, Double>> getCustomerOrder(String customer) {
        List<Map<String, Double>> customerOrdersItems = new ArrayList<>();

        Order order = orderDAO.getCurrentOrder();
        if (order == null) {
            return customerOrdersItems;
        }

        CustomerOrder customerOrder = order.getCustomerOrder(customer);
        if (customerOrder == null) {
            return customerOrdersItems;
        }

        for (OrderItem orderItem : customerOrder.getOrders()) {
            Map<String, Double> customerOrderItem = new HashMap<>();
            customerOrderItem.put(orderItem.getItem(), orderItem.getPrice());
            customerOrdersItems.add(customerOrderItem);
        }
        return customerOrdersItems;
    }

    @Override
    public boolean deleteOrder(String customer) {
        Order order = orderDAO.getCurrentOrder();
        if (order == null) {
            return false;
        }

        boolean wasDeleted = order.deleteCustomerOrder(customer);
        orderDAO.saveOrder(order);
        return wasDeleted;
    }

    @Override
    public boolean deleteOrderItem(String customer, String orderItem) {
        Order order = orderDAO.getCurrentOrder();
        boolean wasDeleted = order.deleteCustomerOrderItem(customer, orderItem);

        if (order.getCustomerOrder(customer).isEmpty()) {
            order.deleteCustomerOrder(customer);
        }

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
        if (currentOrder == null) {
            currentOrder = new Order();
            currentOrder.setDate(LocalDate.now());
        }
        currentOrder.addCustomerOrder(customerOrder.getCustomerOrder(customer));
        orderDAO.saveOrder(currentOrder);

        return true;
    }


}
