package uk.ac.ncl.tavns;

import org.jfree.ui.RefineryUtilities;
import uk.ac.ncl.tavns.controller.MakeData;
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

    private static int numberOfChannels;
    private final int numSampsPerChan;

    private MakeData makeDataThread;

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
        makeDataThread = new MakeData(numberOfChannels, numSampsPerChan);
        final JTabbedPane tabbedPane = new JTabbedPane();
        final ChartsPanel chartsPanel = new ChartsPanel(numberOfChannels, makeDataThread);

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
        makeDataThread.setIsRunning(running);
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