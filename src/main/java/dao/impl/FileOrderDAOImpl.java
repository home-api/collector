package dao.impl;

import com.google.inject.Singleton;
import dao.OrderDAO;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class FileOrderDAOImpl implements OrderDAO {

    private Map<String, Map<String, BigDecimal>> food;
    private static final Logger LOGGER = LoggerFactory.getLogger(FileOrderDAOImpl.class);

    private Map<String, List<Map<String, BigDecimal>>> allOrders;

    public FileOrderDAOImpl() throws Exception {
        allOrders = new HashMap<>();
        initializeFood();
    }

    private void initializeFood() throws Exception {
        food = new HashMap<>();

        Collection<File> menuFiles = FileUtils.listFiles(
                new File(getClass().getClassLoader().getResource("menu").toURI()),
                new String[]{"properties"},
                false);

        for (File menuFile : menuFiles) {
            String menuName = FilenameUtils.getBaseName(menuFile.getAbsolutePath());
            Properties menu = new Properties();
            menu.load(new InputStreamReader(new FileInputStream(menuFile), "UTF-8"));
            Map<String, BigDecimal> options = menu.entrySet().stream().collect(
                    Collectors.toMap(e -> (String) e.getKey(), e -> new BigDecimal((String) e.getValue())));
            food.put(menuName, options);
        }
    }

    @Override
    public boolean addOrder(String customer, String order) {
        LOGGER.info(customer + " is adding order " + order);
        BigDecimal price = findPrice(order);
        if (price == null) {
            LOGGER.info("Price for order " + order + " has not been found");
            return false;
        }

        HashMap<String, BigDecimal> customerOrder = new HashMap<>();
        customerOrder.put(order, price);

        if (allOrders.containsKey(customer)) {
            List<Map<String, BigDecimal>> customerOrders = allOrders.get(customer);
            customerOrders.add(customerOrder);
        } else {
            allOrders.put(customer, new ArrayList<>(Collections.singletonList(customerOrder)));
        }

        LOGGER.info(order + " with price " + price + " has been added for customer " + customer);

        return true;
    }

    private BigDecimal findPrice(String order) {
        for (Map.Entry<String, Map<String, BigDecimal>> group : food.entrySet()) {
            for (Map.Entry<String, BigDecimal> food : group.getValue().entrySet()) {
                if (food.getKey().equals(order)) {
                    return food.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public String getOrder() {
        LOGGER.info("getting sum of all allOrders...");
        StringBuilder response = new StringBuilder("Заказ\n\n");
        Set<Map.Entry<String, List<Map<String, BigDecimal>>>> userOrders = allOrders.entrySet();
        BigDecimal sum = new BigDecimal(0.0);
        Map<String, Integer> groupedSushi = new HashMap<>();
        for (Map.Entry<String, List<Map<String, BigDecimal>>> userOrder : userOrders) {
            response.append(userOrder.getKey());
            response.append(": ");
            List<Map<String, BigDecimal>> orders = userOrder.getValue();
            StringBuilder customerOrderString = new StringBuilder();
            BigDecimal customerSum = new BigDecimal(0.0);
            for (Map<String, BigDecimal> customerOrder : orders) {
                Set<Map.Entry<String, BigDecimal>> sushies = customerOrder.entrySet();
                for (Map.Entry<String, BigDecimal> sushi : sushies) {
                    String sushiName = sushi.getKey();
                    String value = sushiName + "(" + sushi.getValue() + ")";
                    customerOrderString.append(customerOrderString.length() > 0 ? ", " + value : value);
                    customerSum = customerSum.add(sushi.getValue());

                    if (groupedSushi.containsKey(sushiName)) {
                        groupedSushi.put(sushiName, groupedSushi.get(sushiName) + 1);
                    } else {
                        groupedSushi.put(sushiName, 1);
                    }
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
        response.append(" (");

        String allPositions = groupedSushi.entrySet().stream()
                .map(e -> e.getKey() + " - " + e.getValue())
                .collect(Collectors.joining(", "));
        response.append(allPositions);

        response.append(")");
        LOGGER.info("Orders sum has been calculated: " + sum);
        return response.toString();
    }

    @Override
    public boolean removeOrder(String customer) {
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
    public Map<String, Map<String, BigDecimal>> getAllFood() {
        return food;
    }

}
