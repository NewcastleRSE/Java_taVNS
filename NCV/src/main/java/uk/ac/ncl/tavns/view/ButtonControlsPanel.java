package uk.ac.ncl.tavns.view;

import org.jfree.data.time.TimeSeries;
import uk.ac.ncl.tavns.controller.AnalogueInput;

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
    AnalogueInput analogueInput;

    public ButtonControlsPanel(AnalogueInput analogueInput) {
        this.analogueInput = analogueInput;
        setPreferredSize(new Dimension(1000,30));
        Border lineBorder = BorderFactory.createLineBorder(Color.black);
        setBorder(lineBorder);
        setLayout(new FlowLayout());
        startTrace.setBackground(Color.ORANGE);
        add(startTrace);
        add(save);
        add(reset);

        startTrace.addActionListener(this);
        save.addActionListener(this);
        reset.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getActionCommand());
        if (e.getActionCommand().equals("Stop")) {
            startTrace.setText("Start");
            startTrace.setBackground(new Color(1,106,180));
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
            int number_of_series = analogueInput.getTimeSeries().length;
            for (int n = 0; n < number_of_series; n++) {
                int itemCount = timeSeries[n].getItemCount();
                for (int i = 0; i < itemCount; i++) {
                    System.out.print(timeSeries[n].getTimePeriod(i) + "\t");
                    System.out.print(timeSeries[n].getDataItem(i).getValue() + ", ");
                }
                System.out.println();
            }
            analogueInput.setIsRunning(true);
        }
    }
}
