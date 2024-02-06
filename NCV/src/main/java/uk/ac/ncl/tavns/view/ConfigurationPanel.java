package uk.ac.ncl.tavns.view;

import net.miginfocom.swing.MigLayout;
import uk.ac.ncl.tavns.controller.Utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import static uk.ac.ncl.tavns.controller.Utilities.savePropertyFile;

public class ConfigurationPanel extends JPanel implements ActionListener {
    private static JTextField tf_aiChannels = new JTextField(5);
    private static JTextField tf_rangeMinimum = new JTextField(5);
    private static JTextField tf_rangeMaximum = new JTextField( 5);
    private static JTextField tf_samplesPerChannel = new JTextField( 5);
    private static JTextField tf_inputDevice = new JTextField(5);
    private static JTextField tf_outputDevice = new JTextField(5);
    private JButton save = new JButton("Save");

    public ConfigurationPanel() {
        super();
        Properties properties = Utilities.loadProperties();
        tf_aiChannels.setText(properties.getProperty("number_of_ai_channels"));
        tf_rangeMaximum.setText(properties.getProperty("plot_range_maximum"));
        tf_rangeMinimum.setText(properties.getProperty("plot_range_minimum"));
        tf_samplesPerChannel.setText(properties.getProperty("samples_per_channel"));
        tf_inputDevice.setText(properties.getProperty("input_device"));
        tf_outputDevice.setText(properties.getProperty("output_device"));
        MigLayout migLayout = new MigLayout("wrap 5");
        setLayout(migLayout);
        add(new Label("Initial Plot Ranges: "));
        add(new Label("Minimum"));
        add(tf_rangeMinimum);
        add(new Label("Maximum"));
        add(tf_rangeMaximum, "wrap");
        add(new Label("Input Device: "));
        add(tf_inputDevice);
        add(new Label("Output Device: "));
        add(tf_outputDevice, "wrap");
        add(new Label("Samples per channel: "));
        add(tf_samplesPerChannel, "wrap");
        add(new Label("Number of Analogue Input channels: "));
        add(tf_aiChannels, "wrap");
        add(save);

        save.addActionListener(this);

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Save")) {
            Properties properties = new Properties();
            properties.setProperty("plot_range_minimum", tf_rangeMinimum.getText());
            properties.setProperty("plot_range_maximum", tf_rangeMaximum.getText());
            properties.setProperty("samples_per_channel", tf_samplesPerChannel.getText());
            properties.setProperty("number_of_ai_channels", tf_aiChannels.getText());
            properties.setProperty("input_device", tf_inputDevice.getText());
            properties.setProperty("output_device", tf_outputDevice.getText());
            savePropertyFile(properties);
        }
    }


}
