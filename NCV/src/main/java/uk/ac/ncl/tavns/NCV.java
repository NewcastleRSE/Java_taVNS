package uk.ac.ncl.tavns;

import org.jfree.ui.RefineryUtilities;
import uk.ac.ncl.tavns.controller.AnalogueInput;
import uk.ac.ncl.tavns.controller.DigitalOutput;
import uk.ac.ncl.tavns.controller.Utilities;
import uk.ac.ncl.tavns.view.ChartsPanel;
import uk.ac.ncl.tavns.view.ConfigurationPanel;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Properties;

/**
 * NiDAQ Channel Viewer
 */
public class NCV extends JFrame {

    private int numberOfChannels;
    private int numSampsPerChan;
    private String inputDevice;
    private String outputDevice;

    private AnalogueInput analogueInput;
    private DigitalOutput digitalOutput;

    /**
     * Constructs a new application frame.
     *
     * @param title the frame title.
     */
    public NCV(String title) {
        super(title);
        Properties properties = Utilities.loadProperties();
        numberOfChannels = Integer.parseInt(properties.getProperty("number_of_ai_channels"));
        numSampsPerChan = Integer.parseInt(properties.getProperty("samples_per_channel"));
        inputDevice = properties.getProperty("input_device");
        outputDevice = properties.getProperty("output_device");
        digitalOutput = new DigitalOutput(outputDevice);
        analogueInput = new AnalogueInput(numberOfChannels, numSampsPerChan, inputDevice);
        final JTabbedPane tabbedPane = new JTabbedPane();
        final ChartsPanel chartsPanel = new ChartsPanel(numberOfChannels, analogueInput);

        tabbedPane.add("Input Traces", chartsPanel);
        tabbedPane.add("Configuration", new ConfigurationPanel()); // add dummy panel for the moment
        setContentPane(tabbedPane);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent arg0) {
                closeDaq();
                System.exit(0);
            }
        });
    }

    /**
     * Close the DAQ by setting the running flag to false.
     */
    private synchronized void closeDaq()  {
        setRunning(false);
        System.out.println("DAQ closed");
    }

    /**
     * Synchronised method for setting running
     * @param running boolean value to run/stop thread
     */
    public synchronized void setRunning(boolean running) {
        analogueInput.setIsRunning(running);
    }

    /**
     * Start application
     * @param args
     */
    public static void main(String[] args) {
        final NCV ncv = new NCV("NIDAQ Channel Visualiser");
        ncv.pack();
        RefineryUtilities.centerFrameOnScreen(ncv);
        ncv.setVisible(true);
    }

}