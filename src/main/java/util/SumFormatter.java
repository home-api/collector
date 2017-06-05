package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class SumFormatter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SumFormatter.class);

    private static final Double DELIVERY_PRICE = 2.5;

    private SumFormatter() {
    }

    public static String format(Map<String, List<Map<String, Double>>> allOrders) {
        LOGGER.info("getting sum of all allOrders...");
        StringBuilder response = new StringBuilder(
                "Заказ на "
                        + DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("ru")).format(LocalDate.now())
                        + "\n\n");
        Set<Map.Entry<String, List<Map<String, Double>>>> userOrders = allOrders.entrySet();
        BigDecimal deliveryPerUser = calculateUserDelivery(DELIVERY_PRICE, userOrders.size());
        BigDecimal sum = new BigDecimal(0.0).setScale(2, BigDecimal.ROUND_HALF_UP);
        Map<String, Integer> groupedSushi = new HashMap<>();
        for (Map.Entry<String, List<Map<String, Double>>> userOrder : userOrders) {
            response.append(userOrder.getKey());
            response.append(": ");
            List<Map<String, Double>> orders = userOrder.getValue();
            StringBuilder customerOrderString = new StringBuilder();
            BigDecimal customerSum = new BigDecimal(0.0).setScale(2, BigDecimal.ROUND_HALF_UP);
            for (Map<String, Double> customerOrder : orders) {
                Set<Map.Entry<String, Double>> sushies = customerOrder.entrySet();
                for (Map.Entry<String, Double> sushi : sushies) {
                    String sushiName = sushi.getKey();
                    String value = sushiName + "(" + sushi.getValue() + ")";
                    customerOrderString.append(customerOrderString.length() > 0 ? ", " + value : value);
                    customerSum = customerSum.add(BigDecimal.valueOf(sushi.getValue()));

                    if (groupedSushi.containsKey(sushiName)) {
                        groupedSushi.put(sushiName, groupedSushi.get(sushiName) + 1);
                    } else {
                        groupedSushi.put(sushiName, 1);
                    }
                }
            }

            customerOrderString.append(", доставка(" + deliveryPerUser + ")");
            customerSum = customerSum.add(deliveryPerUser);

            sum = sum.add(customerSum);

            response.append(customerOrderString);
            response.append(" - ");
            response.append(customerSum.stripTrailingZeros());
            response.append(" денег.");
            response.append("\n\n");

        }
        response.append("Итого: ");
        response.append(sum.stripTrailingZeros());
        response.append(" денег");
        response.append("\n");

        String allPositions = groupedSushi.entrySet().stream()
                .map(e -> e.getKey() + " - " + e.getValue())
                .collect(Collectors.joining("\n"));
        response.append(allPositions);

        LOGGER.info("Orders sum has been calculated: " + sum);
        return response.toString();
    }

    static BigDecimal calculateUserDelivery(Double delivery, int users) {
        double roundedDeliveryPrice = users > 0 ? Math.ceil(delivery * 100 / users) / 100 : delivery;
        return new BigDecimal(roundedDeliveryPrice).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

}
