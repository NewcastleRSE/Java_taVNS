package uk.ac.ncl.tavns;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NCV extends ApplicationFrame implements ActionListener {

    private static final int channels = 2;
    private static TimeSeries[] series = new TimeSeries[channels];
    private static String[] legend = new String[channels];

    /**
     * Constructs a new application frame.
     *
     * @param title the frame title.
     */
    public NCV(String title, int channels) {
        super(title);
        final TimeSeriesCollection[] dataset = new TimeSeriesCollection[channels];
        final JFreeChart[] chart = new JFreeChart[channels];
        final ChartPanel[] chartPanel = new ChartPanel[channels];
        final JPanel content = new JPanel(new BorderLayout());
        legend = new String[channels];
        series = new TimeSeries[channels];
        content.setLayout(new GridLayout(channels, 1));
        for (int i = 0; i < channels; i++) {
            legend[i] = (legend[i] == null) ? "Legend " : legend[i];
            series[i] = new TimeSeries(legend[i] + " " + i);
            dataset[i] = new TimeSeriesCollection(series[i]);
            chart[i] = createChart(dataset[i], "Dataset " + i);
            chartPanel[i] = new ChartPanel(chart[i]);
            content.add(chartPanel[i]);
            chartPanel[i].setPreferredSize(new java.awt.Dimension(500, 270));
        }
        setContentPane(content);
        Thread t1 = new Thread(new MakeData(series[0]));
        t1.start();
    }

    private JFreeChart createChart(final XYDataset dataset, String title) {
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
                title,
                "Time",
                "Amplitude",
                dataset,
                true,
                true,
                false
        );
        final XYPlot plot = result.getXYPlot();
        ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setFixedAutoRange(60000.0);  // 60 seconds
        axis = plot.getRangeAxis();
        axis.setRange(0.0, 200.0);
        return result;
    }

    private static void runGUI() {
        final NCV ncv = new NCV("NIDAQ Channel Visualiser", channels);
        ncv.pack();
        RefineryUtilities.centerFrameOnScreen(ncv);
        ncv.setVisible(true);
    }

    public static void main(String[] args) {
        runGUI();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}