package uk.ac.ncl.tavns.controller;

import org.jfree.data.time.TimeSeries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Properties;

public class Utilities {
    private static final Logger logger = LoggerFactory.getLogger(Utilities.class);

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

    /**
     * Return a properties class with the default properties set to default values
     * @param properties
     */
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

    /**
     * Save data gathered in TimeSeries to a csv file. A time-date stamp is used for the filename
     * @param timeSeries An array of time series to be saved to a single file
     */
    public static boolean saveData(TimeSeries[] timeSeries, String participantID, ArrayList<String> columnTitles) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss");
        LocalDateTime now = LocalDateTime.now();
        File filename = new File(participantID + "_" + dtf.format(now) + ".csv");
        try {
            FileWriter fileWriter = new FileWriter(filename);
            int number_of_series = timeSeries.length;
            int itemCount = timeSeries[0].getItemCount();

            // Write headers for columns
            fileWriter.write("TimeStamp,");
            for (int i = 0; i < columnTitles.size(); i++) {
                String comma = (i == (columnTitles.size() - 1)?"":",");
                fileWriter.write(columnTitles.get(i) + comma);
            }
            fileWriter.write("\n");

            // Write timeseries data - each series in a column
            for (int i = 0; i < itemCount; i++) {
                fileWriter.write(timeSeries[0].getDataItem(i).getPeriod().getMiddleMillisecond() + ",");
                for (int n = 0; n < number_of_series; n++) {
                    String comma = (n == (number_of_series - 1)?"":",");
                    fileWriter.write(timeSeries[n].getDataItem(i).getValue() + comma);
                }
                fileWriter.write("\n");
            }
            fileWriter.close();
            return true;
        } catch (IOException ex) {
            logger.error("Data not saved");
            ex.printStackTrace();
            return false;
            //throw new RuntimeException(ex);
        }
    }


    /**
     * Normalise a value that falls in a range from rangemin to rangemax to fit in a range min to max
     * @param val
     * @param min
     * @param max
     * @param rangemin
     * @param rangemax
     * @return the normalised value
     */
    public static double normalise(double val, double min, double max, double rangemin, double rangemax) {
        return rangemin + ((val - min) * (rangemax - rangemin) / (max - min));
    }

}
