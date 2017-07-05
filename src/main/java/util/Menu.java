package util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * This is a singleton-util class that composes and provides menu.
 */
public class Menu {

    private static final Logger LOGGER = LoggerFactory.getLogger(Menu.class);

    private static final String MENU_FILE_EXTENSION = "properties";
    private static final String MENU_FILE_ENCODING = "UTF-8";

    private Map<String, Map<String, Double>> menu;

    public Menu() throws Exception {
        String menu = System.getProperty("menu");
        initializeMenu(StringUtils.isNotEmpty(menu) ? menu : ".");
    }

    private void initializeMenu(String menuPath) throws Exception {
        menu = new LinkedHashMap<>();

        File menuFolder = new File(menuPath);
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
            Map<String, Double> options = menu.entrySet().stream().collect(
                    Collectors.toMap(e -> (String) e.getKey(), e -> Double.valueOf((String) e.getValue())));
            this.menu.put(menuName, options);
        }
    }

    public Double getPrice(String order) {
        for (Map.Entry<String, Map<String, Double>> group : menu.entrySet()) {
            for (Map.Entry<String, Double> food : group.getValue().entrySet()) {
                if (food.getKey().equals(order)) {
                    return food.getValue();
                }
            }
        }
        return null;
    }

    public Map<String, Map<String, Double>> getAllMenu() {
        return menu;
    }
}
