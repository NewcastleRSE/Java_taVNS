package uk.ac.ncl.tavns;

import org.jfree.ui.RefineryUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ncl.tavns.controller.AnalogueInput;
import uk.ac.ncl.tavns.controller.StimProtocols;
import uk.ac.ncl.tavns.controller.Utilities;
import uk.ac.ncl.tavns.view.*;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Properties;

/**
 * NiDAQ Channel Viewer
 */
public class NCV extends JFrame {

    private static final Logger logger = LoggerFactory.getLogger(NCV.class);
    private final AnalogueInput analogueInput;
    private ChartsPanel chartsPanel;
    private ButtonControlsPanel buttonControlsPanel;
    private ConfigurationPanel configurationPanel;
    private StimulationConfigurationPanel stimulationConfigurationPanel;
    private PanelCollection panelCollection = new PanelCollection(buttonControlsPanel, chartsPanel, configurationPanel,
            stimulationConfigurationPanel);
    private StimProtocols stimProtocols;


    /**
     * Create the main window GUI. This should actually move to a separate class in the view package"
     *
     * @param title the frame title.
     */
    public NCV(String title) {
        super(title);
        // Load properties
        Properties properties = Utilities.loadProperties();
        int numberOfChannels = Integer.parseInt(properties.getProperty("number_of_ai_channels"));
        int numSampsPerChan = Integer.parseInt(properties.getProperty("samples_per_channel"));
        String inputDevice = properties.getProperty("input_device");
        String outputDevice = properties.getProperty("output_device");
        String analogueOutputChannel = properties.getProperty("analogue_output_channel");
        String digitalOutputChannel = properties.getProperty("digital_output_channel");

        analogueInput = new AnalogueInput(numberOfChannels, numSampsPerChan, inputDevice);
        chartsPanel = new ChartsPanel(panelCollection, numberOfChannels, analogueInput, outputDevice);
        stimProtocols = new StimProtocols(chartsPanel);
        panelCollection.setStimProtocols(stimProtocols);
        configurationPanel = new ConfigurationPanel(panelCollection);
        panelCollection.setChartsPanel(chartsPanel);
        stimulationConfigurationPanel = new StimulationConfigurationPanel(panelCollection, outputDevice,
                analogueOutputChannel, digitalOutputChannel);
        final JTabbedPane tabbedPane = new JTabbedPane();
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
        logger.debug("DAQ closed");
        if (yesOrNo == JOptionPane.CANCEL_OPTION) {
            logger.debug("Save cancelled");
        } else if (yesOrNo == JOptionPane.YES_OPTION) { // if YES (0) then save and exit
            String participantID = panelCollection.getStimulationConfiguration().getTf_participantID().getText().trim();
            while (participantID == null || participantID.isEmpty())
                participantID = JOptionPane.showInputDialog(null, "Please enter a particpant ID",
                        "", JOptionPane.QUESTION_MESSAGE);
            Utilities.saveData(analogueInput.getTimeSeries(), participantID, chartsPanel.getChartTitles());
            logger.debug("Data saved, Exit program");
            System.exit(0);

        } else {
            logger.debug("Exit program");
            System.exit(0);
        }
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
        logger.debug("Application start");
        final NCV ncv = new NCV("NIDAQ Channel Visualiser - Version 0.4.1");
        ncv.pack();
        RefineryUtilities.centerFrameOnScreen(ncv);
        ncv.setVisible(true);
    }

    public ButtonControlsPanel getButtonControlsPanel() {
        return this.buttonControlsPanel;
    }
}