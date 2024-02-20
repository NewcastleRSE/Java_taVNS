package uk.ac.ncl.tavns.controller;

import com.sun.jna.Pointer;
import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import kirkwood.nidaq.jna.Nicaiu;

import java.nio.DoubleBuffer;

public class AnalogueWrite extends Thread {
    private static NiDaq daq = new NiDaq();
    int minVal = 0;
    int maxVal = 5;
    Pointer doTask;
    double stimValue;

    /**
     * Constructor
     */
    public AnalogueWrite(String outputDevice, String outputChannel, String taskName, double stimValue) throws NiDaqException {
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

    public void run() {
        System.out.println("Run thread");
        try {
            daq.startTask(doTask);
            double[] fbb = {stimValue, stimValue};
            DoubleBuffer data = DoubleBuffer.wrap(fbb);
            daq.writeAnalogF64(doTask, 1, 1, 10, Nicaiu.DAQmx_Val_GroupByChannel,
                    data, 0);

        } catch (NiDaqException e) {
            e.printStackTrace();
        }
        try {
            System.out.println("Close all");
            daq.stopTask(doTask);
            daq.clearTask(doTask);
        } catch (NiDaqException e) {
            throw new RuntimeException(e);
        }
    }


}
