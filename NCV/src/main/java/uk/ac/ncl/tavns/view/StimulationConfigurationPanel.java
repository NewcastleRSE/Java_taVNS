package uk.ac.ncl.tavns.view;

import kirkwood.nidaq.access.NiDaqException;
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
    private JTextField tf_rampupDuration = new JTextField("3", 5);
    // Threshold for discontinuing stimulation
    private JTextField tf_stopThreshold = new JTextField("3",5);
    // Maximum duration of overall stimulation
    private JTextField tf_maxDuration = new JTextField("3", 5);
    private JButton startStim = new JButton("Start stimulation");
    private String outputDevice;
    private String outputChannel;
    private boolean stimInitialised = false;
    private PanelCollection panelCollection;
    private StimProtocols stimProtocols;
    public StimulationConfigurationPanel(PanelCollection panelCollection, String outputDevice, String outputChannel) {
        super();
        this.panelCollection = panelCollection;
        this.outputDevice = outputDevice;
        this.outputChannel = outputChannel;
        this.stimProtocols = panelCollection.getStimProtocols();
        setPreferredSize(new Dimension(1000, 30));
        Border lineBorder = BorderFactory.createLineBorder(Color.black);
        setBorder(lineBorder);
        setLayout(new FlowLayout());
        startStim.setBackground(Color.ORANGE);
        startStim.setForeground(Color.BLACK);
        add(new JLabel("Start threshold"));
        add(tf_startThreshold);
        add(new JLabel(""));
        add(cb_rampup);
        add(new JLabel("Rampup duration"));
        add(tf_rampupDuration);
        add(new JLabel("Stop threshold"));
        add(tf_stopThreshold);
        add(new JLabel("Maximum stimulation duration"));
        add(tf_maxDuration);
        add(startStim);

        startStim.addActionListener(this);


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Start stimulation")) {
            startStim.setText("Stop stimulation");
            startStim.setBackground(new Color(1, 106, 180));
            startStim.setForeground(Color.WHITE);
            double startThreshold = Double.parseDouble(tf_startThreshold.getText());
            try {
                if (!stimInitialised) {
                    if (stimProtocols == null)
                        stimProtocols = new StimProtocols(panelCollection.getChartsPanel());
                    stimInitialised = stimProtocols.thresholdStimInit(outputDevice, outputChannel, startThreshold,
                            Double.parseDouble(tf_startThreshold.getText()));
                }
                stimProtocols.thresholdStimStart();
                stimProtocols.thresholdStimStart();
            } catch (NiDaqException ex) {
                throw new RuntimeException(ex);
            }

        }
        if (e.getActionCommand().equals("Stop stimulation")) {
            double startThreshold = Double.parseDouble(tf_startThreshold.getText());
            startStim.setText("Start stimulation");
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

}
