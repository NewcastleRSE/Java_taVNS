package uk.ac.ncl.tavns.controller;

import com.sun.jna.Pointer;
import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import kirkwood.nidaq.jna.Nicaiu;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class AnalogueThresholdWrite implements Runnable {
    private static NiDaq daq = new NiDaq();
    private int minVal = 0;
    private int maxVal = 5;
    private Pointer doTask;
    private double stimValue;
    private boolean running = false;
    private TimeSeriesCollection timeSeriesCollection;
    double stimThreshold;
    String outputDevice;
    String outputChannel;
    String taskName;

    /**
     *
     * @param outputDevice
     * @param outputChannel
     * @param taskName
     * @param stimValue
     * @param timeSeriesCollection
     * @param stimthreshold
     * @throws NiDaqException
     */
    public AnalogueThresholdWrite(String outputDevice, String outputChannel, String taskName, double stimValue,
                                  TimeSeriesCollection timeSeriesCollection, double stimthreshold) throws NiDaqException {
        this.timeSeriesCollection = timeSeriesCollection;
        this.stimValue = stimValue;
        this.stimThreshold = stimthreshold;
        this.outputChannel = outputChannel;
        this.outputDevice = outputDevice;
        this.taskName = taskName;
    }

    /**
     * Run thread
     */
    @Override
    public void run() {
        while (true) {
            try {
                //System.out.println("Run thread");
                // Keep thread running
                // Sleep for 10 millis otherwise start it doesn't go into the running loop ??!?!?!?
                Thread.sleep(1);
                // Start stimulating when running is true
                while (running) {
                    TimeSeries timeSeries = timeSeriesCollection.getSeries(0);
                    int itemCount = timeSeries.getItemCount();
                    Double datapoint = timeSeries.getDataItem(itemCount - 1).getValue().doubleValue();

                    if (datapoint > stimThreshold) {
                        daq.startTask(doTask);
                        daq.DAQmxWriteAnalogScalarF64(doTask, 1, 10, 0.6, 0);
//                        Thread.sleep(200);
                        long start = System.nanoTime();
                        long nanoseconds = 450000000;
                        while(start + nanoseconds >= System.nanoTime());
                        daq.stopTask(doTask);
                        double zero = 0D;
                        daq.DAQmxWriteAnalogScalarF64(doTask, 1, 10, zero, 0);
                        daq.stopTask(doTask);
//                        Thread.sleep(200);
                        while(start + nanoseconds >= System.nanoTime());

                    }
                }
            } catch (NiDaqException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }


    public void stop() {
        try {
            System.out.println("Close all");
            daq.clearTask(doTask);
        } catch (NiDaqException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Return whether thread is stimulating or not. If running=true, it should be stimulating
     *
     * @return
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Set running to true to start stimulating and false to stop.
     *
     * @param running
     */
    public void setRunning(boolean running) {
        if (running) {
            try {
                System.out.println("Initialise thread");
                String physicalChannel = outputDevice + "/" + outputChannel;
                System.out.println("Physical channel: " + physicalChannel);
                doTask = daq.createTask(taskName);
                System.out.println("Task name: " + taskName);
                daq.resetDevice(outputDevice);
                daq.createAOVoltageChannel(doTask, physicalChannel, "", minVal, maxVal,
                        Nicaiu.DAQmx_Val_Volts, null);
            } catch (NiDaqException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                daq.clearTask(doTask);
            } catch (NiDaqException e) {
                throw new RuntimeException(e);
            }
        }
        this.running = running;
    }

    public double getStimThreshold() {
        return stimThreshold;
    }

    public void setStimThreshold(double stimThreshold) {
        this.stimThreshold = stimThreshold;
    }
}
