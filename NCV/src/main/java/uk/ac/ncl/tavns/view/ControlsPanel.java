package uk.ac.ncl.tavns.view;

import uk.ac.ncl.tavns.controller.MakeData;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControlsPanel extends JPanel implements ActionListener {

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

        startTrace.addActionListener(this);
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
