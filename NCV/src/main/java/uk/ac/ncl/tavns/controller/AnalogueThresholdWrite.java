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

    public AnalogueThresholdWrite(String outputDevice, String outputChannel, String taskName, double stimValue,
                                  TimeSeriesCollection timeSeriesCollection) throws NiDaqException {
        this.timeSeriesCollection = timeSeriesCollection;
        this.stimValue = stimValue;
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

    @Override
    public void run() {
        try {
            System.out.println("Run thread");
            while (running) {
                TimeSeries timeSeries = timeSeriesCollection.getSeries(0);
                int itemCount = timeSeries.getItemCount();
                System.out.println(doTask + ": " + stimValue + ", " + timeSeries.getDataItem(itemCount-1).getValue());

                daq.startTask(doTask);
                daq.DAQmxWriteAnalogScalarF64(doTask,1, 10, stimValue, 0);
                Thread.sleep(200);
                daq.stopTask(doTask);
                double zero = 0D;
                daq.DAQmxWriteAnalogScalarF64(doTask,1, 10, zero, 0);
                daq.stopTask(doTask);
                Thread.sleep(200);
            }
        } catch (NiDaqException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            System.out.println("Close all");
            daq.stopTask(doTask);
            daq.clearTask(doTask);
        } catch (NiDaqException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
