package uk.ac.ncl.tavns;

import org.jfree.ui.RefineryUtilities;
import uk.ac.ncl.tavns.controller.MakeData;
import uk.ac.ncl.tavns.view.ChartsPanel;
import uk.ac.ncl.tavns.view.ConfigurationPanel;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * NiDAQ Channel Viewer
 */
public class NCV extends JFrame {

    private static int numberOfChannels = 3;
    private final int numSampsPerChan = 8;

    private MakeData makeDataThread = new MakeData(numberOfChannels, numSampsPerChan);

    /**
     * Constructs a new application frame.
     *
     * @param title the frame title.
     * @param channels the number of analogue input channels to read
     */
    public NCV(String title, int channels) {
        super(title);
        final JTabbedPane tabbedPane = new JTabbedPane();
        final ChartsPanel chartsPanel = new ChartsPanel(channels, makeDataThread);

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
        final NCV ncv = new NCV("NIDAQ Channel Visualiser", numberOfChannels);
        ncv.pack();
        RefineryUtilities.centerFrameOnScreen(ncv);
        ncv.setVisible(true);
    }

}