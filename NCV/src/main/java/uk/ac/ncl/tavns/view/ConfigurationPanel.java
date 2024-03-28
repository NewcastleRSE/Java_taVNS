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
    private static JTextField tf_digitalOutputChannel = new JTextField(5);
    private static JTextField tf_analogueOutputChannel = new JTextField(5);
    private static JTextField tf_chartWidth = new JTextField("1000",5);
    private static JTextField tf_chartHeight = new JTextField("270",5);
    private JButton save = new JButton("Save");
    PanelCollection panelCollection;

    public ConfigurationPanel(PanelCollection panelCollection) {
        super();
        this.panelCollection = panelCollection;
        Properties properties = Utilities.loadProperties();
        tf_aiChannels.setText(properties.getProperty("number_of_ai_channels"));
        tf_rangeMaximum.setText(properties.getProperty("plot_range_maximum"));
        tf_rangeMinimum.setText(properties.getProperty("plot_range_minimum"));
        tf_samplesPerChannel.setText(properties.getProperty("samples_per_channel"));
        tf_inputDevice.setText(properties.getProperty("input_device"));
        tf_outputDevice.setText(properties.getProperty("output_device"));
        tf_analogueOutputChannel.setText(properties.getProperty("analogue_output_port"));
        tf_digitalOutputChannel.setText(properties.getProperty("digital_output_port"));
        tf_chartHeight.setText(properties.getProperty("chart_height"));
        tf_chartWidth.setText(properties.getProperty("chart_width"));
        MigLayout migLayout = new MigLayout("wrap 5");
        setLayout(migLayout);
        add(new JLabel("Initial Plot Ranges: "));
        add(new JLabel("Minimum"));
        add(tf_rangeMinimum);
        add(new JLabel("Maximum"));
        add(tf_rangeMaximum, "wrap");

        add(new JLabel(""));
        add(new JLabel("Chart width: "));
        add(tf_chartWidth);
        add(new JLabel( "Chart height"));
        add(tf_chartHeight, "wrap");

        JSeparator jSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        this.add(jSeparator, "growx, span 6, wrap");
        add(new Label("Input Device: "));
        add(tf_inputDevice);
        add(new Label("Output Device: "));
        add(tf_outputDevice, "wrap");
        add(new Label("Analogue Output port"));
        add(tf_analogueOutputChannel);
        add(new Label("Digital Output port"));
        add(tf_digitalOutputChannel, "wrap");
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
            properties.setProperty("chart_width", tf_chartWidth.getText());
            properties.setProperty("chart_height", tf_chartHeight.getText());
            properties.setProperty("samples_per_channel", tf_samplesPerChannel.getText());
            properties.setProperty("number_of_ai_channels", tf_aiChannels.getText());
            properties.setProperty("input_device", tf_inputDevice.getText());
            properties.setProperty("output_device", tf_outputDevice.getText());
            properties.setProperty("digital_output_channel", tf_digitalOutputChannel.getText());
            properties.setProperty("analogue_output_channel", tf_analogueOutputChannel.getText());
            savePropertyFile(properties);


        }
    }


}
