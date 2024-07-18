package uk.ac.ncl.tavns.view;

import kirkwood.nidaq.access.NiDaqException;
import net.miginfocom.swing.MigLayout;
import uk.ac.ncl.tavns.controller.StimParameters;
import uk.ac.ncl.tavns.controller.StimProtocols;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

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
//    private SteelCheckBox cb_rise = new SteelCheckBox(); // if true stim on voltage rise
    private ButtonGroup stimButtonGroup = new ButtonGroup();
    private JRadioButton rb_insp = new JRadioButton("Inspiratory Gated", true);
    private JRadioButton rb_exp = new JRadioButton("Expiratory Gated", true);
    private JRadioButton rb_cont = new JRadioButton("Continuous", true);
    private JTextField tf_numberOfSpikes = new JTextField("3", 5); // number of stims in the rampup
//    private JTextField tf_maxDuration = new JTextField("3", 5); //
    private JTextField tf_stimValue = new JTextField("0.1", 5); // voltage to stimulate at
    /**
     * Time period of stim
     */
    private JTextField tf_stimFrequency = new JTextField("15", 5);
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
    private JLabel lbl_stimAmp = new JLabel("10.0mA");

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
        pnl_txtFields.setLayout(new MigLayout("", "[][][][]",""));
        startStim.setBackground(Color.ORANGE);
        startStim.setForeground(Color.BLACK);
        JLabel lbl_participantID = new JLabel("Participant ID");
        pnl_txtFields.add(lbl_participantID);
        lbl_participantID.setToolTipText("Add a unique ID for the participant.");
        pnl_txtFields.add(tf_participantID, "wrap");
        tf_participantID.setToolTipText("Add a unique ID for the participant.");
        JLabel lbl_startThreshold = new JLabel("Stim threshold");
        pnl_txtFields.add(lbl_startThreshold);
        lbl_startThreshold.setToolTipText("The voltage threshold at which to start and stop stimulation.");
        pnl_txtFields.add(tf_startThreshold);
        tf_startThreshold.setToolTipText("The voltage threshold at which to start and stop stimulation.");
        JLabel lbl_numberOfSpikes = new JLabel("# stims in ramp");
        pnl_txtFields.add(lbl_numberOfSpikes);
        lbl_numberOfSpikes.setToolTipText("The number of stimuli to produce when ramping up.");
        pnl_txtFields.add(tf_numberOfSpikes, "wrap");
        tf_numberOfSpikes.setToolTipText("The number of stimuli to produce when ramping up.");
        JLabel lbl_stimValue = new JLabel("Stimulation peak amplitude (V)");
        pnl_txtFields.add(lbl_stimValue);
        lbl_stimValue.setToolTipText("Allow for any value between 0 and 0.2. This value is proportionally scaled to Amps by the Stimulator");
        pnl_txtFields.add(tf_stimValue);
        tf_stimValue.setToolTipText("Allow for any value between 0 and 0.2. This value is proportionally scaled to Amps by the Stimulator");
        tf_stimValue.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER){
                    updateCurrent();
                }
            }

        });
        pnl_txtFields.add(lbl_stimAmp, "span 2, wrap");

        // Check max value entered
        tf_stimValue.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                updateCurrent();
            }
        });

        pnl_txtFields.add(cb_rampup);
        pnl_txtFields.add(new JLabel("Frequency:"));
        pnl_txtFields.add(tf_stimFrequency);
        pnl_txtFields.add(new JLabel("(Hz)"), "wrap");

        JPanel pnl_stimButtons = new JPanel();
        pnl_stimButtons.setBorder(lineBorder);
        stimButtonGroup.add(rb_insp);
        stimButtonGroup.add(rb_exp);
        stimButtonGroup.add(rb_cont);
        rb_insp.setSelected(true);
        pnl_stimButtons.add(rb_insp);
        pnl_stimButtons.add(rb_exp);
        pnl_stimButtons.add(rb_cont);

//        JPanel smallBox = new JPanel();
//        smallBox.setBorder(lineBorder);
//        JLabel lbl_rise = new JLabel("expiratory-gated <=> inspiratory-gated");
//        lbl_rise.setToolTipText("Slide to the left to select expiratory-gated stimulation and to the right for inspirator-gate stimulation");
//        cb_rise.setText("");
//        cb_rise.setToolTipText("Slide to the left to select expiratory-gated stimulation and to the right for inspirator-gate stimulation");
//        smallBox.add(lbl_rise, "wrap");
//        smallBox.add(cb_rise);
//        pnl_txtFields.add(smallBox, "span");


        pnl_buttons.add(startStim);
        add(pnl_txtFields, "wrap");
        add(pnl_stimButtons, "wrap");
        add(pnl_buttons);

        startStim.addActionListener(this);


    }

    public void updateCurrent() {
        try {
            float stimValue = Float.parseFloat(tf_stimValue.getText());
            if (!(stimValue >= 0 && stimValue <= 0.20000005)) {
                tf_stimValue.setText("0");
                JOptionPane.showMessageDialog(null, "Enter a value between 0 and 0.2 volt");
            }
            lbl_stimAmp.setText(Float.toString(Float.parseFloat(tf_stimValue.getText()) * 100f) + "mA");
        } catch (NumberFormatException ne) {
            tf_stimValue.setText("0");
            JOptionPane.showMessageDialog(null, "Enter only a numerical value");
        }
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
        int stimValue = 0;
        if (rb_insp.isSelected()) stimValue = 1;
        if (rb_exp.isSelected()) stimValue = 2;
        stimParameters.setStim(stimValue);
//        stimParameters.setStim(cb_rise.isSelected());
        stimParameters.setRampUp(cb_rampup.isSelected());
        stimParameters.setNumberOfSpikes(Long.parseLong(tf_numberOfSpikes.getText()));
        stimParameters.setSpikeFrequency(Long.parseLong(tf_stimFrequency.getText()));

    }

    public JTextField getTf_participantID() {
        return tf_participantID;
    }
}
