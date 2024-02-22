package uk.ac.ncl.tavns;

import org.jfree.ui.RefineryUtilities;
import uk.ac.ncl.tavns.controller.AnalogueInput;
import uk.ac.ncl.tavns.controller.Utilities;
import uk.ac.ncl.tavns.view.ButtonControlsPanel;
import uk.ac.ncl.tavns.view.ChartsPanel;
import uk.ac.ncl.tavns.view.ConfigurationPanel;
import uk.ac.ncl.tavns.view.PanelCollection;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Properties;

/**
 * NiDAQ Channel Viewer
 */
public class NCV extends JFrame {

    private final AnalogueInput analogueInput;
    ChartsPanel chartsPanel;
    ButtonControlsPanel buttonControlsPanel;
    ConfigurationPanel configurationPanel;
    PanelCollection panelCollection = new PanelCollection(buttonControlsPanel, chartsPanel, configurationPanel);


    /**
     * Create the main window GUI. This should actually move to a separate class in the view package"
     *
     * @param title the frame title.
     */
    public NCV(String title) {
        super(title);
        Properties properties = Utilities.loadProperties();
        int numberOfChannels = Integer.parseInt(properties.getProperty("number_of_ai_channels"));
        int numSampsPerChan = Integer.parseInt(properties.getProperty("samples_per_channel"));
        String inputDevice = properties.getProperty("input_device");
        String outputDevice = properties.getProperty("output_device");
        analogueInput = new AnalogueInput(numberOfChannels, numSampsPerChan, inputDevice);
        final JTabbedPane tabbedPane = new JTabbedPane();
        chartsPanel = new ChartsPanel(panelCollection, numberOfChannels, analogueInput, outputDevice);
        panelCollection.setChartsPanel(chartsPanel);
        configurationPanel = new ConfigurationPanel(panelCollection);
        tabbedPane.add("Input Traces", chartsPanel);
        tabbedPane.add("Configuration", configurationPanel);
        setContentPane(tabbedPane);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent arg0) {
                closeDaq();
            }
        });
    }

    /**
     * Close the DAQ by setting the running flag to false.
     */
    private synchronized void closeDaq() {
        var yesOrNo = JOptionPane.showConfirmDialog(null, "Save data before quitting?", "Quit NCV?", JOptionPane.YES_NO_OPTION);
        setRunning(false);
        System.out.println("DAQ closed");
        if (yesOrNo == JOptionPane.YES_NO_OPTION) { // if YES (0) then save and exit
            Utilities.saveData(analogueInput.getTimeSeries());
        }
        System.exit(0);
        System.out.println("DAQ closed");

    }

    /**
     * Synchronised method for setting running
     *
     * @param running boolean value to run/stop thread
     */
    public synchronized void setRunning(boolean running) {
        analogueInput.setIsRunning(running);
    }

    /**
     * Start application
     *
     * @param args - not used at the moment
     */
    public static void main(String[] args) {
        final NCV ncv = new NCV("NIDAQ Channel Visualiser");
        ncv.pack();
        RefineryUtilities.centerFrameOnScreen(ncv);
        ncv.setVisible(true);
    }

}