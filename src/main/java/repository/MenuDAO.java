package repository;

import java.math.BigDecimal;
import java.util.Map;

public interface MenuDAO {

    /**
     * Returns entire menu.
     *
     * @return entire menu
     */
    Map<String, Map<String, BigDecimal>> getAllMenu();

    /**
     * Returns price of the menu item.
     *
     * @param menuItem menu item
     * @return price
     */
    BigDecimal getPrice(String menuItem);

}
