package uk.ac.ncl.tavns.view;

import eu.hansolo.custom.SteelCheckBox;
import kirkwood.nidaq.access.NiDaqException;
import org.jfree.chart.ChartPanel;
import org.jfree.data.time.TimeSeries;
import uk.ac.ncl.tavns.controller.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

/**
 * @author Jannetta S. Steyn
 * Panel containing buttons to control graphs
 * MigLayout documentation: https://www.miglayout.com/mavensite/docs/QuickStart.pdf
 */
public class ButtonControlsPanel extends JPanel implements ActionListener {

    private final JButton startTrace = new JButton("Stop");

    private final AnalogueInput analogueInput;
    private final String outputDevice;
    private byte outputState = 0;
    private JTextField txt_stimValue = new JTextField("2.5");
    private PanelCollection panelCollection;
    private Properties properties;
    // to here

    /**
     * Panel containing buttons and input fields. Displayed at the top of the charts panel
     * @param panelCollection
     * @param analogueInput
     * @param outputDevice
     */
    public ButtonControlsPanel(PanelCollection panelCollection, AnalogueInput analogueInput, String outputDevice) {
        properties = Utilities.loadProperties();
        this.analogueInput = analogueInput;
        this.outputDevice = outputDevice;
        this.panelCollection = panelCollection;
        setPreferredSize(new Dimension(1000, 30));
        Border lineBorder = BorderFactory.createLineBorder(Color.black);
        setBorder(lineBorder);
        setLayout(new FlowLayout());
        startTrace.setBackground(Color.ORANGE);
        add(startTrace);
        add(new JSeparator(SwingConstants.VERTICAL));
        SteelCheckBox steelCheckBox = new SteelCheckBox();
        add(steelCheckBox);
        // check this
        JButton digOut = new JButton("Dig Out");
        add(digOut);
        JButton testRampStim = new JButton("Test Ramp Stim");
        add(testRampStim);
        add(txt_stimValue);
        JButton stim = new JButton("Analogue Stim");
        add(stim);
        JButton save = new JButton("Save");
        add(save);
        JButton reset = new JButton("Reset");
        add(reset);
        JButton resize = new JButton("Resize");
        add(resize);

        startTrace.addActionListener(this);
        digOut.addActionListener(this);
        save.addActionListener(this);
        reset.addActionListener(this);
        resize.addActionListener(this);
        steelCheckBox.addItemListener(e -> {
            outputState = e.getStateChange() == 1 ? (byte)1 : (byte)0;
            System.out.println(outputState);
        });
        testRampStim.addActionListener(this);
        stim.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Stop")) {
            startTrace.setText("Start");
            startTrace.setBackground(new Color(1, 106, 180));
            startTrace.setForeground(Color.WHITE);
            analogueInput.setIsRunning(false);
        } else if (e.getActionCommand().equals("Start")) {
            startTrace.setText("Stop");
            startTrace.setBackground(Color.ORANGE);
            startTrace.setForeground(Color.BLACK);
            analogueInput.setIsRunning(true);
        } else if (e.getActionCommand().equals("Reset")) {
            // clear graphs
            // reset timeseries
            int number_of_series = analogueInput.getTimeSeries().length;
            TimeSeries[] timeSeries = analogueInput.getTimeSeries();
            analogueInput.setIsRunning(false);
            System.out.println("Timeseries length: " + timeSeries.length);
            if (timeSeries[0].getItemCount() > 0)
                for (int n = 0; n < number_of_series; n++) {
                    timeSeries[n].delete(0, timeSeries[n].getItemCount() - 1);
                }
            analogueInput.setIsRunning(true);
        } else if (e.getActionCommand().equals("Resize")) {
            properties = Utilities.loadProperties();
            ChartsPanel chartsPanel = panelCollection.getChartsPanel();
            ChartPanel[] chartPanel = chartsPanel.getChartPanel();
            System.out.println("Resize panels to: " + properties.getProperty("chart_width") +
                    ", " + properties.getProperty("chart_height"));
            for (int i = 0; i < chartPanel.length; i++) {
                chartPanel[i].setPreferredSize(new Dimension(Integer.parseInt(properties.getProperty("chart_width")),
                        Integer.parseInt(properties.getProperty("chart_height"))));
                chartPanel[i].setSize(new Dimension(Integer.parseInt(properties.getProperty("chart_width")),
                        Integer.parseInt(properties.getProperty("chart_height"))));
                chartPanel[i].revalidate();
            }
            chartsPanel.revalidate();
            chartsPanel.repaint();
            this.revalidate();
            this.repaint();
        } else if (e.getActionCommand().equals("Save")) {
            TimeSeries[] timeSeries = analogueInput.getTimeSeries();
            analogueInput.setIsRunning(false);
            Utilities.saveData(timeSeries);
            analogueInput.setIsRunning(true);
        } else if (e.getActionCommand().equals("Dig Out")) {
            StimProtocols.digitalOutSetState(outputDevice, outputState, "/port0/line0");

        } else if (e.getActionCommand().equals("Test Ramp Stim")) {
            Thread thread = new Thread(new AnalogueRamp(outputDevice, "ao1",
                    "AOTask", 10, 200));
            thread.start();
        } else if (e.getActionCommand().equals("Analogue Stim")) {
            String sval = txt_stimValue.getText();
            double stimValue = (sval.equals("") || sval==null)?0:Double.parseDouble(txt_stimValue.getText());
            try {
                Thread thread = new Thread( new AnalogueWrite(outputDevice, "ao1",
                        "AOTask", stimValue));
                thread.start();
            } catch (NiDaqException ex) {
                if (ex.toString().equals("DAQmxErrorInvalidAODataWrite")) {

                }
                throw new RuntimeException(ex);
            }
        } else {
            System.out.println(e.getActionCommand());
        }
    }
}
