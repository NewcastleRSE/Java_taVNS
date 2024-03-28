package uk.ac.ncl.tavns.controller;

import org.jfree.data.time.TimeSeries;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
            FileOutputStream out = new FileOutputStream(propertiesFile);
            properties.store(out, "");
            out.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Load properties from system.properties file
     */
    public static Properties loadProperties() {
        Properties properties = new Properties();
        String configDirectory = System.getProperty("user.home").concat("/.Java_taVNS/");
        String propertiesFile = configDirectory.concat("/system.properties");
        try {
            File f = new File(propertiesFile);
            // If the file doesn't exist, create it
            Files.createDirectories(Paths.get(configDirectory));

            if (!(f.exists())) {
                OutputStream out = new FileOutputStream(f);
                out.close();
            }
            InputStream is = new FileInputStream(f);
            properties.load(is);
            if (properties.isEmpty()) {
                defaultProperties(properties);
                FileOutputStream out = new FileOutputStream(propertiesFile);
                properties.store(out, "");
                out.close();
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    private static void defaultProperties(Properties properties) {
        System.out.println("Create properties file");
        properties.setProperty("plot_range_minimum", "0");
        properties.setProperty("plot_range_maximum", "1");
        properties.setProperty("samples_per_channel", "1");
        properties.setProperty("number_of_ai_channels", "3");
        properties.setProperty("input_device", "Dev1");
        properties.setProperty("output_device", "Dev1");
        properties.setProperty("chart_width", "1000");
        properties.setProperty("chart_height", "720");
        properties.setProperty("digital_output_channel", "port0/line0");
        properties.setProperty("analogue_output_channel", "ao1");
    }

    public static void saveData(TimeSeries[] timeSeries) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss");
        LocalDateTime now = LocalDateTime.now();
        File filename = new File(dtf.format(now) + ".csv");
        try {
            FileWriter fileWriter = new FileWriter(filename);
            int number_of_series = timeSeries.length;
            int itemCount = timeSeries[0].getItemCount();
            for (int i = 0; i < itemCount; i++) {
                String comma = (i == (itemCount-1)?"":",");
                fileWriter.write(timeSeries[0].getDataItem(i).getPeriod().getMiddleMillisecond()+ comma);
            }
            for (int n = 0; n < number_of_series; n++) {

                fileWriter.write("\n");
                for (int i = 0; i < itemCount; i++) {
                    String comma = (i == (itemCount - 1)?"":",");
                    fileWriter.write(timeSeries[n].getDataItem(i).getValue() + comma);
                }
                fileWriter.write("\n");
            }
            fileWriter.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }


    public static double normalise(double val, double min, double max, double rangemin, double rangemax) {
        return rangemin + ((val - min) * (rangemax - rangemin) / (max - min));
    }

}
