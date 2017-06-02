package repository.impl;

import model.Order;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.OrderDAO;

import java.time.LocalDate;

public class OrderDAOImpl extends BasicDAO<Order, String> implements OrderDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderDAOImpl.class);

    private Datastore datastore;

    public OrderDAOImpl(Datastore datastore) {
        super(datastore);
        this.datastore = datastore;
    }

    @Override
    public Order getCurrentOrder() {
        Query<Order> query = datastore.createQuery(Order.class);
        query.criteria("date").equal(LocalDate.now());
        return findOne(query);
    }

    @Override
    public Order getRecentOrder(String customer) {
        Query<Order> query = datastore.createQuery(Order.class);
        query.criteria("customersOrders.name").equal(customer);
        query.order(Sort.descending("date"));
        return findOne(query);
    }

    @Override
    public boolean saveOrder(Order order) {
        datastore.save(order);
        return true;
    }

    @Override
    public boolean deleteAllOrders() {
        datastore.delete(getCurrentOrder());
        return true;
    }

}
