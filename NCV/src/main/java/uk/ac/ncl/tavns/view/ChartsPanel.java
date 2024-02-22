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
import uk.ac.ncl.tavns.controller.AnalogueInput;
import uk.ac.ncl.tavns.controller.Utilities;

import javax.swing.*;
import java.util.Properties;

public class ChartsPanel extends JPanel {

    private static ButtonControlsPanel buttonControlsPanel;
    private Properties properties = Utilities.loadProperties();
    private ChartPanel[] chartPanel;

    /**
     * Panel containing charts for displaying analogue input traces
     * @param panelCollection
     * @param channels
     * @param analogueInput
     * @param digitalOutputDevice
     */
    public ChartsPanel(PanelCollection panelCollection, int channels, AnalogueInput analogueInput, String digitalOutputDevice) {
        super();
        panelCollection.setButtonControlsPanel(new ButtonControlsPanel(panelCollection, analogueInput, digitalOutputDevice));
        buttonControlsPanel = panelCollection.getButtonControlsPanel();
        TimeSeriesCollection[] dataset = new TimeSeriesCollection[channels];
        TimeSeries[] timeSeries = analogueInput.getTimeSeries();
        JFreeChart[] chart = new JFreeChart[channels];
        chartPanel = new ChartPanel[channels];
        MigLayout migLayout = new MigLayout("fillx", "[]rel[]", "[]10[]");
        setLayout(migLayout);
        add(buttonControlsPanel, "wrap");
        add(panelCollection.getStimulationConfiguration(), "wrap");

        for (int i = 0; i < channels; i++) {
            dataset[i] = new TimeSeriesCollection(timeSeries[i]);
            chart[i] = createChart(dataset[i], "Analogue Input " + i);
            chart[i].removeLegend();
            chartPanel[i] = new ChartPanel(chart[i]);
            add(chartPanel[i], "wrap");
            chartPanel[i].setPreferredSize(new java.awt.Dimension(Integer.parseInt(properties.getProperty("chart_width")),
                    Integer.parseInt(properties.getProperty("chart_height"))));
        }

        Thread t1 = new Thread(analogueInput);
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

    public static ButtonControlsPanel getButtonControlsPanel() {
        return buttonControlsPanel;
    }

    public static void setButtonControlsPanel(ButtonControlsPanel buttonControlsPanel) {
        ChartsPanel.buttonControlsPanel = buttonControlsPanel;
    }

    public ChartPanel[] getChartPanel() {
        return chartPanel;
    }

    public void setChartPanel(ChartPanel[] chartPanel) {
        this.chartPanel = chartPanel;
    }
}
