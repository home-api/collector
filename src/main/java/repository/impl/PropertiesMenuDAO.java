package repository.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.MenuDAO;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class PropertiesMenuDAO implements MenuDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesMenuDAO.class);

    private static final String MENU_FILE_NAME = "menu";
    private static final String MENU_FILE_EXTENSION = "properties";
    private static final String MENU_FILE_ENCODING = "UTF-8";

    private Map<String, Map<String, BigDecimal>> menu;

    public PropertiesMenuDAO() throws Exception {
        initializeMenu();
    }

    private void initializeMenu() throws Exception {
        menu = new HashMap<>();

        File menuFolder = new File(MENU_FILE_NAME);
        if (!menuFolder.exists()) {
            LOGGER.error("Menu folder does not exist: " + menuFolder.getAbsolutePath());
            throw new IllegalStateException("Menu folder does not exist" + menuFolder.getAbsolutePath());
        }

        Collection<File> menuFiles = FileUtils.listFiles(
                menuFolder,
                new String[]{MENU_FILE_EXTENSION},
                false);

        if (menuFiles.isEmpty()) {
            LOGGER.error("Menu folder is empty");
            throw new IllegalStateException("Menu folder is empty");
        }

        for (File menuFile : menuFiles) {
            LOGGER.info("Initializing: " + menuFile);
            String menuName = FilenameUtils.getBaseName(menuFile.getAbsolutePath());
            Properties menu = new Properties();
            menu.load(new InputStreamReader(new FileInputStream(menuFile), MENU_FILE_ENCODING));
            Map<String, BigDecimal> options = menu.entrySet().stream().collect(
                    Collectors.toMap(e -> (String) e.getKey(), e -> new BigDecimal((String) e.getValue())));
            this.menu.put(menuName, options);
        }
    }

    @Override
    public BigDecimal getPrice(String order) {
        for (Map.Entry<String, Map<String, BigDecimal>> group : menu.entrySet()) {
            for (Map.Entry<String, BigDecimal> food : group.getValue().entrySet()) {
                if (food.getKey().equals(order)) {
                    return food.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public Map<String, Map<String, BigDecimal>> getAllMenu() {
        return menu;
    }
}
