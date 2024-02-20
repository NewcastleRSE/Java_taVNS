package uk.ac.ncl.tavns.view;

import eu.hansolo.custom.SteelCheckBox;
import kirkwood.nidaq.access.NiDaqException;
import org.jfree.data.time.TimeSeries;
import uk.ac.ncl.tavns.controller.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Jannetta S. Steyn
 * Panel containing buttons to control graphs
 * MigLayout documentation: https://www.miglayout.com/mavensite/docs/QuickStart.pdf
 */
public class ButtonControlsPanel extends JPanel implements ActionListener {

    private final JButton startTrace = new JButton("Stop");

    private final AnalogueInput analogueInput;
    private final String digitalOutputDevice;
    private byte outputState = 0;
    private JTextField txt_stimValue = new JTextField("2.5");
    // to here

    public ButtonControlsPanel(AnalogueInput analogueInput, String digitalOutputDevice) {
        this.analogueInput = analogueInput;
        this.digitalOutputDevice = digitalOutputDevice;
        setPreferredSize(new Dimension(1000, 30));
        Border lineBorder = BorderFactory.createLineBorder(Color.black);
        setBorder(lineBorder);
        setLayout(new FlowLayout());
        startTrace.setBackground(Color.ORANGE);
        add(startTrace);
        SteelCheckBox steelCheckBox = new SteelCheckBox();
        add(steelCheckBox);
        // check this
        JButton digOut = new JButton("Dig Out");
        add(digOut);
        JButton testRampStim = new JButton("Test Ramp Stim");
        add(testRampStim);
        add(txt_stimValue);
        JButton stim = new JButton("Analogue Stim");
        add(stim);
        JButton save = new JButton("Save");
        add(save);
        JButton reset = new JButton("Reset");
        add(reset);

        startTrace.addActionListener(this);
        digOut.addActionListener(this);
        save.addActionListener(this);
        reset.addActionListener(this);
        steelCheckBox.addItemListener(e -> {
            outputState = e.getStateChange() == 1 ? (byte)1 : (byte)0;
            System.out.println(outputState);
        });
        testRampStim.addActionListener(this);
        stim.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Stop")) {
            startTrace.setText("Start");
            startTrace.setBackground(new Color(1, 106, 180));
            analogueInput.setIsRunning(false);
        } else if (e.getActionCommand().equals("Start")) {
            startTrace.setText("Stop");
            startTrace.setBackground(Color.ORANGE);
            analogueInput.setIsRunning(true);
        } else if (e.getActionCommand().equals("Reset")) {
            // clear graphs
            // reset timeseries
            int number_of_series = analogueInput.getTimeSeries().length;
            TimeSeries[] timeSeries = analogueInput.getTimeSeries();
            analogueInput.setIsRunning(false);
            for (int n = 0; n < number_of_series; n++) {
                timeSeries[n].delete(0, timeSeries[n].getItemCount() - 1);
            }
            analogueInput.setIsRunning(true);
        } else if (e.getActionCommand().equals("Save")) {
            TimeSeries[] timeSeries = analogueInput.getTimeSeries();
            analogueInput.setIsRunning(false);
            Utilities.saveData(timeSeries);
            analogueInput.setIsRunning(true);
        } else if (e.getActionCommand().equals("Dig Out")) {
            byte[] data = {outputState, outputState};
            DigitalWrite digitalOutput = new DigitalWrite(digitalOutputDevice, data, "/port0/line0");
            digitalOutput.start();
        } else if (e.getActionCommand().equals("Test Ramp Stim")) {
            AnalogueRamp analogueRamp = new AnalogueRamp(digitalOutputDevice, "ao1",
                    "AOTask", 10, 200);
            analogueRamp.start();
        } else if (e.getActionCommand().equals("Analogue Stim")) {
            String sval = txt_stimValue.getText();
            double stimValue = (sval.equals("") || sval==null)?0:Double.parseDouble(txt_stimValue.getText());
            AnalogueWrite analogueWrite = null;
            try {
                analogueWrite = new AnalogueWrite(digitalOutputDevice, "ao1",
                        "AOTask", stimValue);
                analogueWrite.start();
            } catch (NiDaqException ex) {
                if (ex.toString().equals("DAQmxErrorInvalidAODataWrite")) {

                }
                throw new RuntimeException(ex);
            }
        } else {
            System.out.println(e.getActionCommand());
        }
    }
}
