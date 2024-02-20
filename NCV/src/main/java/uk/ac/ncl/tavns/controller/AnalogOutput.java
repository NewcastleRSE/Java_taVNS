package uk.ac.ncl.tavns.controller;

import kirkwood.nidaq.access.NiDaq;
import org.jfree.data.time.TimeSeries;

public class AnalogOutput {

    private static NiDaq daq = new NiDaq();
    private final int numSampsPerChan;
    private boolean isRunning = true;
    private TimeSeries[] timeSeries;
    private String inputDevice;

    public AnalogOutput(int numberOfChannels, int numSampsPerChan, String inputDevice) {

        this.timeSeries = new TimeSeries[numberOfChannels];
        this.inputDevice = inputDevice;
        for (int i = 0; i < numberOfChannels; i++) {
            timeSeries[i] = new TimeSeries("Legend: " + i);
        }
        this.numSampsPerChan = numSampsPerChan;

    }
}
