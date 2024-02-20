package uk.ac.ncl.tavns.view;

import eu.hansolo.custom.SteelCheckBox;
import kirkwood.nidaq.access.NiDaqException;
import org.jfree.data.time.TimeSeries;
import uk.ac.ncl.tavns.controller.AnalogueInput;
import uk.ac.ncl.tavns.controller.DigitalOutput;
import uk.ac.ncl.tavns.controller.Utilities;

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

    private JButton startTrace = new JButton("Stop");
    private JButton save = new JButton("Save");
    private JButton reset = new JButton("Reset");
    private JButton testRampStim = new JButton("Test Ramp Stim");

    // check this
    private JButton digOut = new JButton("Dig Out");
    private SteelCheckBox steelCheckBox = new SteelCheckBox();
    private AnalogueInput analogueInput;
    private DigitalOutput digitalOutput;
    private byte outputState = 0;
    // to here

    public ButtonControlsPanel(AnalogueInput analogueInput, DigitalOutput digitalOutput) {
        this.analogueInput = analogueInput;
        this.digitalOutput = digitalOutput;
        setPreferredSize(new Dimension(1000, 30));
        Border lineBorder = BorderFactory.createLineBorder(Color.black);
        setBorder(lineBorder);
        setLayout(new FlowLayout());
        startTrace.setBackground(Color.ORANGE);
        add(startTrace);
        add(steelCheckBox);
        add(digOut);
        add(save);
        add(reset);

        startTrace.addActionListener(this);
        digOut.addActionListener(this);
        save.addActionListener(this);
        reset.addActionListener(this);
        steelCheckBox.addItemListener(e -> {
            outputState = e.getStateChange() == 1 ? (byte)1 : (byte)0;
            System.out.println(outputState);
        });
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
            try {
                digitalOutput.writeDigitalOut(data, "/port0/line0");
            } catch (NiDaqException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println(e.getActionCommand());
        }
    }
}
