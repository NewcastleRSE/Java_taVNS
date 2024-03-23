package uk.ac.ncl.tavns.view;

import eu.hansolo.custom.SteelCheckBox;
import kirkwood.nidaq.access.NiDaqException;
import net.miginfocom.swing.MigLayout;
import uk.ac.ncl.tavns.controller.StimParameters;
import uk.ac.ncl.tavns.controller.StimProtocols;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StimulationConfigurationPanel extends JPanel implements ActionListener {
    // Threshold for starting stimulation
    private JTextField tf_startThreshold = new JTextField("-0.6",5);
    // Should we use a ramp up?
    private JCheckBox cb_rampup = new JCheckBox("Ramp up", true);
    // How long should the rampup take?
    private SteelCheckBox cb_rise = new SteelCheckBox();
    private JTextField tf_rampupDuration = new JTextField("3", 5);
    // Threshold for discontinuing stimulation
//    private JTextField tf_stopThreshold = new JTextField("3",5);
    // Maximum duration of overall stimulation
    private JTextField tf_maxDuration = new JTextField("3", 5);
    private JTextField tf_stimValue = new JTextField("2.5", 5); // Stimulation value
    private JButton startStim = new JButton("Start stimulation");
    private String outputDevice;
    private String outputChannel;
    private boolean stimInitialised = false;
    private PanelCollection panelCollection;
    private StimProtocols stimProtocols;
    JPanel pnl_txtFields = new JPanel();
    JPanel pnl_buttons = new JPanel();
    StimParameters stimParameters = new StimParameters();

    /**
     * This panel contains the text fields for capturing the parameters for stimulation protocols. It also
     * holds the buttons
     * @param panelCollection
     * @param outputDevice
     * @param outputChannel
     */
    public StimulationConfigurationPanel(PanelCollection panelCollection, String outputDevice, String outputChannel) {
        super();
        this.panelCollection = panelCollection;
        this.outputDevice = outputDevice;
        this.outputChannel = outputChannel;
        this.stimProtocols = panelCollection.getStimProtocols();
        setLayout(new MigLayout());
        Border lineBorder = BorderFactory.createLineBorder(Color.black);
        setBorder(lineBorder);

        setPreferredSize(new Dimension(1000, 60));
        pnl_buttons.setLayout(new FlowLayout());
        pnl_txtFields.setLayout(new FlowLayout());
        startStim.setBackground(Color.ORANGE);
        startStim.setForeground(Color.BLACK);

        pnl_txtFields.add(new JLabel("Stim threshold"));
        pnl_txtFields.add(tf_startThreshold);
        pnl_txtFields.add(new JLabel(""));
        pnl_txtFields.add(cb_rampup);
        JPanel smallBox = new JPanel();
        smallBox.setBorder(lineBorder);
        cb_rise.setText("Fall <=> Rise");
        smallBox.add(cb_rise);
        pnl_txtFields.add(smallBox);
        pnl_txtFields.add(new JLabel("# stims in ramp"));
        pnl_txtFields.add(tf_rampupDuration);
        pnl_txtFields.add(new JLabel("Maximum stimulation duration"));
        pnl_txtFields.add(tf_maxDuration);
        pnl_txtFields.add(new JLabel("Stimulation value"));
        pnl_txtFields.add(tf_stimValue);

        pnl_buttons.add(startStim);
        add(pnl_txtFields, "wrap");
        add(pnl_buttons);

        startStim.addActionListener(this);


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Start stimulation")) {
            startStim.setText("Stop stimulation");
            startStim.setBackground(new Color(1, 106, 180));
            startStim.setForeground(Color.WHITE);
            try {
                populateStimParameters();
                if (!stimInitialised) {
                    if (stimProtocols == null) {
                        stimProtocols = new StimProtocols(panelCollection.getChartsPanel());
                    }
                    stimInitialised = stimProtocols.thresholdStimInit(stimParameters);
                }
                stimProtocols.thresholdStimStart();
            } catch (NiDaqException ex) {
                throw new RuntimeException(ex);
            }

        }
        if (e.getActionCommand().equals("Stop stimulation")) {
            startStim.setText("Start stimulation");
            populateStimParameters();
            startStim.setBackground(Color.ORANGE);
            startStim.setForeground(Color.BLACK);
            stimProtocols.thresholdStimStop();
        }

    }

    public static double[] concatenateArrays(double[] array1, double[] array2) {
        // Determine the length of the concatenated array
        int totalLength = array1.length + array2.length;

        // Create a new array to store the concatenated elements
        double[] result = new double[totalLength];

        // Copy elements from the first array
        System.arraycopy(array1, 0, result, 0, array1.length);

        // Copy elements from the second array
        System.arraycopy(array2, 0, result, array1.length, array2.length);

        return result;
    }

    /**
     * Populate the StimParameters class from the GUI widgets.
     */
    public void populateStimParameters() {
        stimParameters.setOutputDevice(outputDevice);
        stimParameters.setOutputChannel(outputChannel);
        if (stimParameters.getTaskName() == null) stimParameters.setTaskName("");
        stimParameters.setStimValue(Double.parseDouble(tf_stimValue.getText()));
        stimParameters.setStimThreshold(Double.parseDouble(tf_startThreshold.getText()));
        stimParameters.setRise(cb_rise.isSelected());
        stimParameters.setRampUp(cb_rampup.isSelected());
        stimParameters.setStimDuration(Double.parseDouble(tf_maxDuration.getText()));
    }

}
