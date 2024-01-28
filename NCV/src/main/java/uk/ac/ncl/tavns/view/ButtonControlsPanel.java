package uk.ac.ncl.tavns.view;

import uk.ac.ncl.tavns.controller.MakeData;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonControlsPanel extends JPanel implements ActionListener {

    private JButton startTrace = new JButton("Stop");
    private JButton save = new JButton("Save");
    private JButton reset = new JButton("Reset");
    MakeData makeData;

    public ButtonControlsPanel(MakeData makeData) {
        this.makeData = makeData;
        setPreferredSize(new Dimension(1000,30));
        Border lineBorder = BorderFactory.createLineBorder(Color.black);
        setBorder(lineBorder);
        setLayout(new FlowLayout());
        startTrace.setBackground(Color.ORANGE);
        add(startTrace);
        add(save);
        add(reset);

        startTrace.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getActionCommand());
        if (e.getActionCommand().equals("Stop")) {
            startTrace.setText("Start");
            startTrace.setBackground(new Color(1,106,180));
            makeData.setIsRunning(false);
        } else if (e.getActionCommand().equals("Start")) {
            startTrace.setText("Stop");
            startTrace.setBackground(Color.ORANGE);
            makeData.setIsRunning(true);
        }
    }
}
