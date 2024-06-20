package uk.ac.ncl.tavns.view;

import uk.ac.ncl.SteelCheckBox.custom.SteelCheckBox;
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
    /**
     * An id for the participant to be appended to the beginning of the filename
     */
    private JTextField tf_participantID = new JTextField(15);
    /**
     Threshold for starting stimulation
     **/
    private JTextField tf_startThreshold = new JTextField("-0.6",5); // threshold at which to start stim
    /**
     * Should we use a ramp up?
     */
    private JCheckBox cb_rampup = new JCheckBox("Ramp up", true); // if true do ramp up
    // How long should the rampup take?
    private SteelCheckBox cb_rise = new SteelCheckBox(); // if true stim on voltage rise
    private JTextField tf_numberOfSpikes = new JTextField("3", 5); // number of stims in the rampup
//    private JTextField tf_maxDuration = new JTextField("3", 5); //
    private JTextField tf_stimValue = new JTextField("2.5", 5); // voltage to stimulate at
    /**
     * Time period of stim
     */
    private JTextField tf_stimLength = new JTextField("165", 5);
    /**
     * Time period between stims
     */
    private JTextField tf_restPeriod = new JTextField("165", 5);
    private JButton startStim = new JButton("Start stimulation"); // start stimulating
    private String outputDevice;
    private String analogueOutputChannel;
    private String digitalOutputChannel;
    private boolean stimInitialised = false;
    private PanelCollection panelCollection;
    private StimProtocols stimProtocols;
    private JPanel pnl_txtFields = new JPanel();
    private JPanel pnl_buttons = new JPanel();
    private StimParameters stimParameters = new StimParameters();

    /**
     * This panel contains the text fields for capturing the parameters for stimulation protocols. It also
     * holds the buttons
     * @param panelCollection
     * @param outputDevice
     * @param analogueOutputChannel
     */
    public StimulationConfigurationPanel(PanelCollection panelCollection, String outputDevice,
                                         String analogueOutputChannel, String digitalOutputChannel) {
        super();
        this.panelCollection = panelCollection;
        this.outputDevice = outputDevice;
        this.analogueOutputChannel = analogueOutputChannel;
        this.digitalOutputChannel = digitalOutputChannel;
        this.stimProtocols = panelCollection.getStimProtocols();
        setLayout(new MigLayout());
        Border lineBorder = BorderFactory.createLineBorder(Color.black);
        setBorder(lineBorder);

        setPreferredSize(new Dimension(1000, 60));
        pnl_buttons.setLayout(new FlowLayout());
        pnl_txtFields.setLayout(new MigLayout("", "[][][][][][][]",""));
        startStim.setBackground(Color.ORANGE);
        startStim.setForeground(Color.BLACK);

        pnl_txtFields.add(new JLabel("Participant ID"));
        pnl_txtFields.add(tf_participantID, "wrap");
        pnl_txtFields.add(new JLabel("Stim threshold"));
        pnl_txtFields.add(tf_startThreshold);
        pnl_txtFields.add(new JLabel("# stims in ramp"));
        pnl_txtFields.add(tf_numberOfSpikes);
        pnl_txtFields.add(new JLabel("Stimulation value"));
        pnl_txtFields.add(tf_stimValue);
        pnl_txtFields.add(cb_rampup);
        pnl_txtFields.add(new JLabel("Stim length (ms)"));
        pnl_txtFields.add(tf_stimLength);
        pnl_txtFields.add(new JLabel("Period between stims (ms)"));
        pnl_txtFields.add(tf_restPeriod, "wrap");
        JPanel smallBox = new JPanel();
        smallBox.setBorder(lineBorder);
        JLabel lbl_rise = new JLabel("expiratory-gated <=> inspiratory-gated");
        cb_rise.setText("");
        smallBox.add(lbl_rise, "wrap");
        smallBox.add(cb_rise);
        pnl_txtFields.add(smallBox, "span");


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
        stimParameters.setOutputDevice(outputDevice); // output device
        stimParameters.setAnalogueOutputChannel(analogueOutputChannel); // analogue output channel
        stimParameters.setDigitalOutputChannel(digitalOutputChannel); // digital output channel
        if (stimParameters.getAnalogueTask() == null) stimParameters.setAnalogueTask(""); // task name
        if (stimParameters.getDigitalTask() == null) stimParameters.setDigitalTask("");
        stimParameters.setStimValue(Double.parseDouble(tf_stimValue.getText()));
        stimParameters.setStimThreshold(Double.parseDouble(tf_startThreshold.getText()));
        stimParameters.setRise(cb_rise.isSelected());
        stimParameters.setRampUp(cb_rampup.isSelected());
        stimParameters.setNumberOfSpikes(Long.parseLong(tf_numberOfSpikes.getText()));
        stimParameters.setSpikePeriod(Long.parseLong(tf_stimLength.getText()));
        stimParameters.setRestPeriod(Long.parseLong(tf_restPeriod.getText()));

    }

    public JTextField getTf_participantID() {
        return tf_participantID;
    }
}
