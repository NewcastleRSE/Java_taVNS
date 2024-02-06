package uk.ac.ncl.tavns.controller;

import com.sun.jna.Pointer;
import kirkwood.nidaq.access.NiDaq;
import kirkwood.nidaq.access.NiDaqException;
import kirkwood.nidaq.jna.Nicaiu;

public class DigitalOutput implements Runnable {
    private static NiDaq daq = new NiDaq();

    public DigitalOutput(String outputDevice) {
        System.out.println("Init time series");
        try {
            writeDigitalOut(new byte[]{1, 1, 1, 1}, outputDevice);
        } catch (NiDaqException e) {
            System.out.println("Output device " + outputDevice + " failed");
//            throw new RuntimeException(e);
        }
    }


    /**
     * Write the specified data to the digital out lines.
     * @param data
     * @throws NiDaqException
     */
    public void writeDigitalOut(byte[] data, String outputDevice) throws NiDaqException {
        Pointer doTask = daq.createTask("Task");
        daq.createDOChan(doTask, outputDevice + "/port0/line0", "", Nicaiu.DAQmx_Val_ChanForAllLines);
        daq.startTask(doTask);
        daq.writeDigitalLines(doTask, 1, 1, 10, Nicaiu.DAQmx_Val_GroupByChannel, data);
        daq.stopTask(doTask);
        daq.clearTask(doTask);
        System.out.print("Output stopped and cleared.");
    }

    @Override
    public void run() {

    }
}
