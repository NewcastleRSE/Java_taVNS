package uk.ac.ncl.tavns.view;

import kirkwood.nidaq.access.NiDaqException;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ncl.tavns.controller.StimParameters;
import uk.ac.ncl.tavns.controller.StimProtocols;
import uk.ac.ncl.tavns.controller.Utilities;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.Properties;

public class StimulationConfigurationPanel extends JPanel implements ActionListener {
    private static final Logger logger = LoggerFactory.getLogger(StimulationConfigurationPanel.class);
    Properties protocols = Utilities.loadProtocol("default");
    /**
     * ComboBox containing list of protocol files
     */
    JComboBox<String> protocol_list = new JComboBox<>(Utilities.getPropertiesFilesFromUserDirectory());
    /**
     * An id for the participant to be appended to the beginning of the filename
     */
    private JTextField tf_participantID = new JTextField(15);
    /**
     * Threshold for starting stimulation
     **/
    private JTextField tf_startThreshold = new JTextField(protocols.getProperty("threshold"),5); // threshold at which to start stim
    /**
     * Should we use a ramp up?
     */
    private JCheckBox cb_rampup = new JCheckBox("Ramp up", protocols.getProperty("ramp").equals("true")); // if true do ramp up
    private JRadioButton rb_insp = new JRadioButton("Inspiratory Gated", protocols.getProperty("stimType").equals("1"));
    private JRadioButton rb_exp = new JRadioButton("Expiratory Gated", protocols.getProperty("stimType").equals("2"));
    private JRadioButton rb_cont = new JRadioButton("Continuous", protocols.getProperty("stimType").equals("0"));
    private ButtonGroup stimButtonGroup = new ButtonGroup();

    private JTextField tf_numberOfSpikes = new JTextField(protocols.getProperty("stims"), 5); // number of stims in the rampup
    private JTextField tf_stimValue = new JTextField(protocols.getProperty("peak"), 5); // voltage to stimulate at
    /**
     * Time period of stim
     */
    private JTextField tf_stimFrequency = new JTextField(protocols.getProperty("frequency"), 5);
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
    private JLabel lbl_stimAmp = new JLabel(Float.parseFloat(tf_stimValue.getText()) * 100 + "mA");
    private JLabel lbl_protocol = new JLabel("Protocol: ");


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
        pnl_txtFields.add(tf_participantID);
        tf_participantID.setToolTipText("Add a unique ID for the participant.");

        pnl_txtFields.add(lbl_protocol);
        pnl_txtFields.add(protocol_list);
        protocol_list.addActionListener(this);
        JButton saveAs = new JButton("Save as");
        saveAs.setToolTipText("Save the current settings as a new protocol.");
        JButton replace = new JButton("Replace");
        replace.setToolTipText("Replace the currently selected protocol with the new values");
        pnl_txtFields.add(saveAs);
        pnl_txtFields.add(replace, "wrap");
        saveAs.addActionListener(this);
        replace.addActionListener(this);

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
        JPanel frequency = new JPanel();
        frequency.add(new JLabel("Frequency:"));
        frequency.add(tf_stimFrequency);
        frequency.add(new JLabel("(Hz)"));
        pnl_txtFields.add(frequency, "span 3, wrap");

        JPanel pnl_stimButtons = new JPanel();
        pnl_stimButtons.setBorder(lineBorder);
        stimButtonGroup.add(rb_insp);
        stimButtonGroup.add(rb_exp);
        stimButtonGroup.add(rb_cont);
        pnl_stimButtons.add(rb_insp);
        pnl_stimButtons.add(rb_exp);
        pnl_stimButtons.add(rb_cont);

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
        if (e.getActionCommand().equals("comboBoxChanged")) {
            String filename = protocol_list.getSelectedItem().toString();
            Properties protocol = Utilities.loadProtocol(filename);
            tf_startThreshold.setText(Float.toString(Float.parseFloat(protocol.getProperty("threshold"))));
            tf_numberOfSpikes.setText(Float.toString(Float.parseFloat(protocol.getProperty("stims"))));
            tf_stimValue.setText(Float.toString(Float.parseFloat(protocol.getProperty("peak"))));
            boolean ramp = protocol.getProperty("ramp").equals("true");
            cb_rampup.setSelected(ramp);
            tf_stimFrequency.setText(Float.toString(Float.parseFloat(protocol.getProperty("frequency"))));
            int stimType = Integer.parseInt(protocol.getProperty("stimType"));
            switch (stimType) {
                case 0: rb_cont.setSelected(true);break;
                case 1: rb_insp.setSelected(true);break;
                case 2: rb_exp.setSelected(true);break;
            }
        }
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
        if (e.getActionCommand().equals("Save as")) {
            String protocolName = JOptionPane.showInputDialog("Enter a name to save the current protocol as.");
            if (protocolName != null && !protocolName.isEmpty()) {
                if (Utilities.saveProtocol(protocolName, tf_startThreshold.getText(), tf_numberOfSpikes.getText(),
                        tf_stimValue.getText(), cb_rampup.isSelected(), tf_stimFrequency.getText(), getStimType(),
                        false)) {
                    protocol_list.addItem(protocolName);
                    JOptionPane.showMessageDialog(null, "File saved successfully",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else
                    JOptionPane.showMessageDialog(null, "The file already exists", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        if (e.getActionCommand().equals("Replace")) {
            if (Utilities.saveProtocol(protocol_list.getSelectedItem().toString(), tf_startThreshold.getText(),
                    tf_numberOfSpikes.getText(), tf_stimValue.getText(), cb_rampup.isSelected(),
                    tf_stimFrequency.getText(), getStimType(), true)) {
                JOptionPane.showMessageDialog(null, "File saved successfully",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
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

        stimParameters.setStim(getStimType());
        stimParameters.setRampUp(cb_rampup.isSelected());
        stimParameters.setNumberOfSpikes(Long.parseLong(tf_numberOfSpikes.getText()));
        stimParameters.setSpikeFrequency(Long.parseLong(tf_stimFrequency.getText()));

    }

    public JTextField getTf_participantID() {
        return tf_participantID;
    }

    private int getStimType() {
        int stimType = 0;
        if (rb_insp.isSelected()) stimType = 1;
        if (rb_exp.isSelected()) stimType = 2;
        return stimType;
    }
}
