package uk.ac.ncl.tavns.controller;

import com.sun.jna.Pointer;
import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import kirkwood.nidaq.jna.Nicaiu;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

public class AnalogueInput implements Runnable {
    static double lastValue = 100.0;
    static final double factor = 0.90 + 0.2 * Math.random();
    private static NiDaq daq = new NiDaq();
    private final int numSampsPerChan;
    private boolean isRunning = true;
    private TimeSeries[] timeSeries;
    private String device;

    public AnalogueInput(int numberOfChannels, int numSampsPerChan, String device) {

        this.timeSeries = new TimeSeries[numberOfChannels];
        this.device = device;
        for (int i = 0; i < numberOfChannels; i++) {
            timeSeries[i] = new TimeSeries("Legend: " + i);
        }
        this.numSampsPerChan = numSampsPerChan;

    }

    public double[] readAnalogueIn(int inputBufferSize, String device) throws NiDaqException {
        Pointer aiTask = null;
        try {
            aiTask = daq.createTask("AITask");

            String physicalChannel = device + "/ai0:" + (timeSeries.length - 1);
            daq.createAIVoltageChannel(aiTask, physicalChannel, "",
                    Nicaiu.DAQmx_Val_Cfg_Default, -10.0, 10.0, Nicaiu.DAQmx_Val_Volts,
                    null);
            daq.cfgSampClkTiming(aiTask, "", 100.0, Nicaiu.DAQmx_Val_Rising, Nicaiu.DAQmx_Val_FiniteSamps,
                    numSampsPerChan);
            daq.startTask(aiTask);
            int read = 0;
            double[] buffer = new double[inputBufferSize];

            DoubleBuffer inputBuffer = DoubleBuffer.wrap(buffer);
            IntBuffer samplesPerChannelRead = IntBuffer.wrap(new int[]{read});
            daq.readAnalogF64(aiTask, numSampsPerChan, 100.0, Nicaiu.DAQmx_Val_GroupByChannel, inputBuffer,
                    inputBufferSize, samplesPerChannelRead);

            daq.stopTask(aiTask);
            daq.clearTask(aiTask);
            return buffer;

        } catch (NiDaqException e) {
            try {
                daq.stopTask(aiTask);
                daq.clearTask(aiTask);
                return null;
            } catch (NiDaqException e2) {
            }
            throw (e);
        }
    }

    @Override
    public void run() {
        final Millisecond now = new Millisecond();
        System.out.println("Samples per channel: " + numSampsPerChan);
        while (true) {
            try {
                int inputBufferSize = timeSeries.length * numSampsPerChan;
                double[] data = readAnalogueIn(inputBufferSize, device);
                if (data != null) {
                    for (int i = 0; i < timeSeries.length; i++) {
                        int start = i * numSampsPerChan;
                        int end = start + numSampsPerChan;
                        if (isRunning)
                            timeSeries[i].add(new Millisecond(), mean(Arrays.copyOfRange(data, start, end)));
                        else
                            timeSeries[i].add(new Millisecond(), null);
                    }
                }
            } catch (NiDaqException e) {
                // System.out.println("ERROR:\n" + e.getMessage());
                // ToDo
            }

        }
    }

    private double mean(double[] data) {
        return Arrays.stream(data).average().orElse(Double.NaN);

    }

    public synchronized boolean isIsRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public TimeSeries[] getTimeSeries() {
        return timeSeries;
    }

    public void setTimeSeries(TimeSeries[] timeSeries) {
        this.timeSeries = timeSeries;
    }
}
