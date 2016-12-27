package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class SumFormatter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SumFormatter.class);

    private SumFormatter() {
    }

    public static String format(Map<String, List<Map<String, BigDecimal>>> allOrders) {
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
            response.append(" денег.");
            response.append("\n\n");

        }
        response.append("Итого: ");
        response.append(sum);
        response.append(" денег");
        response.append("\n");

        String allPositions = groupedSushi.entrySet().stream()
                .map(e -> e.getKey() + " - " + e.getValue())
                .collect(Collectors.joining("\n"));
        response.append(allPositions);

        LOGGER.info("Orders sum has been calculated: " + sum);
        return response.toString();
    }

}
