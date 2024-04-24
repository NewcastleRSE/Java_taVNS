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

    public double[] readAnalogueIn(int inputBufferSize, String inputDevice) {
        Pointer aiTask = null;

        try {
            ;
            aiTask = daq.createTask("AITask1");
            String physicalChannel = inputDevice + "/ai0:" + (timeSeries.length - 1);
            logger.debug("Physical channel: " + physicalChannel);
            logger.debug("AI task: " + aiTask.toString());
            try {
                daq.createAIVoltageChannel(aiTask, physicalChannel, "",
                        Nicaiu.DAQmx_Val_Cfg_Default, -10.0, 10.0, Nicaiu.DAQmx_Val_Volts,
                        null);
                try {
                    daq.cfgSampClkTiming(aiTask, "", 100.0, Nicaiu.DAQmx_Val_Rising, Nicaiu.DAQmx_Val_FiniteSamps,
                            numSamplesPerChan);
                    try {
                        daq.startTask(aiTask);
                        int read = 0;
                        double[] buffer = new double[inputBufferSize];

                        DoubleBuffer inputBuffer = DoubleBuffer.wrap(buffer);
                        IntBuffer samplesPerChannelRead = IntBuffer.wrap(new int[]{read});
                        try {
                            daq.readAnalogF64(aiTask, numSamplesPerChan, 100.0, Nicaiu.DAQmx_Val_GroupByChannel, inputBuffer,
                                    inputBufferSize, samplesPerChannelRead);
                            try {
                                daq.stopTask(aiTask);
                                try {
                                    daq.clearTask(aiTask);
                                } catch (NiDaqException e) {
                                    logger.debug("Can't clear task " + aiTask);
                                }
                                return buffer;
                            } catch (NiDaqException e) {
                                logger.debug("Can't stop task " + aiTask);
                            }
                        } catch (NiDaqException e) {
                            logger.debug("Can't read analogue");
                        }
                    } catch (NiDaqException e) {
                        logger.debug("Can't start AI task " + aiTask);
                    }
                } catch (NiDaqException e) {
                    logger.debug("Can' Configure Sample Clock Timing");;
                }
            } catch (NiDaqException e) {
                logger.debug("Can't create AI Voltage Channel");;
            }
            daq.clearTask(aiTask);
        } catch (NiDaqException e) {
            logger.debug("Can't create AI task " + aiTask);
            e.printStackTrace();
            throw new RuntimeException();
        }






        return null;

//        try {
//        } catch (NiDaqException e) {
//            logger.debug("Something's gone wrong");
//            try {
//                daq.stopTask(aiTask);
//                daq.clearTask(aiTask);
//                return null;
//            } catch (NiDaqException e2) {
//                e.printStackTrace();
//                // TODO
//            }
//            throw (e);
//        }
    }

    @Override
    public void run() {
        final Millisecond now = new Millisecond();
        logger.debug("Samples per channel: " + numSamplesPerChan);
        while (true) {
            int inputBufferSize = timeSeries.length * numSamplesPerChan *2;
            logger.trace("Input device: " + inputDevice);
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
