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
    private boolean running = true;
    private TimeSeriesCollection timeSeriesCollection;
    double stimThreshold;

    public AnalogueThresholdWrite(String outputDevice, String outputChannel, String taskName, double stimValue,
                                  TimeSeriesCollection timeSeriesCollection, double stimthreshold) throws NiDaqException {
        this.timeSeriesCollection = timeSeriesCollection;
        this.stimValue = stimValue;
        this.stimThreshold = stimthreshold;
        try {
            System.out.println("Initialise thread");
            String physicalChannel = outputDevice + "/" + outputChannel;
            System.out.println(physicalChannel);
            doTask = daq.createTask(taskName);
            daq.resetDevice(outputDevice);
            daq.createAOVoltageChannel(doTask, physicalChannel, "", minVal, maxVal,
                    Nicaiu.DAQmx_Val_Volts, null);
        } catch (NiDaqException e) {
            daq.stopTask(doTask);
            daq.clearTask(doTask);
            throw new RuntimeException(e);
        }
    }

    /**
     * Run thread
     */
    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("Run thread");
                // Keep thread running
                // Sleep for 10 millis otherwise start it doesn't go into the running loop ??!?!?!?
                Thread.sleep(10);
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
                        while(start + 450 >= System.nanoTime());
                        daq.stopTask(doTask);
                        double zero = 0D;
                        daq.DAQmxWriteAnalogScalarF64(doTask, 1, 10, zero, 0);
                        daq.stopTask(doTask);
//                        Thread.sleep(200);
                        while(start + 450 >= System.nanoTime());

                    }
                }
            } catch (NiDaqException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {
                System.out.println("Close all");
                daq.clearTask(doTask);
            } catch (NiDaqException e) {
                throw new RuntimeException(e);
            }
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
        this.running = running;
    }
}
