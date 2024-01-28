package uk.ac.ncl.tavns.view;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigurationPanel extends JPanel implements ActionListener {
    private static JTextField tf_rangeMinimum = new JTextField("0", 5);
    private static JTextField tf_rangeMaximum = new JTextField("100", 5);
    private static JTextField tf_samplesPerChannel = new JTextField("4", 5);
    private JButton save = new JButton("Save");

    public ConfigurationPanel() {
        super();
        MigLayout migLayout = new MigLayout("fillx", "[]rel[]rel[]rel[]rel[]", "[]10[]");
        setLayout(migLayout);
        add(new Label("Initial Plot Ranges: "));
        add(new Label("Minimum"));
        add(tf_rangeMinimum);
        add(new Label("Maximum"));
        add(tf_rangeMaximum, "wrap");
        add(new Label("Samples per channel: "));
        add(tf_samplesPerChannel, "wrap");
        add(save);

        save.addActionListener(this);

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Save")) {
            savePropertyFile();
        }
    }

    /**
     * Set the specified property with the specified value
     *
     */
    public static void savePropertyFile() {
        Properties properties = new Properties();
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
                properties.setProperty("plot_range_minimum", tf_rangeMinimum.getText());
                properties.setProperty("plot_range_maximum", tf_rangeMaximum.getText());
                properties.setProperty("samples_per_channel", tf_samplesPerChannel.getText());
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
