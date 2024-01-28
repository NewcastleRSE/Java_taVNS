package uk.ac.ncl.tavns.controller;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class Utilities {

    /**
     * Set the specified property with the specified value
     *
     */
    public static void savePropertyFile(Properties properties) {
        String configDirectory = System.getProperty("user.home").concat("/.Java_taVNS/");
        String propertiesFile = configDirectory.concat("/system.properties");
        File f = new File(propertiesFile);
        // If the file doesn't exist, create it
        try {
            Files.createDirectories(Paths.get(configDirectory));
            if (!(f.exists())) {
                OutputStream out = new FileOutputStream(f);
                out.close();
            }
            InputStream is = new FileInputStream(f);
            properties.load(is);
            if (properties.isEmpty()) {
                properties.setProperty("plot_range_minimum", "0");
                properties.setProperty("plot_range_maximum", "1");
                properties.setProperty("samples_per_channel", "1");
            }
            System.out.println("Save properties to " + propertiesFile);
            FileOutputStream out = new FileOutputStream(propertiesFile);
            properties.store(out, "");
            is.close();
            out.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
