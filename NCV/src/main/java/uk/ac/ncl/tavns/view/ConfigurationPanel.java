package uk.ac.ncl.tavns.view;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConfigurationPanel extends JPanel implements ActionListener {
    private double rangeMinimum = -1;
    private double rangeMaximum = 1;
    private JTextField tf_rangeMinimum = new JTextField(String.valueOf(rangeMinimum));
    private JTextField tf_rangeMaximum = new JTextField(String.valueOf(rangeMaximum));

    public ConfigurationPanel() {
        super();
        MigLayout migLayout = new MigLayout("fillx", "[]rel[]rel[]rel[]rel[]", "[]10[]");
        setLayout(migLayout);
        add(new Label("Initial Plot Ranges: "));
        add(new Label("Minimum"));
        add(tf_rangeMinimum);
        add(new Label("Maximum"));
        add(tf_rangeMaximum, "wrap");
        add(new Label("Samples per channel: "));
        add(new JTextField(8), "wrap");
        add(new JButton("Save"));
    }


    public double getRangeMinimum() {
        return rangeMinimum;
    }

    public void setRangeMinimum(double rangeMinimum) {
        rangeMinimum = rangeMinimum;
    }

    public double getRangeMaximum() {
        return rangeMaximum;
    }

    public void setRangeMaximum(double rangeMaximum) {
        rangeMaximum = rangeMaximum;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Save")) {

        }
    }
}
