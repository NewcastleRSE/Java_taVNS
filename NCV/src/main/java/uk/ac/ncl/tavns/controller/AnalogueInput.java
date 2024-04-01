package uk.ac.ncl.tavns.controller;

import com.sun.jna.Pointer;
import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import kirkwood.nidaq.jna.Nicaiu;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

public class AnalogueInput implements Runnable {
    final static Logger logger = LoggerFactory.getLogger(AnalogueInput.class);
    private static NiDaq daq = new NiDaq();
    private final int numSamplesPerChan;
    private boolean isRunning = true;
    private TimeSeries[] timeSeries;
    private String inputDevice;

    public AnalogueInput(int numberOfChannels, int numSamplesPerChan, String inputDevice) {

        this.timeSeries = new TimeSeries[numberOfChannels];
        this.inputDevice = inputDevice;
        for (int i = 0; i < numberOfChannels; i++) {
            timeSeries[i] = new TimeSeries("Legend: " + i);
        }
        this.numSamplesPerChan = numSamplesPerChan;

    }

    public double[] readAnalogueIn(int inputBufferSize, String inputDevice) throws NiDaqException {
        Pointer aiTask = null;
        try {
            aiTask = daq.createTask("AITask");

            String physicalChannel = inputDevice + "/ai0:" + (timeSeries.length - 1);
            daq.createAIVoltageChannel(aiTask, physicalChannel, "",
                    Nicaiu.DAQmx_Val_Cfg_Default, -10.0, 10.0, Nicaiu.DAQmx_Val_Volts,
                    null);
            daq.cfgSampClkTiming(aiTask, "", 100.0, Nicaiu.DAQmx_Val_Rising, Nicaiu.DAQmx_Val_FiniteSamps,
                    numSamplesPerChan);
            daq.startTask(aiTask);
            int read = 0;
            double[] buffer = new double[inputBufferSize];

            DoubleBuffer inputBuffer = DoubleBuffer.wrap(buffer);
            IntBuffer samplesPerChannelRead = IntBuffer.wrap(new int[]{read});
            daq.readAnalogF64(aiTask, numSamplesPerChan, 100.0, Nicaiu.DAQmx_Val_GroupByChannel, inputBuffer,
                    inputBufferSize, samplesPerChannelRead);

            daq.stopTask(aiTask);
            daq.clearTask(aiTask);
            return buffer;

        } catch (NiDaqException e) {
            logger.debug("Something's gone wrong");
            try {
                daq.stopTask(aiTask);
                daq.clearTask(aiTask);
                return null;
            } catch (NiDaqException e2) {
                e.printStackTrace();
                // TODO
            }
            throw (e);
        }
    }

    @Override
    public void run() {
        final Millisecond now = new Millisecond();
        System.out.println("Samples per channel: " + numSamplesPerChan);
        while (true) {
            try {
                int inputBufferSize = timeSeries.length * numSamplesPerChan *2;
                double[] data = readAnalogueIn(inputBufferSize, inputDevice);
                if (data != null) {
                    for (int i = 0; i < timeSeries.length; i++) {
                        int start = i * numSamplesPerChan;
                        int end = start + numSamplesPerChan;
                        if (isRunning)
                            timeSeries[i].addOrUpdate(new Millisecond(), mean(Arrays.copyOfRange(data, start, end)));
                        else
                            timeSeries[i].addOrUpdate(new Millisecond(), null);
                    }
                }
            } catch (NiDaqException e) {
                logger.debug("NidaqException:");
                e.printStackTrace();
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
