package uk.ac.ncl.tavns.view;

import javax.swing.*;
import java.awt.*;

public class ConfigurationPanel extends JPanel {
    private double rangeMinimum = 0;
    private double rangeMaximum = 100;
    private JTextField tf_rangeMinimum = new JTextField(String.valueOf(rangeMinimum));
    private JTextField tf_rangeMaximum = new JTextField(String.valueOf(rangeMaximum));

    public ConfigurationPanel() {
        super();
        add(new Label("Initial Plot Ranges"));
        add(new Label("Minimum"));
        add(tf_rangeMinimum);
        add(new Label("Maximum"));
        add(tf_rangeMaximum);
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

}
