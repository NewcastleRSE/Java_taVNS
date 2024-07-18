package uk.ac.ncl.tavns.controller;

import com.sun.jna.Pointer;
import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import kirkwood.nidaq.jna.Nicaiu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnalogueRamp implements Runnable {
    final static Logger logger = LoggerFactory.getLogger(AnalogueRamp.class);
    private static NiDaq daq = new NiDaq();
    int minVal = 0;
    int maxVal = 5;
    int stims;
    int frequency;
    Pointer doTask;

    /**
     * Constructor
     */
    public AnalogueRamp(String outputDevice, String outputChannel, String taskName, int stims, int frequency) {
        try {
            System.out.println("Initialise thread");
            this.stims = stims;
            this.frequency = frequency;
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
//        System.out.println("Run thread");
        try {
            for (int i = 0; i < stims; i++) {
                daq.startTask(doTask);
                double normalised = Utilities.normalise(i, 0, stims, 0, 5);
                daq.DAQmxWriteAnalogScalarF64(doTask,1, 5, normalised, 0);
                Thread.sleep(1000 / frequency);
                daq.stopTask(doTask);

            }
            daq.clearTask(doTask);
        } catch (NiDaqException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
