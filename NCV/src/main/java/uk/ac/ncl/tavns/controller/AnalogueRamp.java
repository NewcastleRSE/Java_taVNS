package uk.ac.ncl.tavns.controller;

import com.sun.jna.Pointer;
import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import kirkwood.nidaq.jna.Nicaiu;

import java.nio.DoubleBuffer;

public class AnalogueRamp implements Runnable {
    private static NiDaq daq = new NiDaq();
    int minVal = 0;
    int maxVal = 5;
    int stims;
    int sleep;
    Pointer doTask;

    /**
     * Constructor
     */
    public AnalogueRamp(String outputDevice, String outputChannel, String taskName, int stims, int sleep) {
        try {
            System.out.println("Initialise thread");
            this.stims = stims;
            this.sleep = sleep;
            String physicalChannel = outputDevice + "/" + outputChannel;
            doTask = daq.createTask(taskName);
            daq.resetDevice(outputDevice);
            daq.createAOVoltageChannel(doTask, physicalChannel, "", minVal, maxVal,
                    Nicaiu.DAQmx_Val_Volts, null);
        } catch (NiDaqException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        System.out.println("Run thread");
        try {
            for (int i = 0; i < stims; i++) {

                daq.startTask(doTask);
                double[] fbb = {normalise(i, 0, stims, 0, 5)};
                DoubleBuffer data = DoubleBuffer.wrap(fbb);
                daq.writeAnalogF64(doTask, 1, 1, 10, Nicaiu.DAQmx_Val_GroupByChannel,
                        data, 0);
                Thread.sleep(sleep);
                daq.stopTask(doTask);
                double[] fbb2 = {0D, 0D};
                DoubleBuffer data2 = DoubleBuffer.wrap(fbb2);
                daq.writeAnalogF64(doTask, 1, 1, 10, Nicaiu.DAQmx_Val_GroupByChannel,
                        data2, 0);        // daq.write
                daq.stopTask(doTask);
                Thread.sleep(sleep);
            }
            daq.clearTask(doTask);
        } catch (NiDaqException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static double normalise(double val, double min, double max, double rangemin, double rangemax) {
        return rangemin + ((val - min) * (rangemax - rangemin) / (max - min));
    }

}
