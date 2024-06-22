/**
 * https://jtablesaw.github.io/tablesaw/
 */
package uk.ac.ncl.tavns.controller;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.TimeSeriesPlot;

public class TestTableSaw {

    public static void main(String[] args) {
        Table table = Table.read().csv("andrew_2024-06-22_101447.csv");
        System.out.println(table.columnNames());
        Plot.show(TimeSeriesPlot.create("heading", table, "TimeStamp", "Breathing Belt (V)"));
    }
}
