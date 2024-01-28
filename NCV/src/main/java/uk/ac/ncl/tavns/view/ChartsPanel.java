package uk.ac.ncl.tavns.view;

import net.miginfocom.swing.MigLayout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import uk.ac.ncl.tavns.controller.MakeData;

import javax.swing.*;

public class ChartsPanel extends JPanel {

    private static ButtonControlsPanel controlsPanel;

    public ChartsPanel(int channels, MakeData makeDataThread) {
        super();
        controlsPanel = new ButtonControlsPanel(makeDataThread);
        TimeSeriesCollection[] dataset = new TimeSeriesCollection[channels];
        TimeSeries[] series = makeDataThread.getTimeSeries();
        JFreeChart[] chart = new JFreeChart[channels];
        ChartPanel[] chartPanel = new ChartPanel[channels];
        MigLayout migLayout = new MigLayout("fillx", "[]rel[]", "[]10[]");
        setLayout(migLayout);
        add(controlsPanel, "wrap");
        for (int i = 0; i < channels; i++) {
            dataset[i] = new TimeSeriesCollection(series[i]);
            chart[i] = createChart(dataset[i], "Analogue Input " + i);
            chart[i].removeLegend();
            chartPanel[i] = new ChartPanel(chart[i]);
            add(chartPanel[i], "wrap");
            chartPanel[i].setPreferredSize(new java.awt.Dimension(1000, 270));
        }
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
//        axis.setRange(0, 100);
        return result;
    }

}
