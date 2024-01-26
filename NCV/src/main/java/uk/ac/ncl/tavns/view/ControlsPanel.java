package uk.ac.ncl.tavns.view;

import uk.ac.ncl.tavns.controller.MakeData;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControlsPanel extends JPanel implements ActionListener {

    private static double rangeMinimum = 0;
    private static double rangeMaximum = 100;
    private JTextField tf_rangeMinimum = new JTextField(String.valueOf(rangeMinimum));
    private JTextField tf_rangeMaximum = new JTextField(String.valueOf(rangeMaximum));
    private JButton startTrace = new JButton("Stop");
    private MakeData makeData;

    public ControlsPanel(MakeData makeData) {
        this.makeData = makeData;
        setPreferredSize(new Dimension(1000,30));
        Border blackline = BorderFactory.createLineBorder(Color.black);
        setBorder(blackline);
        setLayout(new FlowLayout());
        startTrace.setBackground(Color.ORANGE);
        add(startTrace);
        add(new Label("Minimum"));
        add(tf_rangeMinimum);
        add(new Label("Maximum"));
        add(tf_rangeMaximum);
        startTrace.addActionListener(this);
    }

    public static double getRangeMinimum() {
        return rangeMinimum;
    }

    public static void setRangeMinimum(double rangeMinimum) {
        ControlsPanel.rangeMinimum = rangeMinimum;
    }

    public static double getRangeMaximum() {
        return rangeMaximum;
    }

    public static void setRangeMaximum(double rangeMaximum) {
        ControlsPanel.rangeMaximum = rangeMaximum;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getActionCommand());
        if (e.getActionCommand().equals("Stop")) {
            startTrace.setText("Start");
            startTrace.setBackground(new Color(1,106,180));
            MakeData.setRunMe(false);
        } else if (e.getActionCommand().equals("Start")) {
            startTrace.setText("Stop");
            startTrace.setBackground(Color.ORANGE);
            MakeData.setRunMe(true);
        }
    }
}
