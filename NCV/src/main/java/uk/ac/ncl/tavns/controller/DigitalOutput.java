package uk.ac.ncl.tavns.controller;

import com.sun.jna.Pointer;
import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import kirkwood.nidaq.jna.Nicaiu;

public class DigitalOutput  {
    private static NiDaq daq = new NiDaq();
    private static String outputDevice;

    public DigitalOutput(String outputDevice) {
        System.out.println("Initialise Digital Output");
        this.outputDevice = outputDevice;
    }


    /**
     * Write the specified data to the digital out lines.
     * @param data
     * @throws NiDaqException
     */
    public static void writeDigitalOut(byte[] data, String port) throws NiDaqException {
        Pointer doTask = daq.createTask("Task");
        daq.createDOChan(doTask, outputDevice + port, "", Nicaiu.DAQmx_Val_ChanForAllLines);
        daq.startTask(doTask);
        daq.writeDigitalLines(doTask, 1, 1, 10, Nicaiu.DAQmx_Val_GroupByChannel, data);
        daq.stopTask(doTask);
        daq.clearTask(doTask);
    }

}
