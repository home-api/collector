package dao.impl;

import dao.OrderDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class FileOrderDAOImpl implements OrderDAO {

    private Map<String, BigDecimal> food;
    private static final Logger LOGGER = LoggerFactory.getLogger(FileOrderDAOImpl.class);

    private Map<String, BigDecimal> garnir;
    private Map<String, BigDecimal> gunkan;
    private Map<String, BigDecimal> hotMaki;
    private Map<String, BigDecimal> miniRolls;
    private Map<String, BigDecimal> nigiri;
    private Map<String, BigDecimal> noriMaki;
    private Map<String, BigDecimal> soups;
    private Map<String, BigDecimal> uraMaki;
    private Map<String, List<Map<String, BigDecimal>>> orders;

    public FileOrderDAOImpl() throws Exception {
        orders = new HashMap<>();
        initialize();
    }

    protected void initialize() throws Exception {
        garnir = initializeFood("garnir");
        gunkan = initializeFood("gunkan");
        hotMaki = initializeFood("hotMaki");
        miniRolls = initializeFood("miniRolls");
        nigiri = initializeFood("nigiri");
        noriMaki = initializeFood("noriMaki");
        soups = initializeFood("soups");
        uraMaki = initializeFood("uraMaki");

        food = new HashMap<>();
        food.putAll(garnir);
        food.putAll(gunkan);
        food.putAll(hotMaki);
        food.putAll(miniRolls);
        food.putAll(nigiri);
        food.putAll(noriMaki);
        food.putAll(soups);
        food.putAll(uraMaki);
    }

    private Map<String, BigDecimal> initializeFood(String set) throws IOException {
        Properties foodNameProps = new Properties();
        foodNameProps.load(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(set + ".properties"), "UTF-8"));
        return foodNameProps.entrySet().stream().collect(
                Collectors.toMap(e -> (String) e.getKey(), e -> new BigDecimal((String) e.getValue())));
    }

    @Override
    public boolean addOrder(String customer, String order) {
        LOGGER.info(customer + " is adding order " + order);
        BigDecimal price = food.get(order);
        if (price == null) {
            LOGGER.info("Price for order " + order + " has not been found");
            return false;
        }

        HashMap<String, BigDecimal> customerOrder = new HashMap<>();
        customerOrder.put(order, price);

        if (orders.containsKey(customer)) {
            List<Map<String, BigDecimal>> customerOrders = orders.get(customer);
            customerOrders.add(customerOrder);
        } else {
            orders.put(customer, new ArrayList<>(Collections.singletonList(customerOrder)));
        }

        LOGGER.info(order + " with price " + price + " has been added for customer " + customer);

        return true;
    }

    @Override
    public String getOrder() {
        LOGGER.info("getting sum of all orders...");
        StringBuilder response = new StringBuilder("Заказ\n\n");
        Set<Map.Entry<String, List<Map<String, BigDecimal>>>> entries = orders.entrySet();
        BigDecimal sum = new BigDecimal(0.0);
        for (Map.Entry<String, List<Map<String, BigDecimal>>> entry : entries) {
            response.append(entry.getKey());
            response.append(": ");
            List<Map<String, BigDecimal>> customerOrders = entry.getValue();
            StringBuilder customerOrderString = new StringBuilder();
            BigDecimal customerSum = new BigDecimal(0.0);
            for (Map<String, BigDecimal> customerOrder : customerOrders) {
                Set<Map.Entry<String, BigDecimal>> sushies = customerOrder.entrySet();
                for (Map.Entry<String, BigDecimal> sushi : sushies) {
                    String value = sushi.getKey() + "(" + sushi.getValue() + ")";
                    customerOrderString.append(customerOrderString.length() > 0 ? ", " + value : value);
                    customerSum = customerSum.add(sushi.getValue());
                }
            }
            sum = sum.add(customerSum);

            response.append(customerOrderString);
            response.append(" - ");
            response.append(customerSum);
            response.append(" рублей.");
            response.append("\n\n");

        }
        response.append("Итого: ");
        response.append(sum);
        response.append(" рублей");
        LOGGER.info("Orders sum has been calculated: " + sum);
        return response.toString();
    }

    @Override
    public boolean removeOrder(String customer) {
        LOGGER.info(customer + " is removing its orders...");
        List<Map<String, BigDecimal>> oldOrders = orders.remove(customer);
        boolean wereOrderPresent = oldOrders != null;
        LOGGER.info(wereOrderPresent ? customer + " has removed its orders" : customer + " have had no orders");
        return wereOrderPresent;
    }

    @Override
    public void removeAllOrders() {
        orders.clear();
        LOGGER.info("All order have been removed");
    }

    public Map<String, BigDecimal> getGarnir() {
        return garnir;
    }

    public Map<String, BigDecimal> getGunkan() {
        return gunkan;
    }

    public Map<String, BigDecimal> getHotMaki() {
        return hotMaki;
    }

    public Map<String, BigDecimal> getMiniRolls() {
        return miniRolls;
    }

    public Map<String, BigDecimal> getNigiri() {
        return nigiri;
    }

    public Map<String, BigDecimal> getNoriMaki() {
        return noriMaki;
    }

    public Map<String, BigDecimal> getSoups() {
        return soups;
    }

    public Map<String, BigDecimal> getUraMaki() {
        return uraMaki;
    }

    public Map<String, BigDecimal> getFood() {
        return food;
    }
}
