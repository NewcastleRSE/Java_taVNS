package uk.ac.ncl.tavns;

import net.miginfocom.swing.MigLayout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RefineryUtilities;
import uk.ac.ncl.tavns.view.ControlsPanel;
import uk.ac.ncl.tavns.controller.MakeData;

import javax.swing.*;
import java.awt.*;

/**
 * NiDAQ Channel Viewer
 */
public class NCV extends JFrame {

    private static final int channels = 3;
    private static TimeSeries[] series = new TimeSeries[channels];
    private static int numSampsPerChan = 8;
    private static String[] legend = new String[channels];
    private static MakeData makeDataThread;
    private static ControlsPanel controlsPanel = new ControlsPanel(makeDataThread);

    /**
     * Constructs a new application frame.
     *
     * @param title the frame title.
     */
    public NCV(String title, int channels) {
        super(title);
        final TimeSeriesCollection[] dataset = new TimeSeriesCollection[channels];
        final JTabbedPane tabbedPane = new JTabbedPane();
        final JFreeChart[] chart = new JFreeChart[channels];
        final ChartPanel[] chartPanel = new ChartPanel[channels];
        final JPanel content = new JPanel(new BorderLayout());
        legend = new String[channels];
        series = new TimeSeries[channels];
//        content.setLayout(new GridLayout(channels+1, 1));
        MigLayout migLayout = new MigLayout("fillx", "[]rel[]", "[]10[]");
        content.setLayout(migLayout);
        content.add(controlsPanel, "wrap");
        for (int i = 0; i < channels; i++) {
            legend[i] = (legend[i] == null) ? "Legend " : legend[i];
            series[i] = new TimeSeries(legend[i] + " " + i);
            dataset[i] = new TimeSeriesCollection(series[i]);
            chart[i] = createChart(dataset[i], "Analogue Input " + i);
            chartPanel[i] = new ChartPanel(chart[i]);
            content.add(chartPanel[i], "wrap");
            chartPanel[i].setPreferredSize(new java.awt.Dimension(1000, 270));
        }
        tabbedPane.add("Input Traces", content);
        tabbedPane.add("Configuration", new JPanel()); // add dummy panel for the moment
        setContentPane(tabbedPane);
        makeDataThread = new MakeData(series, numSampsPerChan);
        Thread t1 = new Thread(makeDataThread);
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
        axis.setRange(controlsPanel.getRangeMinimum(), controlsPanel.getRangeMaximum());
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

}