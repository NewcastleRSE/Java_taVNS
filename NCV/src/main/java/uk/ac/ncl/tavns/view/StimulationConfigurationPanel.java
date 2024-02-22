package uk.ac.ncl.tavns.view;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StimulationConfigurationPanel extends JPanel implements ActionListener {
    // Threshold for starting stimulation
    JTextField startThreshold = new JTextField("3",5);
    // Should we use a ramp up?
    JCheckBox rampup = new JCheckBox("Ramp up", true);
    // How long should the rampup take?
    JTextField rampupDuration = new JTextField("3", 5);
    // Threshold for discontinuing stimulation
    JTextField stopThreshold = new JTextField("3",5);
    // Maximum duration of overall stimulation
    JTextField maxDuration = new JTextField("3", 5);
    JButton startStim = new JButton("Start stimulation");
    public StimulationConfigurationPanel() {
        super();
        setPreferredSize(new Dimension(1000, 30));
        Border lineBorder = BorderFactory.createLineBorder(Color.black);
        setBorder(lineBorder);
        setLayout(new FlowLayout());
        startStim.setBackground(Color.ORANGE);
        startStim.setForeground(Color.BLACK);
        add(new JLabel("Start threshold"));
        add(startThreshold);
        add(new JLabel(""));
        add(rampup);
        add(new JLabel("Rampup duration"));
        add(rampupDuration);
        add(new JLabel("Stop threshold"));
        add(stopThreshold);
        add(new JLabel("Maximum stimulation duration"));
        add(maxDuration);
        add(startStim);

        startStim.addActionListener(this);


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Start stimulation")) {
            startStim.setText("Stop stimulation");
            startStim.setBackground(new Color(1, 106, 180));
            startStim.setForeground(Color.WHITE);
        }
        if (e.getActionCommand().equals("Stop stimulation")) {
            startStim.setText("Start stimulation");
            startStim.setBackground(Color.ORANGE);
            startStim.setForeground(Color.BLACK);
        }

    }
}
